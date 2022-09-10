package com.github.kaheero.loans;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaheero.book.BookEntity;
import com.github.kaheero.book.BookService;
import java.time.LocalDate;
import java.util.Optional;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.github.kaheero")
public class LoanControllerTest {

  private static final String API_PATH_LOANS = "/loans";

  @Autowired
  MockMvc mvc;

  @MockBean
  private BookService bookService;

  @MockBean
  private LoanService loanService;

  @Test
  @DisplayName("deve realizar um empréstimo")
  void createLoansTest() throws Exception {

    BookEntity bookEntity = BookEntity.builder()
        .id(1L)
        .isbn("123")
        .build();

    BDDMockito
        .given(bookService.getBookByIsbn("123"))
        .willReturn(Optional.of(bookEntity));

    LoanEntity loanEntity = LoanEntity.builder()
        .id(1L)
        .isbn("123")
        .customer("John Doe")
        .book(bookEntity)
        .startAt(LocalDate.now())
        .returned(Boolean.FALSE)
        .build();

    BDDMockito
        .given(loanService.save(Mockito.any(LoanEntity.class)))
        .willReturn(loanEntity);

    LoanDTO dto = LoanDTO.builder()
        .isbn("123")
        .customer("John Doe")
        .build();

    String payload = new ObjectMapper().writeValueAsString(dto);

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
        .post(API_PATH_LOANS)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(payload);

    mvc.perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(content().string("1"));
  }

}
