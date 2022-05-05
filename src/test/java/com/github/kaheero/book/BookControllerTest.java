package com.github.kaheero.book;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaheero.exceptions.BussinessException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.github.kaheero")
class BookControllerTest {

  private static final String API_PATH_BOOKS = "/books";

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookService bookService;

  @Test
  @DisplayName("Deve criar um livro com sucesso.")
  void createBookTest() throws Exception {

    // cenário
    BookDTO bookDTO = BookDTO.builder()
        .title("As aventuras")
        .author("Artur")
        .isbn("123456789")
        .build();

    BookEntity bookSave = BookEntity.builder()
        .id(1L)
        .title("As aventuras")
        .author("Artur")
        .isbn("123456789")
        .build();

    BDDMockito
        .given(bookService.save(Mockito.any(BookEntity.class)))
        .willReturn(bookSave);

    // execução
    String json = new ObjectMapper().writeValueAsString(bookDTO);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(API_PATH_BOOKS)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    // verificação
    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").isNotEmpty())
        .andExpect(jsonPath("title").value(bookDTO.getTitle()))
        .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
        .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
  }

  @Test
  @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para a criação.")
  void createInvalidBookTest() throws Exception {
    // cenário
    BookDTO bookDTO = BookDTO.builder().build();

    // execução
    String json = new ObjectMapper().writeValueAsString(bookDTO);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(API_PATH_BOOKS)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    // verificação
    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("erros", Matchers.hasSize(3)));
  }

  @Test
  @DisplayName("Deve lançar erro ao cadastrar um livro com isbn já existente.")
  void createBookWithDuplicatedIsbnTest() throws Exception {

    String json = new ObjectMapper().writeValueAsString(this.buildBookDTO());
    final String errorMessage = "isbn já cadastrado";

    BDDMockito
        .when(bookService.save(Mockito.any(BookEntity.class)))
        .thenThrow(new BussinessException(errorMessage));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(API_PATH_BOOKS)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("erros", Matchers.hasSize(1)))
        .andExpect(jsonPath("erros[0]").value(errorMessage));
  }

  @Test
  @DisplayName("Deve obter as informações de um livro.")
  void returnBookDetailsTest() throws Exception {
    // cenário (given)
    final Long id = 1L;
    BookEntity book = BookEntity.builder()
        .id(id)
        .title(buildBookDTO().getTitle())
        .author(buildBookDTO().getAuthor())
        .isbn(buildBookDTO().getIsbn())
        .build();

    BDDMockito
        .when(bookService.getBookById(1L))
        .thenReturn(Optional.of(book));

    // execução (when)
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .get(API_PATH_BOOKS.concat("/" + id))
        .accept(MediaType.APPLICATION_JSON);

    // verificação (then)
    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id))
        .andExpect(jsonPath("title").value(buildBookDTO().getTitle()))
        .andExpect(jsonPath("author").value(buildBookDTO().getAuthor()))
        .andExpect(jsonPath("isbn").value(buildBookDTO().getIsbn()));
  }

  @Test
  @DisplayName("Deve retornar resource not found quando o livro não existir.")
  void bookNotFoundTest() throws Exception {
    // cenário e execução
    BDDMockito
        .when(bookService.getBookById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .get(API_PATH_BOOKS.concat("/" + 1))
        .accept(MediaType.APPLICATION_JSON);

    // verificação
    mvc.perform(requestBuilder)
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve deletar um livro.")
  void deleteBookTest() throws Exception {
    // cenário
    final Long bookId = 1L;
    BookEntity book = BookEntity.builder()
        .id(bookId)
        .build();

    BDDMockito
        .when(bookService.getBookById(bookId))
        .thenReturn(Optional.of(book));

    // execução
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .delete(API_PATH_BOOKS.concat("/" + bookId));

    // verificação
    mvc.perform(requestBuilder)
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar.")
  void deleteInexistenteBookTest() throws Exception {
    // cenário
    BDDMockito
        .when(bookService.getBookById(1L))
        .thenReturn(Optional.empty());

    // execução
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .delete(API_PATH_BOOKS.concat("/" + 1));

    // verificação
    mvc.perform(requestBuilder)
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve atualizar um livro.")
  void updateBookTest() throws Exception {
    // cenário
    final Long bookId = 1L;
    final String json = new ObjectMapper().writeValueAsString(this.buildBookDTO());

    BookEntity bookEntity = BookEntity.builder()
        .id(bookId)
        .title("some title")
        .author("some author")
        .isbn("321")
        .build();

    BookEntity updatedBook = BookEntity.builder()
        .id(bookId)
        .author("Artur")
        .title("As aventuras")
        .isbn("321")
        .build();

    BDDMockito
        .when(bookService.getBookById(bookId))
        .thenReturn(Optional.of(bookEntity));

    BDDMockito
        .when(bookService.update(bookEntity))
        .thenReturn(updatedBook);

    // execução
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .put(API_PATH_BOOKS.concat("/" + bookId))
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    // verificação
    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(bookId))
        .andExpect(jsonPath("title").value(updatedBook.getTitle()))
        .andExpect(jsonPath("author").value(updatedBook.getAuthor()))
        .andExpect(jsonPath("isbn").value(updatedBook.getIsbn()));
  }

  @Test
  @DisplayName("Deve retornar not found ao tentar atualizar um livro inexistente")
  void updateInexistenteBookTest() throws Exception {
    // cenário
    final String json = new ObjectMapper().writeValueAsString(this.buildBookDTO());

    BDDMockito
        .when(bookService.getBookById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    // execução
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .put(API_PATH_BOOKS.concat("/" + Mockito.anyLong()))
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    // verificação
    mvc.perform(requestBuilder)
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve filtrar livros.")
  void findBookTest() throws Exception {
    // cenário
    final Long bookId = 1L;

    BookEntity book = BookEntity.builder()
        .id(bookId)
        .title(buildBookDTO().getTitle())
        .author(buildBookDTO().getAuthor())
        .isbn(buildBookDTO().getIsbn())
        .build();

    Page<BookEntity> page = new PageImpl<>(List.of(book), PageRequest.of(0, 100), 1);

    BDDMockito
        .given(bookService.find(Mockito.any(BookEntity.class), Mockito.any(Pageable.class)))
        .willReturn(page);

    final String queryString = String.format("?title=%s&author=%s&page=0&size=100",
        book.getTitle(),
        book.getAuthor());

    // execução
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .get(API_PATH_BOOKS.concat(queryString))
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    // verificação
    mvc.perform(requestBuilder)
        .andExpect(status().isPartialContent())
        .andExpect(jsonPath("content", Matchers.hasSize(1)))
        .andExpect(jsonPath("totalElements").value(1))
        .andExpect(jsonPath("pageable.pageSize").value(100))
        .andExpect(jsonPath("pageable.pageNumber").value(0));
  }

  private BookDTO buildBookDTO() {
    return BookDTO.builder()
        .title("As aventuras")
        .author("Artur")
        .isbn("123456789")
        .build();
  }

}
