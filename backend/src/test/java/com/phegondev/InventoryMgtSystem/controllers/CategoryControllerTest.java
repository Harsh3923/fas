package com.phegondev.InventoryMgtSystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phegondev.InventoryMgtSystem.dtos.CategoryDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.services.CategoryService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)     // <-- disables security filters
@Import(CategoryController.class)             // <-- explicitly load the controller only
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    // 🔥 Mock JwtUtils (IMPORTANT!!)
    @MockBean
    private com.phegondev.InventoryMgtSystem.security.JwtUtils jwtUtils;

    // 🔥 Mock CustomUserDetailsService (VERY IMPORTANT!!)
    @MockBean
    private com.phegondev.InventoryMgtSystem.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_success() throws Exception {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");

        Response res = Response.builder()
                .status(200)
                .message("Category added")
                .build();

        Mockito.when(categoryService.createCategory(Mockito.any())).thenReturn(res);

        mockMvc.perform(post("/api/categories/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Category added"));
    }

    @Test
    void getAllCategories_success() throws Exception {

        Response res = Response.builder()
                .status(200)
                .message("All categories returned")
                .build();

        Mockito.when(categoryService.getAllCategories()).thenReturn(res);

        mockMvc.perform(get("/api/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void getCategoryById_success() throws Exception {

        Response res = Response.builder()
                .status(200)
                .message("Category found")
                .build();

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category found"));
    }

    @Test
    void updateCategory_success() throws Exception {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Updated");

        Response res = Response.builder()
                .status(200)
                .message("Category updated")
                .build();

        Mockito.when(categoryService.updateCategory(Mockito.eq(1L), Mockito.any()))
                .thenReturn(res);

        mockMvc.perform(put("/api/categories/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category updated"));
    }

    @Test
    void deleteCategory_success() throws Exception {

        Response res = Response.builder()
                .status(200)
                .message("Category deleted")
                .build();

        Mockito.when(categoryService.deleteCategory(1L)).thenReturn(res);

        mockMvc.perform(delete("/api/categories/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted"));
    }
}
