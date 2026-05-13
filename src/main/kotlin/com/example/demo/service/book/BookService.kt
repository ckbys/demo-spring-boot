package com.example.demo.service.book

import com.example.demo.model.book.BookRequest
import com.example.demo.model.book.BookResponse
import com.example.demo.model.book.PublicationStatus
import com.example.demo.repository.author.AuthorRepository
import com.example.demo.repository.book.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    @Transactional
    fun createBook(request: BookRequest): BookResponse {
        validateAuthorsExist(request.authorIds!!)
        return bookRepository.create(request)
    }

    @Transactional
    fun updateBook(id: Long, request: BookRequest): BookResponse {
        validateAuthorsExist(request.authorIds!!)
        
        val existingBook = bookRepository.findById(id) 
            ?: throw IllegalArgumentException("Book not found with id: $id")
            
        // Validate status transition: Cannot change from PUBLISHED to UNPUBLISHED
        if (existingBook.publicationStatus == PublicationStatus.PUBLISHED && 
            request.publicationStatus == PublicationStatus.UNPUBLISHED) {
            throw IllegalArgumentException("Cannot change status from PUBLISHED to UNPUBLISHED")
        }

        return bookRepository.update(id, request) 
            ?: throw IllegalArgumentException("Failed to update book")
    }
    
    @Transactional(readOnly = true)
    fun getBooksByAuthorId(authorId: Long): List<BookResponse> {
        return bookRepository.findBooksByAuthorId(authorId)
    }

    private fun validateAuthorsExist(authorIds: List<Long>) {
        if (!authorRepository.existsByIds(authorIds)) {
            throw IllegalArgumentException("One or more authors do not exist")
        }
    }
}
