package com.sisimpur.library.service;

import com.sisimpur.library.model.Author;
import com.sisimpur.library.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sisimpur.library.model.Book;
import com.sisimpur.library.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public Book getBook(Long id) {
        return bookRepository.findById(id).orElse(null);

    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    public Book createBook(Map<String, Object> bookData) {

        try {
                String title = (String) bookData.get("title");
                System.out.println("Received book title: " + title);
                String genre = (String) bookData.get("genre");

                // Validate published year
                if (!(bookData.get("published_year") instanceof Number)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Published year must be an integer.");
                }
                int publishedYear = ((Number) bookData.get("published_year")).intValue();

                // Validate author ID
                if (!(bookData.get("author_id") instanceof Number)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author ID must be a number.");
                }
                Long authorId = ((Number) bookData.get("author_id")).longValue();

                Optional<Author> author = authorRepository.findById(authorId);
                if (author.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found with ID: " + authorId);
                }

                // Create and save book
                Book book = new Book();
                book.setTitle(title);
                book.setGenre(genre);
                book.setPublishedYear(publishedYear);
                book.setAuthor(author.get());

                return bookRepository.save(book);

            } catch (ClassCastException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data type provided.");
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
            }
        }

    public Book updateBook(Long bookId, Map<String, Object> bookData) {

        Optional<Book> existingBook = bookRepository.findById(bookId);
        if (existingBook.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with ID: " + bookId);
        }

        Book book = existingBook.get();

        try {
            if (bookData.containsKey("title")) {
                book.setTitle((String) bookData.get("title"));
            }

            if (bookData.containsKey("genre")) {
                book.setGenre((String) bookData.get("genre"));
            }

            if (bookData.containsKey("published_year")) {
                if (!(bookData.get("published_year") instanceof Number)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Published year must be an integer.");
                }
                book.setPublishedYear(((Number) bookData.get("published_year")).intValue());
            }

            if (bookData.containsKey("author_id")) {
                if (!(bookData.get("author_id") instanceof Number)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author ID must be a number.");
                }
                Long authorId = ((Number) bookData.get("author_id")).longValue();
                Optional<Author> author = authorRepository.findById(authorId);
                if (author.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found with ID: " + authorId);
                }
                book.setAuthor(author.get());
            }

            return bookRepository.save(book);

        } catch (ClassCastException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data type provided.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    public void deleteBook(Long bookId) {
        Optional<Book> existingBook = bookRepository.findById(bookId);
        if (existingBook.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with ID: " + bookId);
        }

        bookRepository.deleteById(bookId);
    }


}
