package com.github.kaheero.loans;

import com.github.kaheero.book.BookEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

  private Long id;
  private String isbn;
  private String customer;
  private BookEntity book;
  private LocalDate startAt;
  private LocalDate endAt;
  private Boolean returned;

}
