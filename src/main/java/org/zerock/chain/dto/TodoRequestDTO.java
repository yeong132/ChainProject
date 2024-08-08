package org.zerock.chain.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Data
public class TodoRequestDTO {

    private Long todoNo;
    private String todoContent;
    private String todoCategory;
    private boolean todoStatus;
    private boolean todoFavorite;

    private LocalDateTime todoCreatedDate;

    public String getFormattedTodoCreatedDate() {
        return this.todoCreatedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
