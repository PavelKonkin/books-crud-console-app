package com.books.file.repository;

import com.books.exception.NotFoundException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Repository
public class FileRepositoryImpl implements FileRepository {
    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final MessageSource messageSource;


    @Autowired
    public FileRepositoryImpl(GridFsTemplate gridFsTemplate, MongoTemplate mongoTemplate,
                           MessageSource messageSource) {
        this.gridFsTemplate = gridFsTemplate;
        MongoDatabase database = mongoTemplate.getDb(); // Получаем базу данных из MongoTemplate
        this.gridFSBucket = GridFSBuckets.create(database);
        this.messageSource = messageSource;
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        DBObject metadata = new BasicDBObject();
        String decodedFilename = URLDecoder.decode(file.getOriginalFilename(), StandardCharsets.UTF_8);
        metadata.put("filename", decodedFilename);

        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(), decodedFilename, file.getContentType(), metadata);
        return fileId.toString();
    }

    @Override
    public GridFSFile getFile(String id) {
        return gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
    }

    @Override
    public void downloadFile(String id, HttpServletResponse response) throws IOException {
        GridFSFile gridFSFile = getFile(id);
        if (gridFSFile == null) {
            throw new NotFoundException(messageSource
                    .getMessage("fileNotFound", null, LocaleContextHolder.getLocale()));
        }

        // Получаем метаданные и устанавливаем content-type
        assert gridFSFile.getMetadata() != null;
        response.setContentType(gridFSFile.getMetadata().get("_contentType").toString());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + URLEncoder.encode(gridFSFile.getFilename(), StandardCharsets.UTF_8) + "\"");

        // Создаем поток для скачивания файла из GridFS
        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[8192]; // Используем буфер для чтения файла
            int bytesRead;
            while ((bytesRead = downloadStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead); // Записываем данные в ответ
            }

            os.flush(); // Убедимся, что все данные отправлены
        } catch (IOException e) {
            throw new RuntimeException(messageSource
                    .getMessage("errorDownloadingFile", null, LocaleContextHolder.getLocale()), e);
        }
    }
}
