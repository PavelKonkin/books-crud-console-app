package books.service;

import books.exception.NotFoundException;
import books.mapper.BookMapper;
import books.model.Book;
import books.model.dto.BookDto;
import books.storage.BookRepository;
import books.storage.FileStorage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final FileStorage fileStorage;
    private final MessageSource messageSource;


    @Autowired
    public BookServiceImpl(BookMapper bookMapper, BookRepository bookRepository,
                           FileStorage fileStorage, MessageSource messageSource) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.fileStorage = fileStorage;
        this.messageSource = messageSource;
    }

    @Override
    public void create(BookDto bookDto) {
        Book book = bookMapper.convertBookDto(bookDto);
        bookRepository.save(book);
    }

    @Override
    public BookDto get(Integer id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(bookMapper::convertBook).orElse(null);
    }

    @Override
    public void update(BookDto bookDto) {
        bookRepository.save(bookMapper.convertBookDto(bookDto));
    }

    @Override
    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::convertBook)
                .collect(Collectors.toList());
    }

    @Override
    public void downloadImage(int id, HttpServletResponse response) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(messageSource
                .getMessage("bookNotFound", null, LocaleContextHolder.getLocale())));
        try {
            fileStorage.downloadFile(book.getImageId(), response);
        } catch (IOException e) {
            throw new RuntimeException(messageSource
                    .getMessage("errorDownloadingFile", null, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public ResponseEntity<String> uploadImage(int id, MultipartFile file) {
        try {
            Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(messageSource
                    .getMessage("bookNotFound", null, LocaleContextHolder.getLocale())));
            String imageId = fileStorage.storeFile(file);
            book.setImageId(imageId);
            bookRepository.save(book);
            return ResponseEntity.ok(messageSource
                    .getMessage("imageUploadSuccess", null, LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource
                    .getMessage("imageUploadError", null, LocaleContextHolder.getLocale()));
        }
    }
}
