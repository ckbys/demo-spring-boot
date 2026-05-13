package com.example.demo.controller.author

import com.example.demo.model.author.AuthorRequest
import com.example.demo.model.author.AuthorResponse
import com.example.demo.service.author.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    @PostMapping
    fun createAuthor(@Valid @RequestBody request: AuthorRequest): ResponseEntity<AuthorResponse> {
        val response = authorService.createAuthor(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun updateAuthor(
        @PathVariable id: Long,
        @Valid @RequestBody request: AuthorRequest
    ): ResponseEntity<AuthorResponse> {
        val response = authorService.updateAuthor(id, request)
        return ResponseEntity.ok(response)
    }
}
