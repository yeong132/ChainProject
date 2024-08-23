package org.zerock.chain.pse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

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

    @Column(name = "emp_no")
    private Integer empNo = 1; // Integer로 변경하고 기본값 설정

    @Column(name = "todo_content")
    private String todoContent;

    @Column(name = "todo_category")
    private String todoCategory;

    @Column(name = "todo_status")
    private boolean todoStatus;

    @Column(name = "todo_favorite")
    private boolean todoFavorite;

    @Column(name = "todo_created_date")
    private LocalDate todoCreatedDate = LocalDate.now();

    public void setTodoStatus(boolean todoStatus) {
        this.todoStatus = todoStatus;
    }
    public void setTodoFavoriteStatus(boolean todoFavorite) {
        this.todoFavorite = todoFavorite;
    }

}
