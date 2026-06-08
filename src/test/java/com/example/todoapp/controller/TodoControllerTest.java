package com.example.todoapp.controller;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo todo1;
    private Todo todo2;

    @BeforeEach
    void setUp() {
        todo1 = new Todo(1L, "Belajar CI/CD", "Pelajari GitHub Actions", false);
        todo2 = new Todo(2L, "Deploy App", "Deploy ke production", true);
    }

    @Test
    void testGetAllTodos_Returns200() throws Exception {
        when(todoService.getAllTodos()).thenReturn(Arrays.asList(todo1, todo2));
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Belajar CI/CD"));
    }

    @Test
    void testGetTodoById_Found_Returns200() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(Optional.of(todo1));
        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Belajar CI/CD"));
    }

    @Test
    void testGetTodoById_NotFound_Returns404() throws Exception {
        when(todoService.getTodoById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/todos/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTodo_Returns200() throws Exception {
        when(todoService.createTodo(any(Todo.class))).thenReturn(todo1);
        mockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(todo1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Belajar CI/CD"));
    }

    @Test
    void testUpdateTodo_Found_Returns200() throws Exception {
        when(todoService.updateTodo(eq(1L), any(Todo.class))).thenReturn(todo1);
        mockMvc.perform(put("/api/todos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(todo1)))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateTodo_NotFound_Returns404() throws Exception {
        when(todoService.updateTodo(eq(99L), any(Todo.class)))
            .thenThrow(new RuntimeException("Not found"));
        mockMvc.perform(put("/api/todos/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(todo1)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTodo_Returns204() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);
        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGetTodosByStatus_Returns200() throws Exception {
        when(todoService.getTodosByStatus(true))
            .thenReturn(Arrays.asList(todo2));
        mockMvc.perform(get("/api/todos/status/true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }
}