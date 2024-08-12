package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.chain.dto.TodoDTO;
import org.zerock.chain.dto.TodoRequestDTO;
import org.zerock.chain.model.Todo;
import org.zerock.chain.repository.TodoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 등록, 조회, 삭제 메서드 구현
@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ModelMapper modelMapper;

    // 리스트 등록
    @Override
    public void saveOrUpdate(TodoRequestDTO todoRequestDTO) {
        Todo todo = modelMapper.map(todoRequestDTO, Todo.class);
        todoRepository.save(todo);
    }

    // 전체 리스트 조회
    @Override
    public List<TodoDTO> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(todo -> convertToDto(todo))
                .collect(Collectors.toList());
    }

    // 즐겨찾기 리스트
    @Override
    public List<TodoDTO> getFavoriteTodos() {
        return todoRepository.findAll().stream()
                .filter(Todo::isTodoFavorite)
                .map(todo -> convertToDto(todo))
                .collect(Collectors.toList());
    }

    // 완료상태 리스트
    @Override
    public List<TodoDTO> getCompletedTodos() {
        return todoRepository.findAll().stream()
                .filter(Todo::isTodoStatus)
                .map(todo -> convertToDto(todo))
                .collect(Collectors.toList());
    }

    // 미완료 상태 리스트
    @Override
    public List<TodoDTO> getIncompleteTodos() {
        return todoRepository.findAll().stream()
                .filter(todo -> !todo.isTodoStatus())
                .map(todo -> convertToDto(todo))
                .collect(Collectors.toList());
    }
    private TodoDTO convertToDto(Todo todo) {
        return new ModelMapper().map(todo, TodoDTO.class);
    }

    @Override
    public void deleteCompletedTodos() {
        List<Todo> completedTodos = todoRepository.findByTodoStatus(true);
        todoRepository.deleteAll(completedTodos);
    }

    // 완료상태 여부 전송
    public void updateTodoStatus(Long todoNo, boolean todoStatus) {
        Todo todo = todoRepository.findById(todoNo)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo ID: " + todoNo));
        todo.setTodoStatus(todoStatus);
        todoRepository.save(todo);
    }


}

