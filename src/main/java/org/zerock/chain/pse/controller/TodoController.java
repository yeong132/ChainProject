package org.zerock.chain.pse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.pse.dto.TodoDTO;
import org.zerock.chain.pse.dto.TodoRequestDTO;
import org.zerock.chain.pse.service.TodoService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // 리스트 등록
    @PostMapping("/add")
    public String addTodo(TodoRequestDTO todoRequestDTO) {
        todoService.saveOrUpdate(todoRequestDTO);
        return "redirect:/todo/list";
    }

    // 리스트 조회
    @GetMapping("/list")
    public String list(Model model) {
        // 전체 목록을 최신순으로 정렬
        List<TodoDTO> todos = todoService.getAllTodos().stream()
                .sorted(Comparator.comparing(TodoDTO::getTodoCreatedDate).reversed())
                .collect(Collectors.toList());

        // 오늘 날짜만 필터링
        LocalDate today = LocalDate.now();
        List<TodoDTO> todayTodos = todos.stream()
                .filter(todo -> todo.getTodoCreatedDate().equals(today))
                .collect(Collectors.toList());

        // 즐겨찾기 목록 필터링
        List<TodoDTO> favoriteTodos = todoService.getFavoriteTodos();

        // 완료된 항목 필터링
        List<TodoDTO> completedTodos = todoService.getCompletedTodos();

        // 진행 중인 항목 필터링
        List<TodoDTO> incompleteTodos = todoService.getIncompleteTodos();

        model.addAttribute("todos", todos);
        model.addAttribute("todayTodos", todayTodos);  // 오늘 날짜 항목
        model.addAttribute("favoriteTodos", favoriteTodos);
        model.addAttribute("completedTodos", completedTodos);
        model.addAttribute("incompleteTodos", incompleteTodos);

        return "todo/list";
    }

    // 완료 리스트 삭제
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

    // 즐겨찾기 상태 업데이트
    @PostMapping("/updateFavoriteStatus")
    public String updateFavoriteStatus(@RequestParam("todoNo") Long todoNo, @RequestParam("todoFavorite") boolean todoFavorite) {
        todoService.updateTodoFavoriteStatus(todoNo, todoFavorite);
        return "redirect:/todo/list";
    }
}
