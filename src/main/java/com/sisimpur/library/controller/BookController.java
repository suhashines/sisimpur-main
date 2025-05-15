package com.sisimpur.library.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sisimpur.library.dto.BookRequest;
import com.sisimpur.library.model.Book;
import com.sisimpur.library.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequest bookDto) {

        Book savedBook = bookService.createBook(bookDto);

        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Map<String, Object> bookData) {
        Book updatedBook = bookService.updateBook(id, bookData);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> filterBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean available) {

        List<Book> filteredBooks = bookService.filterBooks(author, title, genre, year, available);

        if (filteredBooks.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 if no books match
        }

        return ResponseEntity.ok(filteredBooks); // 200 OK
    }


}
