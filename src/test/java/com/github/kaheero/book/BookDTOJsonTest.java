package com.github.kaheero.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@JsonTest
@ExtendWith(SpringExtension.class)
class BookDTOJsonTest {

  @Autowired
  private JacksonTester<BookDTO> json;
  private BookDTO bookDTO;

  private static final Long ID = 1L;
  private static final String TITLE = "Vinte mil leguas submarinas";
  private static final String AUTHOR = "Julio Verne";
  private static final String ISBN = "1234567890";

  @BeforeEach
  public void setup(){
    this.bookDTO = this.createNewValidBookDTO();
  }

  @Test
  @DisplayName("Serializes id.")
  void idSerializes() throws IOException {
    JsonContent<BookDTO> jsonContent = this.json.write(bookDTO);

    Assertions.assertThat(jsonContent)
        .extractingJsonPathValue("$.id")
        .isEqualTo(1);
  }

  @Test
  @DisplayName("Deserialize id.")
  void idDeserializes() throws IOException {
    final Long bookId = 1L;

    Long id = this.json.parseObject(this.parseDtoToJson()).getId();

    Assertions.assertThat(bookId).isEqualTo(id);
  }

  @Test
  @DisplayName("Serializes title.")
  void titleSerializes() throws IOException {
    JsonContent<BookDTO> jsonContent = this.json.write(bookDTO);

    Assertions.assertThat(jsonContent)
        .extractingJsonPathValue("$.title")
        .isEqualTo(TITLE);
  }

  @Test
  @DisplayName("Deserialize title.")
  void titleDeserialize() throws IOException {
    String title = this.json.parseObject(this.parseDtoToJson()).getTitle();

    Assertions.assertThat(title).isEqualTo(TITLE);
  }

  @Test
  @DisplayName("Serializes author.")
  void authorSerializes() throws IOException {
    JsonContent<BookDTO> jsonContent = this.json.write(bookDTO);

    Assertions.assertThat(jsonContent)
        .extractingJsonPathValue("$.author")
        .isEqualTo(AUTHOR);
  }

  @Test
  @DisplayName("Deserialize author.")
  void authorDeserialize() throws IOException {
    String author = this.json.parseObject(this.parseDtoToJson()).getAuthor();

    Assertions.assertThat(author).isEqualTo(AUTHOR);
  }

  @Test
  @DisplayName("Serializes isbn.")
  void isbnSerializes() throws IOException {
    JsonContent<BookDTO> jsonContent = this.json.write(bookDTO);

    Assertions.assertThat(jsonContent)
        .extractingJsonPathValue("$.isbn")
        .isEqualTo(ISBN);
  }

  @Test
  @DisplayName("Deserialize isbn.")
  void isbnDeserialize() throws IOException {
    String isbn = this.json.parseObject(this.parseDtoToJson()).getIsbn();

    Assertions.assertThat(isbn).isEqualTo(ISBN);
  }

  private BookDTO createNewValidBookDTO(){
    return BookDTO.builder()
        .id(ID)
        .title(TITLE)
        .author(AUTHOR)
        .isbn(ISBN)
        .build();
  }

  private String parseDtoToJson() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(this.createNewValidBookDTO());
  }

}
