package com.example.demo.controller.book

import com.example.demo.model.book.BookRequest
import com.example.demo.model.book.BookResponse
import com.example.demo.service.book.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class BookController(
    private val bookService: BookService
) {

    @PostMapping("/books")
    fun createBook(@Valid @RequestBody request: BookRequest): ResponseEntity<BookResponse> {
        val response = bookService.createBook(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/books/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @Valid @RequestBody request: BookRequest
    ): ResponseEntity<BookResponse> {
        val response = bookService.updateBook(id, request)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/authors/{authorId}/books")
    fun getBooksByAuthor(@PathVariable authorId: Long): ResponseEntity<List<BookResponse>> {
        val response = bookService.getBooksByAuthorId(authorId)
        return ResponseEntity.ok(response)
    }
}
