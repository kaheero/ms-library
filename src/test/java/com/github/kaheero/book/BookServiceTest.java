package com.github.kaheero.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.kaheero.exceptions.BussinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

  private BookService service;

  @MockBean
  private BookRepository repository;

  @BeforeEach
  public void setup() {
    this.service = new BookServiceImpl(repository);
  }

  @Test
  @DisplayName("Deve salvar um livro.")
  void saveBookTest() {
    // cenário
    BookEntity book = BookEntity.builder()
        .title("Vinte mil léguas submarinas.")
        .author("Julio Verne")
        .isbn("123")
        .build();

    Mockito
        .when(repository.existsByIsbn(Mockito.anyString()))
        .thenReturn(false);

    Mockito
        .when(repository.save(book))
        .thenReturn(BookEntity.builder()
            .id(1L)
            .title("Vinte mil léguas submarinas.")
            .author("Julio Verne")
            .isbn("123")
            .build());

    // execução
    BookEntity saveBook = service.save(book);

    // verificação
    assertThat(saveBook.getId()).isNotNull();
    assertThat(saveBook.getTitle()).isEqualTo(book.getTitle());
    assertThat(saveBook.getAuthor()).isEqualTo(book.getAuthor());
    assertThat(saveBook.getIsbn()).isEqualTo(book.getIsbn());
  }

  @Test
  @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado.")
  void shouldNotSaveABookWithDuplicatedISBN() {
    // cenário
    BookEntity book = this.createValidBook();
    Mockito
        .when(repository.existsByIsbn(Mockito.anyString()))
        .thenReturn(true);

    // execução
    Throwable throwable = Assertions.catchThrowable(() -> service.save(book));

    // verificação
    assertThat(throwable)
        .isInstanceOf(BussinessException.class)
        .hasMessage("isbn já cadastrado");
    Mockito.verify(repository, Mockito.never()).save(book);
  }

  @Test
  @DisplayName("Deve obter um livro por id")
  void getBookByIdTest() {
    // cenário
    final Long bookId = 1L;

    BookEntity book = this.createValidBook();
    book.setId(bookId);

    Mockito
        .when(repository.findById(bookId))
        .thenReturn(Optional.of(book));

    // execução
    Optional<BookEntity> foundBook = service.getBookById(bookId);

    // verificação
    assertThat(foundBook).isPresent();
    assertThat(foundBook.get().getId()).isEqualTo(book.getId());
    assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
    assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
  }

  @Test
  @DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existir na base")
  void bookNotFoundByIdTest() {
    // cenário
    final Long bookId = 1L;

    BDDMockito
        .when(repository.findById(bookId))
        .thenReturn(Optional.empty());

    // execução
    Optional<BookEntity> bookNotFound = service.getBookById(bookId);

    // verificações
    assertThat(bookNotFound).isEmpty();
  }

  @Test
  @DisplayName("Deve deletar um livro com sucesso.")
  void deleteBook() {
    // cenário
    BookEntity book = BookEntity.builder()
        .id(1L)
        .build();

    // execução
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> this.service.delete(book));

    // verificação
    Mockito
        .verify(this.repository, Mockito.times(1))
        .delete(book);
  }

  @Test
  @DisplayName("Deve lançar um erro ao deletar um livro com id inexistente.")
  void deleteBookwithException() {
    // cenário
    BookEntity book = BookEntity.builder()
        .title("title")
        .author("author")
        .isbn("isbn")
        .build();

    // execução
    Throwable exception = Assertions.catchThrowable(() -> this.service.delete(book));

    // verificação
    assertThat(exception)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Book and book id cant be null");
    Mockito
        .verify(this.repository, Mockito.never())
        .delete(book);
  }

  @Test
  @DisplayName("Deve atualizar um livro com sucesso.")
  void updateBookTest() {
    // cenário
    final Long bookId = 1L;

    // livro a atualizar
    BookEntity updatingBook = BookEntity.builder()
        .id(bookId)
        .title("title")
        .author("author")
        .isbn("isbn")
        .build();

    // simulação
    BookEntity updatedBook = this.createValidBook();
    updatedBook.setId(bookId);

    Mockito
        .when(repository.save(updatingBook))
        .thenReturn(updatedBook);

    // execução
    BookEntity book = service.update(updatingBook);

    // verificação
    assertThat(book).isNotNull();
    assertThat(book.getId()).isEqualTo(updatedBook.getId());
    assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
    assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
  }

  @Test
  @DisplayName("Deve lançar uma excessão ao atualizar livro com id inexistente.")
  void givenUpdateBookWithIdNullWhenReturnIllegalArgumentException() {
    // cenário
    BookEntity book = BookEntity.builder()
        .title("title")
        .author("author")
        .isbn("isbn")
        .build();

    // execução
    Throwable throwable = Assertions.catchThrowable(() -> service.update(book));

    // verificação
    assertThat(throwable)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Book and book id cant be null");
    Mockito
        .verify(this.repository, Mockito.never())
        .save(book);
  }

  @Test
  @DisplayName("Deve filtrar os livros pelas propriedades")
  public void findBookTest() {
    // cenario
    final int NUMBER_PAGE = 0;
    final int TOTAL_ELEMENTS_FOR_PAGE = 10;
    BookEntity book = this.createValidBook();
    List<BookEntity> books = new ArrayList<>();
    books.add(book);
    PageRequest pageRequest = PageRequest.of(NUMBER_PAGE, TOTAL_ELEMENTS_FOR_PAGE);
    Page<BookEntity> page = new PageImpl<>(books, pageRequest, 1);

    BDDMockito
            .when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
            .thenReturn(page);

    // execucao
    Page<BookEntity> pageOfBooks = service.find(book, pageRequest);

    // verificacao
    assertThat(pageOfBooks.getTotalElements()).isEqualTo(1);
    assertThat(pageOfBooks.getContent()).isNotEmpty();
    assertThat(pageOfBooks.getContent()).hasSize(1);
    assertThat(pageOfBooks.getContent()).isEqualTo(books);
    assertThat(pageOfBooks.getPageable().getPageNumber()).isZero();
    assertThat(pageOfBooks.getPageable().getPageSize()).isEqualTo(TOTAL_ELEMENTS_FOR_PAGE);
  }

  private BookEntity createValidBook() {
    return BookEntity.builder()
        .title("Vinte mil léguas submarinas.")
        .author("Julio Verne")
        .isbn("123")
        .build();
  }


}
