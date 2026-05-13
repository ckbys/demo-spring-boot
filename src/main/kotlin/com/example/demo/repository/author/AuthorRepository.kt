package com.example.demo.repository.author

import com.example.demo.jooq.tables.references.AUTHORS
import com.example.demo.model.author.AuthorRequest
import com.example.demo.model.author.AuthorResponse
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(private val dsl: DSLContext) {

    fun create(request: AuthorRequest): AuthorResponse {
        val record = dsl.insertInto(AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .returning()
            .fetchOne() ?: throw IllegalStateException("Failed to create author")
            
        return AuthorResponse(
            id = record.id!!,
            name = record.name!!,
            birthDate = record.birthDate!!
        )
    }

    fun update(id: Long, request: AuthorRequest): AuthorResponse? {
        val record = dsl.update(AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .where(AUTHORS.ID.eq(id))
            .returning()
            .fetchOne() ?: return null

        return AuthorResponse(
            id = record.id!!,
            name = record.name!!,
            birthDate = record.birthDate!!
        )
    }

    fun existsByIds(ids: List<Long>): Boolean {
        if (ids.isEmpty()) return false
        val count = dsl.selectCount()
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetchOne(0, Int::class.java) ?: 0
        return count == ids.size
    }
}
