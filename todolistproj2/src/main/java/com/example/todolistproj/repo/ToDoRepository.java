package com.example.todolistproj.repo;

import com.example.todolistproj.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    public List<ToDo> findAllByUserId(int userId);
}
