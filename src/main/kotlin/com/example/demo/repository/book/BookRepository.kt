package com.example.demo.repository.book

import com.example.demo.jooq.tables.references.AUTHORS
import com.example.demo.jooq.tables.references.BOOKS
import com.example.demo.jooq.tables.references.BOOK_AUTHORS
import com.example.demo.model.author.AuthorResponse
import com.example.demo.model.book.BookRequest
import com.example.demo.model.book.BookResponse
import com.example.demo.model.book.PublicationStatus
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun create(request: BookRequest): BookResponse {
        val bookRecord = dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .set(BOOKS.PUBLICATION_STATUS, request.publicationStatus?.name)
            .returning()
            .fetchOne() ?: throw IllegalStateException("Failed to create book")

        val bookId = bookRecord.id!!

        val insertQueries = request.authorIds!!.map { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookId)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
        }
        dsl.batch(insertQueries).execute()

        val authors = fetchAuthorsForBook(bookId)

        return BookResponse(
            id = bookId,
            title = bookRecord.title!!,
            price = bookRecord.price!!,
            publicationStatus = PublicationStatus.valueOf(bookRecord.publicationStatus!!),
            authors = authors
        )
    }

    fun update(id: Long, request: BookRequest): BookResponse? {
        val bookRecord = dsl.update(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .set(BOOKS.PUBLICATION_STATUS, request.publicationStatus?.name)
            .where(BOOKS.ID.eq(id))
            .returning()
            .fetchOne() ?: return null

        dsl.deleteFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(id))
            .execute()

        val insertQueries = request.authorIds!!.map { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, id)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
        }
        dsl.batch(insertQueries).execute()

        val authors = fetchAuthorsForBook(id)

        return BookResponse(
            id = id,
            title = bookRecord.title!!,
            price = bookRecord.price!!,
            publicationStatus = PublicationStatus.valueOf(bookRecord.publicationStatus!!),
            authors = authors
        )
    }
    
    fun findById(id: Long): BookResponse? {
        val bookRecord = dsl.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne() ?: return null
            
        return BookResponse(
            id = id,
            title = bookRecord.title!!,
            price = bookRecord.price!!,
            publicationStatus = PublicationStatus.valueOf(bookRecord.publicationStatus!!),
            authors = fetchAuthorsForBook(id)
        )
    }

    fun findBooksByAuthorId(authorId: Long): List<BookResponse> {
        val records = dsl.select(BOOKS.asterisk())
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetchInto(BOOKS)

        return records.map { bookRecord ->
            val bookId = bookRecord.id!!
            BookResponse(
                id = bookId,
                title = bookRecord.title!!,
                price = bookRecord.price!!,
                publicationStatus = PublicationStatus.valueOf(bookRecord.publicationStatus!!),
                authors = fetchAuthorsForBook(bookId)
            )
        }
    }

    private fun fetchAuthorsForBook(bookId: Long): List<AuthorResponse> {
        return dsl.select(AUTHORS.asterisk())
            .from(AUTHORS)
            .join(BOOK_AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .fetchInto(AUTHORS)
            .map { authorRecord ->
                AuthorResponse(
                    id = authorRecord.id!!,
                    name = authorRecord.name!!,
                    birthDate = authorRecord.birthDate!!
                )
            }
    }
}
