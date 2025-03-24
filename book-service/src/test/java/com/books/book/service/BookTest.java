package com.books.book.service;

import com.books.dto.AuthorDto;
import com.books.dto.BookDto;
import com.books.dto.GenreDto;
import com.books.user.model.dto.LoginRequest;
import com.books.user.model.dto.SignupRequest;
import com.books.utils.helper.RetryHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BookTest {

    public static ComposeContainer containers;
    private static final RestTemplate restTemplate = new RestTemplate();
    private static String baseUrlBooks;
    private static String baseUrlUsers;
    private static String token;

    static {
        containers = new ComposeContainer(new File("../docker-compose.test.yml"),
                new File("../docker-compose.dev.yml"))
                .withServices("consul", "kafka", "config-server", "book-service", "user-service", "book-db",
                        "user-db", "gateway", "jwt-service")
                .waitingFor("book-service", Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)))
                .waitingFor("user-service", Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)))
                .waitingFor("jwt-service", Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)))
                .waitingFor("gateway", Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)))
                .withLocalCompose(true);

        containers.start();
    }

    @BeforeAll
    static void setup() {
        baseUrlBooks = "http://localhost:8080/api/v1/books";
        baseUrlUsers = "http://localhost:8080/api/v1/users";

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(@NotNull HttpStatusCode statusCode) {
                return false; // Отключаем обработку ошибок статусов
            }
        });
        // 1. Регистрация пользователя
        SignupRequest signupRequest = new SignupRequest("user", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> signupRequestEntity = new HttpEntity<>(signupRequest, headers);
        RetryHelper.executeWithRetry(() -> restTemplate
                .postForEntity(baseUrlUsers + "/register", signupRequestEntity, String.class));

        // 2. Логин пользователя и получение JWT токена
        LoginRequest loginRequest = new LoginRequest("user", "password");
        HttpEntity<LoginRequest> loginRequestEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .postForEntity(baseUrlUsers + "/login", loginRequestEntity, String.class));
        token = loginResponse.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

    }

    @BeforeEach
    void cleanupDatabase() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://localhost:6542/books";
        String username = System.getenv("POSTGRE_BOOKS_USER");
        String password = System.getenv("POSTGRE_BOOKS_PASS");
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        Statement statement = connection.createStatement();

        statement.execute("TRUNCATE TABLE books RESTART IDENTITY CASCADE");
        statement.close();
        connection.close();
    }


    @Test
    void createBook_ShouldSaveBook() {
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));
        BookDto savedBook = createBookResponse.getBody();

        assertThat(savedBook, notNullValue());
        assertThat(savedBook.getId(), equalTo(1));
        assertThat(createBookResponse.getStatusCode(), equalTo(HttpStatus.CREATED));


        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<List<BookDto>> bookListEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookDto>> bookListResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.GET, bookListEntity, new ParameterizedTypeReference<>() {
                }));
        List<BookDto> books = bookListResponse.getBody();

        assertThat(books, notNullValue());
        assertThat(books.size(), equalTo(1));

        BookDto createdBook = books.get(0);

        assertThat(createdBook.getTitle(), equalTo(bookDto.getTitle()));
        assertThat(createdBook.getDescription(), equalTo(bookDto.getDescription()));
    }

    @Test
    void createBook_ShouldReturnStatusForbidden_IfNotAuthorized() {
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<Void> createBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, Void.class));

        assertThat(createBookResponse.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));


        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<List<BookDto>> bookListEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookDto>> bookListResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.GET, bookListEntity, new ParameterizedTypeReference<>() {
                }));
        List<BookDto> books = bookListResponse.getBody();

        assertThat(books, notNullValue());
        assertThat(books.size(), equalTo(0));
    }

    @Test
    void createBook_ShouldReturnBadRequestStatus_WhenInvalidBody() {
        BookDto bookDto = new BookDto(null, "T", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<Void> createBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, Void.class));

        assertThat(createBookResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));


        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<List<BookDto>> bookListEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookDto>> bookListResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.GET, bookListEntity, new ParameterizedTypeReference<>() {
                }));
        List<BookDto> books = bookListResponse.getBody();

        assertThat(books, notNullValue());
        assertThat(books.size(), equalTo(0));
    }

    @Test
    void getAllBooks_ShouldGetAllBooks() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<List<BookDto>> bookListEntity1 = new HttpEntity<>(headers);
        ResponseEntity<List<BookDto>> bookListResponse1 = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.GET, bookListEntity1, new ParameterizedTypeReference<>() {
                }));

        assertThat(bookListResponse1.getStatusCode(), equalTo(HttpStatus.OK));

        List<BookDto> books = bookListResponse1.getBody();

        assertThat(books, notNullValue());
        assertThat(books.size(), equalTo(0));

        BookDto bookDto1 = new BookDto(null, "Title1", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description1", null);
        BookDto bookDto2 = new BookDto(null, "Title2", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description2", null);
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity1 = new HttpEntity<>(bookDto1, headers);
        ResponseEntity<Void> createBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity1, Void.class));

        assertThat(createBookResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity2 = new HttpEntity<>(bookDto2, headers);
        createBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity2, Void.class));

        assertThat(createBookResponse.getStatusCode(), equalTo(HttpStatus.CREATED));


        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<List<BookDto>> bookListEntity2 = new HttpEntity<>(headers);
        ResponseEntity<List<BookDto>> bookListResponse2 = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.GET, bookListEntity2, new ParameterizedTypeReference<>() {
                }));

        assertThat(bookListResponse2.getStatusCode(), equalTo(HttpStatus.OK));

        books = bookListResponse2.getBody();

        assertThat(books, notNullValue());
        assertThat(books.size(), equalTo(2));
    }

    @Test
    void getAllBooks_ShouldReturnStatusForbidden_WhenNotAuthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<List<BookDto>> bookListEntity = new HttpEntity<>(headers);
        ResponseEntity<String> bookListResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.GET, bookListEntity, new ParameterizedTypeReference<>() {
                }));
        assertThat(bookListResponse.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void getBook_ShouldReturnBook() {
        // Сначала создаем книгу
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));
        BookDto savedBook = createBookResponse.getBody();

        assertThat(savedBook, notNullValue());

        // Затем получаем книгу по id
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<BookDto> getBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + savedBook.getId(),
                                HttpMethod.GET, getBookEntity, BookDto.class));

        assertThat(getBookResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getBookResponse.getBody(), notNullValue());
        assertThat(getBookResponse.getBody().getTitle(), equalTo(bookDto.getTitle()));
    }

    @Test
    void getBook_ShouldReturnNotFound_WhenBookDoesNotExist() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<BookDto> getBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/999", HttpMethod.GET, getBookEntity, BookDto.class));

        assertThat(getBookResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getBook_ShouldReturnForbidden_WhenNotAuthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<String> getBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/1", HttpMethod.GET, getBookEntity, String.class));

        assertThat(getBookResponse.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        // Сначала создаем книгу
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createBookResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));
        BookDto savedBook = createBookResponse.getBody();

        assertThat(savedBook, notNullValue());

        // Удаляем книгу
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> deleteBookEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + savedBook.getId(),
                                HttpMethod.DELETE, deleteBookEntity, Void.class));

        assertThat(deleteResponse.getStatusCode(), equalTo(HttpStatus.OK));

        // Проверяем, что книга удалена
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<String> getAllResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + savedBook.getId(), HttpMethod.GET, getBookEntity,
                                new ParameterizedTypeReference<>() {}));

        assertThat(getAllResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void deleteBook_ShouldReturnForbidden_WhenNotAuthorized() {
        // Сначала создаем книгу
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createBookResponse = RetryHelper.executeWithRetry(() -> restTemplate
                .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));
        BookDto savedBook = createBookResponse.getBody();

        assertThat(savedBook, notNullValue());

        // Удаляем книгу без авторизации
        headers = new HttpHeaders();
        HttpEntity<Void> deleteBookEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + savedBook.getId(),
                                HttpMethod.DELETE, deleteBookEntity, Void.class));

        assertThat(deleteResponse.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));

        // Проверяем, что книга не удалена
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + savedBook.getId(), HttpMethod.GET, getBookEntity,
                                new ParameterizedTypeReference<>() {}));

        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void deleteBook_ShouldReturnNotFound_WhenBookDontExist() {
        // Удаляем книгу которой нет в базе
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> deleteBookEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + 1,
                                HttpMethod.DELETE, deleteBookEntity, Void.class));

        assertThat(deleteResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updateBook_ShouldUpdateBook() {
        // Сначала создаем книгу
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));

        BookDto createdBook = createResponse.getBody();

        assertThat(createdBook, notNullValue());

        // Обновляем книгу
        BookDto updatedBookDto = new BookDto(createdBook.getId(), "Updated Title",
                List.of(new AuthorDto("Updated Author")),
                List.of(new GenreDto("Updated genre")), "Updated description", null);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> updateBookEntity = new HttpEntity<>(updatedBookDto, headers);
        ResponseEntity<Void> updateResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.PUT, updateBookEntity, Void.class));

        assertThat(updateResponse.getStatusCode(), equalTo(HttpStatus.OK));

        // Проверяем обновленную книгу
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<BookDto> getBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + createdBook.getId(), HttpMethod.GET,
                                getBookEntity, BookDto.class));
        BookDto updatedBook = getBookResponse.getBody();

        assertThat(updatedBook, notNullValue());
        assertThat(updatedBook.getTitle(), equalTo(updatedBookDto.getTitle()));
    }

    @Test
    void updateBook_ShouldReturnForbidden_WhenNotAuthorized() {
        // Сначала создаем книгу
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));

        BookDto createdBook = createResponse.getBody();

        assertThat(createdBook, notNullValue());

        // Обновляем книгу
        BookDto updatedBookDto = new BookDto(createdBook.getId(), "Updated Title",
                List.of(new AuthorDto("Updated Author")),
                List.of(new GenreDto("Updated genre")), "Updated description", null);

        headers = new HttpHeaders();
        HttpEntity<BookDto> updateBookEntity = new HttpEntity<>(updatedBookDto, headers);
        ResponseEntity<Void> updateResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.PUT, updateBookEntity, Void.class));

        assertThat(updateResponse.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));

        // Проверяем что книга не обновилась
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<BookDto> getBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + createdBook.getId(), HttpMethod.GET,
                                getBookEntity, BookDto.class));
        BookDto updatedBook = getBookResponse.getBody();

        assertThat(updatedBook, notNullValue());
        assertThat(updatedBook.getTitle(), equalTo(createdBook.getTitle()));
    }

    @Test
    void updateBook_ShouldReturnBadRequest_WhenInvalidBookProperties() {
        // Сначала создаем книгу
        BookDto bookDto = new BookDto(null, "Title", List.of(new AuthorDto("Author")),
                List.of(new GenreDto("genre")), "description", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> createBookEntity = new HttpEntity<>(bookDto, headers);
        ResponseEntity<BookDto> createResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.POST, createBookEntity, BookDto.class));

        BookDto createdBook = createResponse.getBody();

        assertThat(createdBook, notNullValue());

        // Обновляем книгу на книгу с некорректным названием
        BookDto updatedBookDto = new BookDto(createdBook.getId(), "U",
                List.of(new AuthorDto("Updated Author")),
                List.of(new GenreDto("Updated genre")), "Updated description", null);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<BookDto> updateBookEntity = new HttpEntity<>(updatedBookDto, headers);
        ResponseEntity<Void> updateResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks, HttpMethod.PUT, updateBookEntity, Void.class));

        assertThat(updateResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));

        // Проверяем что книга не обновилась
        headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> getBookEntity = new HttpEntity<>(headers);
        ResponseEntity<BookDto> getBookResponse = RetryHelper
                .executeWithRetry(() -> restTemplate
                        .exchange(baseUrlBooks + "/" + createdBook.getId(), HttpMethod.GET,
                                getBookEntity, BookDto.class));
        BookDto updatedBook = getBookResponse.getBody();

        assertThat(updatedBook, notNullValue());
        assertThat(updatedBook.getTitle(), equalTo(createdBook.getTitle()));
    }
}
