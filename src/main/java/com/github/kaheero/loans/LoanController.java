package com.github.kaheero.loans;

import com.github.kaheero.book.BookEntity;
import com.github.kaheero.book.BookService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/loans")
public class LoanController {

  private final LoanService loanService;
  private final BookService bookService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Long create(@RequestBody LoanDTO loanDTO) {

    BookEntity book = bookService.getBookByIsbn(loanDTO.getIsbn())
        .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found passed isbn"));

    LoanEntity loanEntity = LoanEntity.builder()
        .book(book)
        .customer(loanDTO.getCustomer())
        .startAt(LocalDate.now())
        .build();

    loanEntity = loanService.save(loanEntity);
    return loanEntity.getId();
  }


}
