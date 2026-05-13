package com.example.demo.service.book

import com.example.demo.model.author.AuthorResponse
import com.example.demo.model.book.BookRequest
import com.example.demo.model.book.BookResponse
import com.example.demo.model.book.PublicationStatus
import com.example.demo.repository.author.AuthorRepository
import com.example.demo.repository.book.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var bookService: BookService

    private val validAuthorIds = listOf(1L, 2L)
    private val authors = listOf(
        AuthorResponse(1L, "Author 1", LocalDate.of(1990, 1, 1)),
        AuthorResponse(2L, "Author 2", LocalDate.of(1995, 5, 5))
    )

    @Test
    fun `createBook should return created book successfully`() {
        // Arrange
        val request = BookRequest(title = "Test Book", price = 1000, publicationStatus = PublicationStatus.UNPUBLISHED, authorIds = validAuthorIds)
        val expectedResponse = BookResponse(id = 1L, title = "Test Book", price = 1000, publicationStatus = PublicationStatus.UNPUBLISHED, authors = authors)

        `when`(authorRepository.existsByIds(validAuthorIds)).thenReturn(true)
        `when`(bookRepository.create(request)).thenReturn(expectedResponse)

        // Act
        val result = bookService.createBook(request)

        // Assert
        assertEquals(expectedResponse, result)
        verify(authorRepository).existsByIds(validAuthorIds)
        verify(bookRepository).create(request)
    }

    @Test
    fun `createBook should throw IllegalArgumentException when author does not exist`() {
        // Arrange
        val request = BookRequest(title = "Test Book", price = 1000, publicationStatus = PublicationStatus.UNPUBLISHED, authorIds = listOf(999L))

        `when`(authorRepository.existsByIds(listOf(999L))).thenReturn(false)

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            bookService.createBook(request)
        }

        assertEquals("One or more authors do not exist", exception.message)
        verify(authorRepository).existsByIds(listOf(999L))
    }

    @Test
    fun `updateBook should throw IllegalArgumentException when changing status from PUBLISHED to UNPUBLISHED`() {
        // Arrange
        val bookId = 1L
        val existingBook = BookResponse(id = bookId, title = "Test Book", price = 1000, publicationStatus = PublicationStatus.PUBLISHED, authors = authors)
        val request = BookRequest(title = "Updated Book", price = 1000, publicationStatus = PublicationStatus.UNPUBLISHED, authorIds = validAuthorIds)

        `when`(authorRepository.existsByIds(validAuthorIds)).thenReturn(true)
        `when`(bookRepository.findById(bookId)).thenReturn(existingBook)

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            bookService.updateBook(bookId, request)
        }

        assertEquals("Cannot change status from PUBLISHED to UNPUBLISHED", exception.message)
        verify(authorRepository).existsByIds(validAuthorIds)
        verify(bookRepository).findById(bookId)
    }

    @Test
    fun `updateBook should return updated book when update is successful`() {
        // Arrange
        val bookId = 1L
        val existingBook = BookResponse(id = bookId, title = "Test Book", price = 1000, publicationStatus = PublicationStatus.UNPUBLISHED, authors = authors)
        val request = BookRequest(title = "Updated Book", price = 1100, publicationStatus = PublicationStatus.PUBLISHED, authorIds = validAuthorIds)
        val expectedResponse = BookResponse(id = bookId, title = "Updated Book", price = 1100, publicationStatus = PublicationStatus.PUBLISHED, authors = authors)

        `when`(authorRepository.existsByIds(validAuthorIds)).thenReturn(true)
        `when`(bookRepository.findById(bookId)).thenReturn(existingBook)
        `when`(bookRepository.update(bookId, request)).thenReturn(expectedResponse)

        // Act
        val result = bookService.updateBook(bookId, request)

        // Assert
        assertEquals(expectedResponse, result)
        verify(authorRepository).existsByIds(validAuthorIds)
        verify(bookRepository).findById(bookId)
        verify(bookRepository).update(bookId, request)
    }

    @Test
    fun `updateBook should throw IllegalArgumentException when book does not exist`() {
        // Arrange
        val bookId = 999L
        val request = BookRequest(title = "Updated Book", price = 1000, publicationStatus = PublicationStatus.PUBLISHED, authorIds = validAuthorIds)

        `when`(authorRepository.existsByIds(validAuthorIds)).thenReturn(true)
        `when`(bookRepository.findById(bookId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            bookService.updateBook(bookId, request)
        }

        assertEquals("Book not found with id: $bookId", exception.message)
        verify(authorRepository).existsByIds(validAuthorIds)
        verify(bookRepository).findById(bookId)
    }

    @Test
    fun `getBooksByAuthorId should return list of books when author has books`() {
        // Arrange
        val authorId = 1L
        val expectedResponse = listOf(
            BookResponse(id = 1L, title = "Test Book", price = 1000, publicationStatus = PublicationStatus.PUBLISHED, authors = authors)
        )

        `when`(bookRepository.findBooksByAuthorId(authorId)).thenReturn(expectedResponse)

        // Act
        val result = bookService.getBooksByAuthorId(authorId)

        // Assert
        assertEquals(expectedResponse, result)
        verify(bookRepository).findBooksByAuthorId(authorId)
    }

    @Test
    fun `getBooksByAuthorId should return empty list when author has no books`() {
        // Arrange
        val authorId = 999L // 存在しない、または書籍を持たない著者ID
        val expectedResponse = emptyList<BookResponse>()

        `when`(bookRepository.findBooksByAuthorId(authorId)).thenReturn(expectedResponse)

        // Act
        val result = bookService.getBooksByAuthorId(authorId)

        // Assert
        assertEquals(expectedResponse, result)
        verify(bookRepository).findBooksByAuthorId(authorId)
    }
}
