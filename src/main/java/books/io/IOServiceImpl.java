package books.io;

import books.model.dto.AuthorDto;
import books.model.dto.BookDto;
import books.model.dto.GenreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        List<AuthorDto> authors = getAuthorsList();
        List<GenreDto> genres = getGenresList();
        String description = getUserInput(STRING_REGEX_FILTER,
                messageSource.getMessage("stringIncorrectMessage",
                        null, currentLocale),
                messageSource.getMessage("bookCreationDescriptionMessage",
                        null, currentLocale));

        return BookDto.builder()
                .title(title)
                .authors(authors)
                .genres(genres)
                .description(description)
                .build();
    }

    private List<AuthorDto> getAuthorsList() {
        List<AuthorDto> authors = new ArrayList<>();
        while (true) {
            printAuthorsInputMenu();

            String userInput = getUserInput(TWO_OPTIONS_MENU_REGEX_FILTER,
                    messageSource.getMessage("twoOptionsMenuIncorrectInputMessage",
                            null, currentLocale), "");

            if (userInput.equals("1")) {
                String authorName = getUserInput(STRING_REGEX_FILTER,
                        messageSource.getMessage("stringIncorrectMessage",
                                null, currentLocale),
                        messageSource.getMessage("enterAuthorName",
                                null, currentLocale));
                AuthorDto authorDto = AuthorDto.builder()
                        .name(authorName)
                        .build();
                authors.add(authorDto);
            } else if (userInput.equals("2") && !authors.isEmpty()) {
                break;
            } else {
                System.out.println(messageSource.getMessage("authorsEmptyAlert",
                        null, currentLocale));
            }
        }
        return authors;
    }

    private void printAuthorsInputMenu() {
        System.out.println(messageSource.getMessage("authorsMenuTitle",
                null, currentLocale));
        System.out.println(messageSource.getMessage("authorsMenuAddAuthorText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("menuContinueText",
                null, currentLocale));
    }

    private List<GenreDto> getGenresList() {
        List<GenreDto> genres = new ArrayList<>();
        while (true) {
            printGenresInputMenu();

            String userInput = getUserInput(TWO_OPTIONS_MENU_REGEX_FILTER,
                    messageSource.getMessage("twoOptionsMenuIncorrectInputMessage",
                            null, currentLocale), "");

            if (userInput.equals("1")) {
                String genreTitle = getUserInput(STRING_REGEX_FILTER,
                        messageSource.getMessage("stringIncorrectMessage",
                                null, currentLocale),
                        messageSource.getMessage("enterGenreName",
                                null, currentLocale));
                GenreDto genreDto = GenreDto.builder()
                        .title(genreTitle)
                        .build();
                genres.add(genreDto);
            } else if (userInput.equals("2") && !genres.isEmpty()) {
                break;
            } else {
                System.out.println(messageSource.getMessage("genresEmptyAlert",
                        null, currentLocale));
            }
        }
        return genres;
    }

    private void printGenresInputMenu() {
        System.out.println(messageSource.getMessage("genresMenuTitle",
                null, currentLocale));
        System.out.println(messageSource.getMessage("genresMenuAddGenreText",
                null, currentLocale));
        System.out.println(messageSource.getMessage("menuContinueText",
                null, currentLocale));
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
                messageSource.getMessage("twoOptionsMenuIncorrectInputMessage",
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
        System.out.println(bookDto.getAuthors());
        List<AuthorDto> authors = getAuthorsList();

        System.out.print(messageSource.getMessage("bookUpdateCurrentGenresMessage",
                null, currentLocale));
        System.out.println(bookDto.getGenres());
        List<GenreDto> genres = getGenresList();

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
                .authors(authors)
                .genres(genres)
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
            System.out.println(book.getAuthors());
            System.out.print(messageSource.getMessage("bookListBookGenres",
                    null, currentLocale));
            System.out.println(book.getGenres());
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
    public void showInvalidIdMessage() {
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
