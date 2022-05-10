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
public class BookDTOJsonTest {

    @Autowired
    private JacksonTester<BookDTO> json;
    private BookDTO bookDTO;

    @BeforeEach
    public void setup(){
        this.bookDTO = this.createNewValidBookDTO();
    }

    @Test
    @DisplayName("Serializes ID")
    public void idSerializes() throws IOException {
        JsonContent<BookDTO> jsonContent = this.json.write(bookDTO);

        Assertions.assertThat(jsonContent)
                .extractingJsonPathValue("$.id")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Deserialize ID")
    public void idDeserializes() throws IOException {
        Long id = this.json.parseObject(this.parseDtoToJson()).getId();
        Assertions.assertThat(id).isEqualTo(bookDTO.getId());
    }

    private BookDTO createNewValidBookDTO(){
        return BookDTO.builder()
                .id(1L)
                .title("Vinte mil leguas submarinas")
                .author("Julio Verne")
                .isbn("1234567890")
                .build();
    }

    private String parseDtoToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this.createNewValidBookDTO());
    }

}
