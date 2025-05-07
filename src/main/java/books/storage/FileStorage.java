package books.storage;

import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {
    public String storeFile(MultipartFile file) throws IOException;

    public GridFSFile getFile(String id);

    public void downloadFile(String id, HttpServletResponse response) throws IOException;
}
