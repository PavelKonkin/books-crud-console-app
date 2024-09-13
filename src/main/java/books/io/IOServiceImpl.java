package books.io;

import books.model.dto.BookDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@Component
public class IOServiceImpl implements IOService {
    private final Scanner scanner = new Scanner(System.in);

    private static final Pattern MAIN_MENU_REGEX_FILTER = Pattern
            .compile("\\b[1-4]\\b|\\bвыход\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern STRING_REGEX_FILTER = Pattern.compile("^.{2,}$");
    private static final Pattern INTEGER_REGEX_FILTER = Pattern.compile("^[1-9]\\d*$");
    private static final String STRING_INCORRECT_MESSAGE
            = "Длина должна быть минимум 2 символа";
    private static final String INTEGER_INCORRECT_MESSAGE
            = "id книги должно быть целым числом больше 0";
    private static final Pattern TWO_OPTIONS_MENU_REGEX_FILTER = Pattern.compile("\\b[1-2]\\b");




    @Override
    public String getMainMenuChoice() {
        printMainMenu();
        return getUserInput(MAIN_MENU_REGEX_FILTER,
                "Введите целое число от 1 до 4, " +
                        "для окончания работы с программой введите Выход", "");
    }

    @Override
    public BookDto getBookCreationData() {
        String title = getUserInput(STRING_REGEX_FILTER,
                STRING_INCORRECT_MESSAGE,
                "Введите название книги: ");
        String author = getUserInput(STRING_REGEX_FILTER,
                STRING_INCORRECT_MESSAGE,
                "Введите автора книги: ");
        String description = getUserInput(STRING_REGEX_FILTER,
                STRING_INCORRECT_MESSAGE,
                "Введите краткое описание книги: ");

        return BookDto.builder()
                .title(title)
                .author(author)
                .description(description)
                .build();
    }

    @Override
    public Integer getBookId() {
        Integer id = null;
        System.out.println("Выберите действие:");
        System.out.println("1 - Ввести id книги");
        System.out.println("2 - Вернуться в основное меню");
        String input = getUserInput(TWO_OPTIONS_MENU_REGEX_FILTER,
                "Введите 1 или 2", "");
        if (input.equals("1")) {
            id = Integer.parseInt(getUserInput(INTEGER_REGEX_FILTER,
                    INTEGER_INCORRECT_MESSAGE, "Введите id книги: "));
        }
        return id;
    }

    @Override
    public BookDto getBookUpdateData(BookDto bookDto) {
        System.out.print("Текущее название книги: ");
        System.out.println(bookDto.getTitle());
        String title = getUserInput(STRING_REGEX_FILTER,
                STRING_INCORRECT_MESSAGE,
                "Введите новое название книги: ");
        System.out.print("Текущий автор книги: ");
        System.out.println(bookDto.getAuthor());
        String author = getUserInput(STRING_REGEX_FILTER,
                STRING_INCORRECT_MESSAGE,
                "Введите нового автора книги: ");
        System.out.print("Текущее описание книги: ");
        System.out.println(bookDto.getDescription());
        String description = getUserInput(STRING_REGEX_FILTER,
                STRING_INCORRECT_MESSAGE,
                "Введите новое описание книги: ");

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
        System.out.println("Список книг:");
        for (BookDto book : books) {
            System.out.print("id книги: ");
            System.out.println(book.getId());
            System.out.print("Название: ");
            System.out.println(book.getTitle());
            System.out.print("Автор: ");
            System.out.println(book.getAuthor());
            System.out.print("Описание: ");
            System.out.println(book.getDescription());
            System.out.println("---------------");
        }
    }

    private void printMainMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 - Создать новую книгу");
        System.out.println("2 - Редактировать книгу");
        System.out.println("3 - Удалить книгу");
        System.out.println("4 - Вывести список всех книг");
        System.out.println("Выход - Выйти из программы");
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
