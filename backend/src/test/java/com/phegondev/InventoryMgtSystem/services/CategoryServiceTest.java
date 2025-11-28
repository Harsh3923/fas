package com.phegondev.InventoryMgtSystem.services;

import com.phegondev.InventoryMgtSystem.dtos.CategoryDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Category;
import com.phegondev.InventoryMgtSystem.repositories.CategoryRepository;
import com.phegondev.InventoryMgtSystem.services.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------------
    // CREATE CATEGORY
    // ----------------------------------------------------------------
    @Test
    void createCategory_success() {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");

        Category mapped = new Category();
        mapped.setName("Electronics");

        when(modelMapper.map(dto, Category.class)).thenReturn(mapped);
        when(categoryRepository.save(mapped)).thenReturn(mapped);

        Response response = categoryService.createCategory(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Category Saved Successfully", response.getMessage());
    }

    // ----------------------------------------------------------------
    // GET ALL CATEGORIES
    // ----------------------------------------------------------------
    @Test
    void getAllCategories_success() {

    // mock entity
    Category c1 = new Category();
    c1.setId(1L);
    c1.setName("Food");

    List<Category> entityList = List.of(c1);

    // mock DTO
    CategoryDTO dto = new CategoryDTO();
    dto.setId(1L);
    dto.setName("Food");

    List<CategoryDTO> dtoList = List.of(dto);

    // repo mock
    when(categoryRepository.findAll(any(Sort.class))).thenReturn(entityList);

    // 🔥 FIX: MATCH THE EXACT TYPE
    when(modelMapper.map(eq(entityList), any(java.lang.reflect.Type.class)))
            .thenReturn(dtoList);

    Response res = categoryService.getAllCategories();

    assertEquals(200, res.getStatus());
    assertEquals("success", res.getMessage());
    assertNotNull(res.getCategories());   // <--- Previously failing
    assertEquals(1, res.getCategories().size());
    assertEquals("Food", res.getCategories().get(0).getName());
    }

    // ----------------------------------------------------------------
    // GET CATEGORY BY ID
    // ----------------------------------------------------------------
    @Test

    void getCategoryById_success() {

        Category category = new Category();
        category.setId(5L);
        category.setName("Clothes");

        CategoryDTO dto = new CategoryDTO();
        dto.setId(5L);
        dto.setName("Clothes");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(dto);

        Response response = categoryService.getCategoryById(5L);

        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertEquals("Clothes", response.getCategory().getName());
    }

    // ----------------------------------------------------------------
    // GET CATEGORY — NOT FOUND
    // ----------------------------------------------------------------
    @Test
    void getCategoryById_notFound() {

        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            categoryService.getCategoryById(10L);
        });
    }

    // ----------------------------------------------------------------
    // UPDATE CATEGORY
    // ----------------------------------------------------------------
    @Test
    void updateCategory_success() {

        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old Name");

        CategoryDTO dto = new CategoryDTO();
        dto.setName("New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        Response response = categoryService.updateCategory(1L, dto);

        verify(categoryRepository).save(existing);

        assertEquals("New Name", existing.getName());
        assertEquals(200, response.getStatus());
        assertEquals("Category Was Successfully Updated", response.getMessage());
    }

    // ----------------------------------------------------------------
    // DELETE CATEGORY
    // ----------------------------------------------------------------
    @Test
    void deleteCategory_success() {

        Category existing = new Category();
        existing.setId(3L);

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(existing));

        Response response = categoryService.deleteCategory(3L);

        verify(categoryRepository).deleteById(3L);
        assertEquals(200, response.getStatus());
        assertEquals("Category Was Successfully Deleted", response.getMessage());
    }

    // ----------------------------------------------------------------
    // DELETE CATEGORY — NOT FOUND
    // ----------------------------------------------------------------
    @Test
    void deleteCategory_notFound() {

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            categoryService.deleteCategory(99L);
        });
    }
}
