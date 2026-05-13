package com.example.demo.model.author

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class AuthorRequest(
    @field:NotBlank(message = "Name must not be blank")
    val name: String?,

    @field:NotNull(message = "Birth date must not be null")
    @field:PastOrPresent(message = "Birth date must be in the past or present")
    val birthDate: LocalDate?
)

data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate
)
