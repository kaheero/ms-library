package com.github.kaheero.book;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

  BookEntity save(BookEntity book);

  Optional<BookEntity> getBookById(Long id);

  void delete(BookEntity bookEntity);

  BookEntity update(BookEntity bookEntity);

  Page<BookEntity> find(BookEntity bookEntity, Pageable pageable);

  Optional<BookEntity> getBookByIsbn(String isbn);

}
