package com.github.kaheero.book;

import com.github.kaheero.exceptions.BussinessException;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

  private BookRepository repository;

  @Override
  public BookEntity save(BookEntity book) {
    if (repository.existsByIsbn(book.getIsbn())) {
      throw new BussinessException("isbn já cadastrado");
    }
    return repository.save(book);
  }

  @Override
  public Optional<BookEntity> getBookById(Long id) {
    return this.repository.findById(id);
  }

  @Override
  public void delete(BookEntity bookEntity) {
    if (Objects.isNull(bookEntity) || Objects.isNull(bookEntity.getId())){
      throw new IllegalArgumentException("Book and book id cant be null");
    }
    this.repository.delete(bookEntity);
  }

  @Override
  public BookEntity update(BookEntity bookEntity) {
    if (Objects.isNull(bookEntity) || Objects.isNull(bookEntity.getId())){
      throw new IllegalArgumentException("Book and book id cant be null");
    }
    return this.repository.save(bookEntity);
  }

  @Override
  public Page<BookEntity> find(BookEntity bookEntity, Pageable pageable) {
    return null;
  }

}
