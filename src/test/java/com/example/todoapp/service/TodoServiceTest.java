package com.example.todoapp.service;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo todo1;
    private Todo todo2;

    @BeforeEach
    void setUp() {
        todo1 = new Todo(1L, "Belajar CI/CD", "Pelajari GitHub Actions", false);
        todo2 = new Todo(2L, "Deploy App", "Deploy ke production", true);
    }

    @Test
    void testGetAllTodos_ReturnsList() {
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo1, todo2));
        List<Todo> result = todoService.getAllTodos();
        assertEquals(2, result.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void testGetTodoById_Found() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        Optional<Todo> result = todoService.getTodoById(1L);
        assertTrue(result.isPresent());
        assertEquals("Belajar CI/CD", result.get().getTitle());
    }

    @Test
    void testGetTodoById_NotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Todo> result = todoService.getTodoById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateTodo_Success() {
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);
        Todo result = todoService.createTodo(todo1);
        assertNotNull(result);
        assertEquals("Belajar CI/CD", result.getTitle());
        verify(todoRepository, times(1)).save(todo1);
    }

    @Test
    void testUpdateTodo_Success() {
        Todo updated = new Todo(1L, "Updated Title", "Updated Desc", true);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(updated);
        Todo result = todoService.updateTodo(1L, updated);
        assertEquals("Updated Title", result.getTitle());
        assertTrue(result.isCompleted());
    }

    @Test
    void testUpdateTodo_NotFound_ThrowsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
            () -> todoService.updateTodo(99L, todo1));
    }

    @Test
    void testDeleteTodo_Success() {
        doNothing().when(todoRepository).deleteById(1L);
        assertDoesNotThrow(() -> todoService.deleteTodo(1L));
        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetTodosByStatus_Completed() {
        when(todoRepository.findByCompleted(true))
            .thenReturn(Arrays.asList(todo2));
        List<Todo> result = todoService.getTodosByStatus(true);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }
}