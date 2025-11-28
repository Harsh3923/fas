package com.phegondev.InventoryMgtSystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phegondev.InventoryMgtSystem.dtos.ProductDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.services.ProductService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    // 🔥 REQUIRED SECURITY MOCKS
    @MockBean
    private com.phegondev.InventoryMgtSystem.security.JwtUtils jwtUtils;

    @MockBean
    private com.phegondev.InventoryMgtSystem.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveProduct_success() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "fake-image".getBytes()
        );

        // Mock Response returned by service
        Response res = Response.builder()
                .status(200)
                .message("Product created")
                .build();

        Mockito.when(productService.saveProduct(
                ArgumentMatchers.any(ProductDTO.class),
                ArgumentMatchers.any()
        )).thenReturn(res);

        mockMvc.perform(multipart("/api/products/add")
                .file(image)
                .param("name", "Laptop")
                .param("sku", "SKU-123")
                .param("price", "1500.00")
                .param("stockQuantity", "10")
                .param("categoryId", "1")
                .param("supplierId", "1")
                .param("description", "Gaming laptop")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product created"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void getAllProducts_success() throws Exception {

        Response res = Response.builder()
                .status(200)
                .message("All products")
                .build();

        Mockito.when(productService.getAllProducts()).thenReturn(res);

        mockMvc.perform(get("/api/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void deleteProduct_success() throws Exception {

        Response res = Response.builder()
                .status(200)
                .message("Product deleted")
                .build();

        Mockito.when(productService.deleteProduct(1L)).thenReturn(res);

        mockMvc.perform(delete("/api/products/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted"));
    }
}
