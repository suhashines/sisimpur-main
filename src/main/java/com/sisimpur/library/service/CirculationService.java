package com.sisimpur.library.service;

import com.sisimpur.library.model.Author;
import com.sisimpur.library.model.Book;
import com.sisimpur.library.model.User;
import com.sisimpur.library.repository.BookRepository;
import com.sisimpur.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor

public class CirculationService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Map<String, Object> borrowBooks(Long userId, List<Long> bookIds) {
        Map<String, Object> response = new HashMap<>();

        // Check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found.");
            return response;
        }

        // Fetch books from database
        List<Book> books = bookRepository.findAllById(bookIds);

        // Check if all requested books exist
        if (books.size() != bookIds.size()) {
            response.put("success", false);
            response.put("message", "Some books do not exist. Borrowing failed.");
            return response;
        }

        // if any book is already borrowed
        for (Book book : books) {
            if (book.getUser() != null) {
                response.put("success", false);
                response.put("message", "Some books are already borrowed. Borrowing failed.");
                return response; // entire request is rejected
            }
        }

        // If all books are available, we can borrow them
        for (Book book : books) {
            book.setUser(userOptional.get());
        }
        bookRepository.saveAll(books);

        response.put("success", true);
        response.put("message", "Books borrowed successfully.");
        return response;
    }


    public Map<String, Object> returnBooks(Long userId, List<Long> bookIds) {
        Map<String, Object> response = new HashMap<>();

        //if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found.");
            return response;
        }

        List<Book> books = bookRepository.findAllById(bookIds);
        List<Long> returnedBooks = new ArrayList<>();
        List<Long> invalidBooks = new ArrayList<>();

        for (Book book : books) {
            if (book.getUser() != null && book.getUser().getId().equals(userId)) {
                returnedBooks.add(book.getId());
                book.setUser(null);
            } else {
                invalidBooks.add(book.getId());
            }
        }

        if (returnedBooks.isEmpty()) {
            response.put("success", false);
            response.put("message", "No books were returned. Either they are not borrowed or invalid.");
            return response;
        }

        // saving the returned books
        bookRepository.saveAll(books);

        response.put("success", true);
        response.put("message", "Books returned successfully.");
        response.put("returned_books", returnedBooks);
        response.put("invalid_books", invalidBooks);

        return response;
    }

}
