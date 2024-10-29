package books.storage;

import books.model.Author;
import books.model.Book;
import books.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class BookStorageDbImpl implements BookStorage {
    private final JdbcTemplate jdbcTemplate;

    public BookStorageDbImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void add(Book book) {
        String sql = "INSERT INTO books (title, description) VALUES (?, ?) RETURNING book_id";
        Integer bookId = jdbcTemplate.queryForObject(sql, Integer.class, book.getTitle(), book.getDescription());

        for (Author author : book.getAuthors()) {
            Integer authorId = findOrCreateAuthor(author);
            linkAuthorToBook(bookId, authorId);
        }

        for (Genre genre : book.getGenres()) {
            Integer genreId = findOrCreateGenre(genre);
            linkGenreToBook(bookId, genreId);
        }
    }

    @Override
    public Optional<Book> get(Integer id) {
        String sql = "SELECT " +
                "b.book_id, b.title, b.description, " +
                "a.author_id, a.name AS author_name, " +
                "g.genre_id, g.title AS genre_title " +
                "FROM " +
                "books b " +
                "LEFT JOIN " +
                "book_author ba ON b.book_id = ba.book_id " +
                "LEFT JOIN " +
                "authors a ON ba.author_id = a.author_id " +
                "LEFT JOIN " +
                "book_genre bg ON b.book_id = bg.book_id " +
                "LEFT JOIN " +
                "genres g ON bg.genre_id = g.genre_id " +
                "WHERE b.book_id = ?";

        // Создаем пустую книгу
        Book book = new Book();
        List<Author> authors = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            if (book.getId() == null) {
                // Инициализация книги только один раз
                book.setId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setDescription(rs.getString("description"));
            }

            // Добавление авторов
            Integer authorId = rs.getInt("author_id");
            if (authorId != null && !containsAuthor(authors, authorId)) {
                Author author = new Author();
                author.setId(authorId);
                author.setName(rs.getString("author_name"));
                authors.add(author);
            }

            // Добавление жанров
            Integer genreId = rs.getInt("genre_id");
            if (genreId != null && !containsGenre(genres, genreId)) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setTitle(rs.getString("genre_title"));
                genres.add(genre);
            }
        }, id);

        // Если книга не была найдена
        if (book.getId() == null) {
            return Optional.empty();
        }

        // Устанавливаем авторов и жанры книги
        book.setAuthors(authors);
        book.setGenres(genres);

        return Optional.of(book);
    }

    @Override
    @Transactional
    public void update(Book updatedBook) {
        String sql = "UPDATE books SET title = ?, description = ? WHERE book_id = ?";
        jdbcTemplate.update(sql, updatedBook.getTitle(), updatedBook.getDescription(), updatedBook.getId());

        // Удаляем существующие связи книги с авторами и жанрами
        jdbcTemplate.update("DELETE FROM book_author WHERE book_id = ?", updatedBook.getId());
        jdbcTemplate.update("DELETE FROM book_genre WHERE book_id = ?", updatedBook.getId());

        // Добавляем новые связи с авторами
        for (Author author : updatedBook.getAuthors()) {
            Integer authorId = findOrCreateAuthor(author);
            linkAuthorToBook(updatedBook.getId(), authorId);
        }

        // Добавляем новые связи с жанрами
        for (Genre genre : updatedBook.getGenres()) {
            Integer genreId = findOrCreateGenre(genre);
            linkGenreToBook(updatedBook.getId(), genreId);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT " +
                "b.book_id, b.title, b.description, " +
                "a.author_id, a.name AS author_name, " +
                "g.genre_id, g.title AS genre_title " +
            "FROM " +
                "books b " +
            "LEFT JOIN " +
                "book_author ba ON b.book_id = ba.book_id " +
            "LEFT JOIN " +
                "authors a ON ba.author_id = a.author_id " +
            "LEFT JOIN " +
                "book_genre bg ON b.book_id = bg.book_id " +
            "LEFT JOIN " +
                "genres g ON bg.genre_id = g.genre_id";

        Map<Integer, Book> bookMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Integer bookId = rs.getInt("book_id");
            Book book = bookMap.get(bookId);

            // Если книга еще не добавлена в Map, создаем новый объект Book
            if (book == null) {
                book = new Book();
                book.setId(bookId);
                book.setTitle(rs.getString("title"));
                book.setDescription(rs.getString("description"));
                book.setAuthors(new ArrayList<>());
                book.setGenres(new ArrayList<>());
                bookMap.put(bookId, book);
            }

            // Добавляем автора, если он существует
            Integer authorId = rs.getInt("author_id");
            if (authorId != null && !containsAuthor(book.getAuthors(), authorId)) {
                Author author = new Author();
                author.setId(authorId);
                author.setName(rs.getString("author_name"));
                book.getAuthors().add(author);
            }

            // Добавляем жанр, если он существует
            Integer genreId = rs.getInt("genre_id");
            if (genreId != null && !containsGenre(book.getGenres(), genreId)) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setTitle(rs.getString("genre_title"));
                book.getGenres().add(genre);
            }
        });

        return List.copyOf(bookMap.values());
    }

    private boolean containsAuthor(List<Author> authors, Integer authorId) {
        return authors.stream().anyMatch(author -> author.getId().equals(authorId));
    }

    private boolean containsGenre(List<Genre> genres, Integer genreId) {
        return genres.stream().anyMatch(genre -> genre.getId().equals(genreId));
    }

    private Integer findOrCreateAuthor(Author author) {
        String findAuthorSql = "SELECT author_id FROM authors WHERE name = ?";
        List<Integer> authorIds = jdbcTemplate.query(findAuthorSql, (rs, rowNum) -> rs.getInt("author_id"), author.getName());

        if (!authorIds.isEmpty()) {
            return authorIds.get(0); // Автор уже существует
        }

        // Если автор не найден, создаем его
        String insertAuthorSql = "INSERT INTO authors (name) VALUES (?) RETURNING author_id";
        return jdbcTemplate.queryForObject(insertAuthorSql, Integer.class, author.getName());
    }

    private Integer findOrCreateGenre(Genre genre) {
        String findGenreSql = "SELECT genre_id FROM genres WHERE title = ?";
        List<Integer> genreIds = jdbcTemplate.query(findGenreSql, (rs, rowNum) -> rs.getInt("genre_id"), genre.getTitle());

        if (!genreIds.isEmpty()) {
            return genreIds.get(0); // Жанр уже существует
        }

        // Если жанр не найден, создаем его
        String insertGenreSql = "INSERT INTO genres (title) VALUES (?) RETURNING genre_id";
        return jdbcTemplate.queryForObject(insertGenreSql, Integer.class, genre.getTitle());
    }

    // Метод для связывания книги с автором
    private void linkAuthorToBook(Integer bookId, Integer authorId) {
        String sql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, bookId, authorId);
    }

    // Метод для связывания книги с жанром
    private void linkGenreToBook(Integer bookId, Integer genreId) {
        String sql = "INSERT INTO book_genre (book_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, bookId, genreId);
    }
}
