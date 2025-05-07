package com.books.file.repository;

import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileRepository {
    String storeFile(MultipartFile file) throws IOException;

    GridFSFile getFile(String id);

    void downloadFile(String id, HttpServletResponse response) throws IOException;

    void deleteFile(String fileId);
}
