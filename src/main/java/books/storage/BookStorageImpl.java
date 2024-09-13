package books.storage;

import books.model.Book;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookStorageImpl implements BookStorage {
    private static final String filePath = "src/main/resources/books.csv";
    private File file;
    private final CsvMapper csvMapper = new CsvMapper();
    private final CsvSchema schema = csvMapper.schemaFor(Book.class).withHeader();


    @PostConstruct
    public void init() throws IOException {
        file = new File(filePath);
        // Проверяем, существует ли файл
        if (!file.exists()) {
            // Если не существует, создаем файл с заголовками
            createCSVFileWithHeaders();
        }
    }

    @Override
    public void add(Book book) throws IOException {
        List<Book> books = findAll();
        books.add(book);
        csvMapper.writer(schema).writeValue(file, books);
    }

    @Override
    public Optional<Book> get(Integer id) {
        List<Book> books = findAll();
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    @Override
    public void update(Book updatedBook) throws IOException {
        List<Book> books = findAll();
        List<Book> updatedBooks = books.stream()
                .map(book -> book.getId().equals(updatedBook.getId()) ? updatedBook : book)
                .collect(Collectors.toList());
        csvMapper.writer(schema).writeValue(file, updatedBooks);

    }

    @Override
    public void delete(Integer id) throws IOException {
        List<Book> books = findAll();
        books = books.stream()
                .filter(book -> !book.getId().equals(id))
                .collect(Collectors.toList());
        csvMapper.writer(schema).writeValue(file, books);

    }

    @Override
    public List<Book> findAll() {
        try (MappingIterator<Book> bookIter = csvMapper.readerFor(Book.class)
                .with(schema)
                .readValues(file)) {
            return bookIter.readAll();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private void createCSVFileWithHeaders() throws IOException {
        // Пишем пустой список книг
        csvMapper.writer(schema).writeValue(new File(filePath), Collections.emptyList());
    }
}
