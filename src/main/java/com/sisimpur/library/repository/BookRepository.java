package com.sisimpur.library.repository;

import com.sisimpur.library.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sisimpur.library.model.Book;

import java.util.List;


public interface BookRepository extends JpaRepository<Book, Long> {
    public List<Book> findByAuthor(Author author);
    List<Book> findByPublishedYear(int publishedYear);
    List<Book> findByUserIsNull();
}