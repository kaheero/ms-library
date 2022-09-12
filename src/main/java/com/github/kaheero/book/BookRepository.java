package com.github.kaheero.book;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

  boolean existsByIsbn(String isbn);

  Optional<BookEntity> findById(Long id);

  Optional<BookEntity> findByIsbn(String isbn);

}
