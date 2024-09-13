package books.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class BookIdGenerator {
    private static final Path ID_FILE_PATH = Paths.get("src/main/resources/lastId.txt");
    private int currentId;

    public BookIdGenerator() {
        // Загрузка последнего ID при инициализации
        loadLastId();
    }

    public int generateId() {
        currentId++;
        saveCurrentId();
        return currentId;
    }

    private void loadLastId() {
        if (Files.exists(ID_FILE_PATH)) {
            try {
                String lastId = Files.readString(ID_FILE_PATH);
                currentId = Integer.parseInt(lastId);
            } catch (IOException | NumberFormatException e) {
                // Если файл пуст или ID невалиден, начинаем с 0
                currentId = 0;
            }
        } else {
            currentId = 0;
        }
    }

    private void saveCurrentId() {
        try {
            Files.writeString(ID_FILE_PATH, String.valueOf(currentId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
