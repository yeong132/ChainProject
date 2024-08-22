package org.zerock.chain.pse.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.pse.dto.TodoDTO;
import org.zerock.chain.pse.dto.TodoRequestDTO;
import org.zerock.chain.pse.service.TodoService;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Controller
@RequestMapping("/todo")
public class TodoController {
// 등록, 조회, 삭제 메서드 추가

    @Autowired
    private TodoService todoService;

    // 리스트 등록
    @PostMapping("/add")
    public String addTodo(TodoRequestDTO todoRequestDTO) {
        todoService.saveOrUpdate(todoRequestDTO);
        return "redirect:/todo/list";
    }

    // 리스트 조회
    @GetMapping("/list")
    public String list(Model model) {
        List<TodoDTO> todos = todoService.getAllTodos();
        List<TodoDTO> favoriteTodos = todos.stream()
                .filter(TodoDTO::isTodoFavorite)
                .collect(Collectors.toList());
        List<TodoDTO> completedTodos = todos.stream()
                .filter(TodoDTO::isTodoStatus)
                .collect(Collectors.toList());
        List<TodoDTO> incompleteTodos = todos.stream()
                .filter(todo -> !todo.isTodoStatus())
                .collect(Collectors.toList());

        model.addAttribute("todos", todos);
        model.addAttribute("favoriteTodos", favoriteTodos);
        model.addAttribute("completedTodos", completedTodos);
        model.addAttribute("incompleteTodos", incompleteTodos);
        return "todo/list";
    }

    //  완료 리스트 삭제
    @GetMapping("/deleteCompleted")
    public String deleteCompletedTodos() {
        todoService.deleteCompletedTodos();
        return "redirect:/todo/list";
    }

    // 상태 업데이트 (완료여부)
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("todoNo") Long todoNo, @RequestParam("todoStatus") boolean todoStatus) {
        todoService.updateTodoStatus(todoNo, todoStatus);
        return "redirect:/todo/list";
    }

}
