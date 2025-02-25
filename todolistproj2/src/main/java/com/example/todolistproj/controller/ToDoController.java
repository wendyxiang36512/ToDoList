package com.example.todolistproj.controller;

import com.example.todolistproj.entity.ToDo;
import com.example.todolistproj.entity.UserInfo;
import com.example.todolistproj.repo.ToDoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todos")  // Standard RESTful path
@CrossOrigin("*") // Allow cross-origin requests (limit this in production)
public class ToDoController {

    private final ToDoRepository toDoRepository;

    public ToDoController(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    /**
     * Get all To-Do list items from the database
     */
    @GetMapping
    public ResponseEntity<List<ToDo>> getAllToDos(HttpSession session) {
        UserInfo currentUser = (UserInfo)session.getAttribute("user");
        return ResponseEntity.ok(toDoRepository.findAllByUserId(currentUser.getId()));
    }

    /**
     * Add a new To-Do item
     */
    @PostMapping
    public ResponseEntity<ToDo> addToDo(@RequestBody ToDo todo, HttpSession session) {
        UserInfo currentUser = (UserInfo)session.getAttribute("user");
        todo.setUserId(currentUser.getId());
        return ResponseEntity.ok(toDoRepository.save(todo));
    }

    /**
     * Update an existing To-Do item
     */
    @PutMapping("/{id}")
    public ResponseEntity<ToDo> updateToDo(@PathVariable Long id, @RequestBody ToDo updatedToDo) {
        Optional<ToDo> existingToDo = toDoRepository.findById(id);
        if (existingToDo.isPresent()) {
            ToDo todo = existingToDo.get();
            todo.setTask(updatedToDo.getTask());
            todo.setDeadline(updatedToDo.getDeadline());
            return ResponseEntity.ok(toDoRepository.save(todo));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a To-Do item
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable Long id) {
        if (toDoRepository.existsById(id)) {
            toDoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
