package com.sisimpur.library.service;

import com.sisimpur.library.model.Author;
import com.sisimpur.library.model.Book;
import com.sisimpur.library.repository.AuthorRepository;
import com.sisimpur.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public Author getAuthorById(Long id){
        return authorRepository.findById(id).orElse(null);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author updateAuthor(Long id, Map<String, String> authorData) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isEmpty()) {
            throw new RuntimeException("Author not found with ID: " + id);
        }

        Author author = optionalAuthor.get();

        // Validate and update fields
        if (authorData.containsKey("name")) {
            String name = authorData.get("name").trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }
            author.setName(name);
        }

        if (authorData.containsKey("biography")) {
            String bio = authorData.get("biography").trim();
            if (bio.isEmpty()) {
                throw new IllegalArgumentException("Biography cannot be empty.");
            }
            author.setBio(bio);
        }

        return authorRepository.save(author);
    }

    public Author createAuthor(Map<String, Object> authorData) {
        String name = (String) authorData.get("name");

        System.out.println("got author name "+name);
        // Author name is mandatory
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }

        // Bio is optional
        String biography = (String) authorData.getOrDefault("biography", "");

        // Create and save the Author
        Author author = new Author();
        author.setName(name);
        author.setBio(biography);
        Author savedAuthor = authorRepository.save(author);

        // Handle books
        List<Map<String, Object>> booksData = (List<Map<String, Object>>) authorData.get("books");
        List<Book> books = new ArrayList<>();

        if (booksData != null) {
            for (Map<String, Object> bookMap : booksData) {
                String title = (String) bookMap.get("title");

                // Book must have a title
                if (title == null || title.trim().isEmpty()) {
                    throw new IllegalArgumentException("Book title cannot be empty.");
                }

                String genre = (String) bookMap.getOrDefault("genre", null);
                Integer publishedYear = bookMap.containsKey("published_year")
                        ? ((Number) bookMap.get("published_year")).intValue()
                        : null;

                // Create and save book
                Book book = new Book();
                book.setTitle(title);
                book.setGenre(genre); // Nullable
                book.setPublishedYear(publishedYear != null ? publishedYear : 0); // Default 0 if missing
                book.setAuthor(savedAuthor);
                books.add(book);
            }
        }

        // Save all books if any
        if (!books.isEmpty()) {
            bookRepository.saveAll(books);
        }

        return savedAuthor;
    }
}
