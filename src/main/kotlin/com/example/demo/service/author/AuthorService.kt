package com.example.demo.service.author

import com.example.demo.model.author.AuthorRequest
import com.example.demo.model.author.AuthorResponse
import com.example.demo.repository.author.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository
) {

    @Transactional
    fun createAuthor(request: AuthorRequest): AuthorResponse {
        return authorRepository.create(request)
    }

    @Transactional
    fun updateAuthor(id: Long, request: AuthorRequest): AuthorResponse {
        return authorRepository.update(id, request)
            ?: throw IllegalArgumentException("Author not found with id: $id")
    }
}
