package com.sisimpur.library.service;

import com.sisimpur.library.dto.BookRequest;
import com.sisimpur.library.model.Author;
import com.sisimpur.library.repository.AuthorRepository;
import com.sisimpur.library.util.StringMatchingUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sisimpur.library.model.Book;
import com.sisimpur.library.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    private final static double SEARCH_TOLERANCE = 0.12;

    public Book getBook(Long id) {
        return bookRepository.findById(id).orElse(null);

    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    //refactored api
    public Book createBook(BookRequest bookData) {
        
        Author author = authorRepository.findById(bookData.getAuthorId().longValue()).get() ;
               
        // Prepare Book entity
        Book book = new Book();
        book.setTitle(bookData.getTitle().trim());

        // Set genre only if not blank
        String genre = bookData.getGenre();
        if (genre != null && !genre.trim().isEmpty()) {
            book.setGenre(genre.trim());
        } else {
            book.setGenre(null);
        }

        // Set publishedYear (default to 0 if null)
        book.setPublishedYear(bookData.getPublishedYear() != null ? bookData.getPublishedYear() : 0);

        // Associate author
        book.setAuthor(author);

        return bookRepository.save(book);
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

    public List<Book> getBooksByAuthor(String authorName) {
        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }

        List<Author> allAuthors = authorRepository.findAll();

        // If no authors exist, return empty list
        if (allAuthors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Book> matchingBooks = new ArrayList<>();

        for (Author author : allAuthors) {
            double score = StringMatchingUtil.calculateCombinedScore(authorName, author.getName());

            if (score > SEARCH_TOLERANCE) {
                List<Book> booksByAuthor = bookRepository.findByAuthor(author);
                matchingBooks.addAll(booksByAuthor);
            }
        }

        if (matchingBooks.isEmpty()) {
            return Collections.emptyList();
        }

        return matchingBooks;
    }

    public List<Book> getBooksByTitle(String bookTitle) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }

        List<Book> allBooks = bookRepository.findAll();

        if (allBooks.isEmpty()) {
            return Collections.emptyList();
        }

        List<Book> matchingBooks = new ArrayList<>();

        for (Book book : allBooks) {
            double score = StringMatchingUtil.calculateCombinedScore(bookTitle, book.getTitle());

            if (score > SEARCH_TOLERANCE) {
                matchingBooks.add(book);
            }
        }

        // If no matching books found, return empty response
        if (matchingBooks.isEmpty()) {
            return Collections.emptyList();
        }

        return matchingBooks;
    }

    public List<Book> getBooksByGenre(String genreQuery) {

        if (genreQuery == null || genreQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty");
        }

        // fetching all books
        List<Book> allBooks = bookRepository.findAll();

        if (allBooks.isEmpty()) {
            return Collections.emptyList();
        }

        List<Book> bestMatches = new ArrayList<>();
        double highestScore = 0.0;

        for (Book book : allBooks) {
            String genre = book.getGenre();
            if (genre == null || genre.trim().isEmpty()) {
                continue;
            }

            double score = StringMatchingUtil.calculateCombinedScore(genreQuery, genre);

            if (score > highestScore) {
                highestScore = score;
                bestMatches.clear();
                bestMatches.add(book);
            } else if (score == highestScore) {
                bestMatches.add(book);
            }
        }

        return bestMatches;
    }

    public List<Book> getBooksByPublishedYear(int year) {
        List<Book> books = bookRepository.findByPublishedYear(year);
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found for the given published year.");
        }
        return books;
    }

    public List<Book> getAvailableBooks() {
        List<Book> books = bookRepository.findByUserIsNull();
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available books found.");
        }
        return books;
    }

    // let's combine these queries

    public List<Book> filterBooks(String author, String title, String genre, Integer publishedYear, Boolean available) {

        Set<Book> resultSet = null;

        if (author != null && !author.isEmpty()) {
            resultSet = new HashSet<>(getBooksByAuthor(author));
        }

        if (title != null && !title.isEmpty()) {
            Set<Book> titleMatches = new HashSet<>(getBooksByTitle(title));
            resultSet = (resultSet == null) ? titleMatches : intersection(resultSet, titleMatches);
        }

        if (genre != null && !genre.isEmpty()) {
            Set<Book> genreMatches = new HashSet<>(getBooksByGenre(genre));
            resultSet = (resultSet == null) ? genreMatches : intersection(resultSet, genreMatches);
        }

        if (publishedYear != null) {
            Set<Book> yearMatches = new HashSet<>(getBooksByPublishedYear(publishedYear));
            resultSet = (resultSet == null) ? yearMatches : intersection(resultSet, yearMatches);
        }

        if (available != null && available) {
            Set<Book> availabilityMatches = new HashSet<>(getAvailableBooks());
            resultSet = (resultSet == null) ? availabilityMatches : intersection(resultSet, availabilityMatches);
        }

        return (resultSet == null) ? Collections.emptyList() : new ArrayList<>(resultSet);
    }

    private Set<Book> intersection(Set<Book> set1, Set<Book> set2) {
        set1.retainAll(set2);
        return set1;
    }

}
