package com.github.kaheero.book;

import com.github.kaheero.exceptions.ApiErrors;
import com.github.kaheero.exceptions.BusinessException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

  private final BookService service;
  private final ModelMapper mapper;

  @GetMapping
  @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
  public Page<BookDTO> findBooks(BookDTO bookDTO, Pageable pageRequest){
    BookEntity bookEntity = mapper.map(bookDTO, BookEntity.class);
    Page<BookEntity> result = service.find(bookEntity, pageRequest);
    List<BookDTO> books = result.getContent()
            .stream()
            .map(entity -> mapper.map(entity, BookDTO.class))
            .collect(Collectors.toList());
    return new PageImpl<>(books, pageRequest, result.getTotalElements());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookDTO createBook(@RequestBody @Valid BookDTO bookDTO) {
    BookEntity entity = mapper.map(bookDTO, BookEntity.class);
    return mapper.map(service.save(entity), BookDTO.class);
  }

  @GetMapping(path = "/{id}")
  public BookDTO getBookById(@PathVariable Long id) {
    return service.getBookById(id)
        .map(book -> mapper.map(book, BookDTO.class))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBookById(@PathVariable Long id) {
    BookEntity bookEntity = service.getBookById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    service.delete(bookEntity);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public BookDTO updateBookById(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
    return service.getBookById(id).map(book -> {
          book.setAuthor(bookDTO.getAuthor());
          book.setTitle(bookDTO.getTitle());
          return mapper.map(service.update(book), BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
    BindingResult bindingResult = exception.getBindingResult();
    return new ApiErrors(bindingResult);
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleValidationExceptions(BusinessException exception) {
    return new ApiErrors(exception);
  }

}
