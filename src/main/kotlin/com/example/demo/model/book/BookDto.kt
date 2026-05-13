package com.example.demo.model.book

import com.example.demo.model.author.AuthorResponse
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

enum class PublicationStatus {
    UNPUBLISHED,
    PUBLISHED
}

data class BookRequest(
    @field:NotBlank(message = "Title must not be blank")
    val title: String?,

    @field:NotNull(message = "Price must not be null")
    @field:Min(value = 0, message = "Price must be 0 or greater")
    val price: Int?,

    @field:NotNull(message = "Publication status must not be null")
    val publicationStatus: PublicationStatus?,

    @field:NotEmpty(message = "A book must have at least one author")
    val authorIds: List<Long>?
)

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
    val authors: List<AuthorResponse>
)
