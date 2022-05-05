package com.github.kaheero.book;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@EnableAutoConfiguration
class BookRepositoryTest {

  @Autowired
  TestEntityManager entityManager;

  @Autowired
  private BookRepository repository;

  @Test
  @DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado.")
  void returnTrueWhenIsbnExists() {
    // cenário
    final String isbn = "978-85-7657-313-5";
    entityManager.persist(this.createValidBook());

    // execução
    boolean exists = repository.existsByIsbn(isbn);

    // verificação
    Assertions.assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Deve retornar falso quando não existir um livro na base com isbn informado.")
  void returnFalseWhenIsbnDoesntExists() {
    // cenário
    final String isbn = "123456";

    // execução
    boolean exists = repository.existsByIsbn(isbn);

    // verificação
    Assertions.assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Deve retornar um livro por id")
  void findBookById() {
    // cenário
    final Long bookId = 1L;

    BookEntity book = this.createValidBook();

    entityManager.persist(book);

    // execução
    Optional<BookEntity> foundBook = repository.findById(bookId);

    // verificação
    Assertions.assertThat(foundBook).isPresent();
  }

  @Test
  @DisplayName("Deve atualizar um livro com sucesso.")
  void saveBookTest() {
    // cenário
    BookEntity book = this.createValidBook();

    // execução
    BookEntity savedBook = this.repository.save(book);

    // verificação
    Assertions.assertThat(savedBook.getId()).isNotNull();
  }

  @Test
  @DisplayName("Deve deletar um livro com sucesso.")
  void deleteBookTest() {
    // cenário
    BookEntity book = this.createValidBook();
    this.entityManager.persist(book);
    BookEntity foundBook = this.entityManager.find(BookEntity.class, book.getId());

    // execução
    this.repository.delete(foundBook);
    BookEntity deleteBook = this.entityManager.find(BookEntity.class, book.getId());

    // verificação
    Assertions.assertThat(deleteBook).isNull();
  }

  private BookEntity createValidBook() {
    return BookEntity.builder()
        .title("Duna")
        .author("Frank Hebert")
        .isbn("978-85-7657-313-5")
        .build();
  }

}
