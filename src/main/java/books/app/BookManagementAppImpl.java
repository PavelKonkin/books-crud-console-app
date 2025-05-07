package books.app;

import books.io.IOService;
import books.model.dto.BookDto;
import books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class BookManagementAppImpl implements BookManagementApp {
    private final BookService bookService;
    private final IOService ioService;

    @Autowired
    public BookManagementAppImpl(BookService bookService, IOService ioService) {
        this.bookService = bookService;
        this.ioService = ioService;
    }

    @Override
    public void run() throws IOException {
        while (true) {
            String userChoice = ioService.getMainMenuChoice();
            if (!processChoice(userChoice)) {
                break;
            }
        }
    }

    private boolean processChoice(String userChoice) throws IOException {
        Integer bookId;
        BookDto bookDto;
        switch (userChoice.toLowerCase()) {
            case "1":
                bookDto = ioService.getBookCreationData();
                bookService.create(bookDto);
                break;
            case "2":
                bookId = ioService.getBookId();
                if (bookId != null) {
                    bookDto = bookService.get(bookId);
                    if (bookDto != null) {
                        bookDto = ioService.getBookUpdateData(bookDto);
                        bookService.update(bookDto);
                    } else {
                        ioService.showMessage("Книги с таким id не существует");
                    }
                }
                break;
            case "3":
                bookId = ioService.getBookId();
                bookService.delete(bookId);
                break;
            case "4":
                List<BookDto> books = bookService.getAllBooks();
                ioService.printBooksList(books);
                break;
            case "выход":
                return false;
        }
        return true;
    }
}
