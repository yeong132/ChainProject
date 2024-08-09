package org.zerock.chain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {

    private Long todoNo;
    private String todoContent;
    private String todoCategory;
    private boolean todoStatus;
    private boolean todoFavorite;

    private LocalDateTime todoCreatedDate;

    public String getFormattedTodoCreatedDate() {
        return this.todoCreatedDate != null ? this.todoCreatedDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : "";
    }

}
