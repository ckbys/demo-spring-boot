package com.example.demo.service.author

import com.example.demo.model.author.AuthorRequest
import com.example.demo.model.author.AuthorResponse
import com.example.demo.repository.author.AuthorRepository
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
class AuthorServiceTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var authorService: AuthorService

    @Test
    fun `createAuthor should return created author successfully`() {
        // Arrange
        val request = AuthorRequest(name = "Test Author", birthDate = LocalDate.of(1990, 1, 1))
        val expectedResponse = AuthorResponse(id = 1L, name = "Test Author", birthDate = LocalDate.of(1990, 1, 1))
        
        `when`(authorRepository.create(request)).thenReturn(expectedResponse)

        // Act
        val result = authorService.createAuthor(request)

        // Assert
        assertEquals(expectedResponse, result)
        verify(authorRepository).create(request)
    }

    @Test
    fun `updateAuthor should return updated author when author exists`() {
        // Arrange
        val authorId = 1L
        val request = AuthorRequest(name = "Updated Author", birthDate = LocalDate.of(1990, 1, 1))
        val expectedResponse = AuthorResponse(id = authorId, name = "Updated Author", birthDate = LocalDate.of(1990, 1, 1))
        
        `when`(authorRepository.update(authorId, request)).thenReturn(expectedResponse)

        // Act
        val result = authorService.updateAuthor(authorId, request)

        // Assert
        assertEquals(expectedResponse, result)
        verify(authorRepository).update(authorId, request)
    }

    @Test
    fun `updateAuthor should throw IllegalArgumentException when author does not exist`() {
        // Arrange
        val authorId = 999L
        val request = AuthorRequest(name = "Updated Author", birthDate = LocalDate.of(1990, 1, 1))
        
        `when`(authorRepository.update(authorId, request)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authorService.updateAuthor(authorId, request)
        }
        
        assertEquals("Author not found with id: 999", exception.message)
        verify(authorRepository).update(authorId, request)
    }
}
