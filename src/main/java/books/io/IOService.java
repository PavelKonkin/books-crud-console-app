package books.io;

import books.model.dto.BookDto;

import java.util.List;

public interface IOService {
    String getMainMenuChoice();

    BookDto getBookCreationData();

    Integer getBookId();

    BookDto getBookUpdateData(BookDto bookDto);

    void showMessage(String message);

    void printBooksList(List<BookDto> books);

    void setLocale();

    void showIvalidIdMessage();
}
