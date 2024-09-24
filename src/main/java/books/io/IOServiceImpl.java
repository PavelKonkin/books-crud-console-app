package books.io;

import books.model.dto.BookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

@Component
public class IOServiceImpl implements IOService {
    private final Scanner scanner = new Scanner(System.in);
    private final MessageSource messageSource;
    private Locale currentLocale;

    private static final Pattern MAIN_MENU_REGEX_FILTER = Pattern
            .compile("\\b[1-4]\\b|\\bвыход\\b|\\bexit\\b",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern STRING_REGEX_FILTER = Pattern.compile("^.{2,}$");
    private static final Pattern INTEGER_REGEX_FILTER = Pattern.compile("^[1-9]\\d*$");
    private static final Pattern TWO_OPTIONS_MENU_REGEX_FILTER = Pattern.compile("\\b[1-2]\\b");
    private static final Pattern LOCALE_REGEX_FILTER
            = Pattern.compile("\\ben\\b|\\bru\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Autowired
    public IOServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @Override
    public String getMainMenuChoice() {
        printMainMenu();
        return getUserInput(MAIN_MENU_REGEX_FILTER,
                messageSource.getMessage("mainMenuIncorrectInputMessage",
                        null, currentLocale), "");
    }

    @Override
    public BookDto getBookCreationData() {
        String title = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookCreationTitleMessage",
                        null, currentLocale));
        String author = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookCreationAuthorMessage",
                        null, currentLocale));
        String description = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookCreationDescriptionMessage",
                        null, currentLocale));

        return BookDto.builder()
                .title(title)
                .author(author)
                .description(description)
                .build();
    }

    @Override
    public Integer getBookId() {
        Integer id = null;
        System.out.println(messageSource.getMessage("optionMenuTitle",
                null, currentLocale));
        System.out.println(messageSource.getMessage("getBookByIdMenuEnterIdText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("getBookByIdMenuGoBackText",
                null, currentLocale));
        String input = getUserInput(TWO_OPTIONS_MENU_REGEX_FILTER,
                messageSource.getMessage("getBookByIdMenuIncorrectInputMessage",
                        null, currentLocale),
                "");
        if (input.equals("1")) {
            id = Integer.parseInt(getUserInput(INTEGER_REGEX_FILTER,
                    messageSource.getMessage("integerIncorrectMessage",
                            null, currentLocale),
                    messageSource.getMessage("getBookByIdMenuEnterBookIdMessage",
                            null, currentLocale)));
        }
        return id;
    }

    @Override
    public BookDto getBookUpdateData(BookDto bookDto) {
        System.out.print(messageSource.getMessage("bookUpdateCurrentTitleMessage",
                null, currentLocale));
        System.out.println(bookDto.getTitle());
        String title = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookUpdateEnterNewTitleMessage",
                        null, currentLocale));
        System.out.print(messageSource.getMessage("bookUpdateCurrentAuthorMessage",
                null, currentLocale));
        System.out.println(bookDto.getAuthor());
        String author = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookUpdateEnterNewAuthorMessage",
                        null, currentLocale));
        System.out.print(messageSource.getMessage("bookUpdateCurrentDescriptionMessage",
                null, currentLocale));
        System.out.println(bookDto.getDescription());
        String description = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookUpdateEnterNewDescriptionMessage",
                        null, currentLocale));

        return BookDto.builder()
                .id(bookDto.getId())
                .title(title)
                .author(author)
                .description(description)
                .build();
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void printBooksList(List<BookDto> books) {
        System.out.println(messageSource.getMessage("bookListTitle",
                null, currentLocale));
        for (BookDto book : books) {
            System.out.print(messageSource.getMessage("bookListBookId",
                    null, currentLocale));
            System.out.println(book.getId());
            System.out.print(messageSource.getMessage("bookListBookTitle",
                    null, currentLocale));
            System.out.println(book.getTitle());
            System.out.print(messageSource.getMessage("bookListBookAuthor",
                    null, currentLocale));
            System.out.println(book.getAuthor());
            System.out.print(messageSource.getMessage("bookListBookDescription",
                    null, currentLocale));
            System.out.println(book.getDescription());
            System.out.println(messageSource.getMessage("bookListBookSeparator",
                    null, currentLocale));
        }
    }

    @Override
    public void setLocale() {
        currentLocale = Locale.forLanguageTag(getUserInput(LOCALE_REGEX_FILTER,
                messageSource.getMessage("invalidEnterLocaleMessage", null, null),
                messageSource.getMessage("enterLocaleMessage", null, null)));

    }

    @Override
    public void showIvalidIdMessage() {
        System.out.println(messageSource.getMessage("invalidBookIdMessage", null, currentLocale));
    }

    private void printMainMenu() {
        System.out.println(messageSource.getMessage("optionMenuTitle",
                null, currentLocale));
        System.out.println(messageSource.getMessage("mainMenuCreateBookOptionText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("mainMenuUpdateBookOptionText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("mainMenuDeleteBookOptionText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("mainMenuPrintAllStoredBooksOptionText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("mainMenuExitProgramOptionText",
                null, currentLocale));
    }

    private String getUserInput(Pattern regexFilter,
                                String incorrectInputMessage, String inputMessage) {
        System.out.print(inputMessage);
        String userInput = scanner.nextLine();

        while (!regexFilter.matcher(userInput).find()) {
            System.out.println(incorrectInputMessage);
            System.out.print(inputMessage);
            userInput = scanner.nextLine();
        }

        return userInput;
    }
}
