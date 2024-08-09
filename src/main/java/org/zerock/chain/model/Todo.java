package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todo")
@ToString
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_no")
    private Long todoNo;

    @Column(name = "todo_content")
    private String todoContent;

    @Column(name = "todo_category")
    private String todoCategory;

    @Column(name = "todo_status")
    private boolean todoStatus;

    @Column(name = "todo_favorite")
    private boolean todoFavorite;


    @Column(name = "todo_created_date", updatable = false)
    private LocalDateTime todoCreatedDate;

    @PrePersist
    protected void onCreate() {
        this.todoCreatedDate = LocalDateTime.now();
    }

    public String getFormattedTodoCreatedDate() {
        return this.todoCreatedDate != null ? this.todoCreatedDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : "";
    }

    public void setTodoStatus(boolean todoStatus) {
        this.todoStatus = todoStatus;
    }

}
