package com.books.book.repository;

import com.books.book.model.Book;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @EntityGraph(value = "book.authors.genres", type = EntityGraph.EntityGraphType.FETCH)
    @Cacheable(value = "books")
    @NonNull
    List<Book> findAll();

    @NonNull
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors LEFT JOIN FETCH b.genres WHERE b.id = :id")
    Optional<Book> findById(@NonNull @Param("id") Integer id);
}
