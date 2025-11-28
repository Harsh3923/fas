package com.phegondev.InventoryMgtSystem.services;

import com.phegondev.InventoryMgtSystem.dtos.ProductDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Category;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.models.Supplier;
import com.phegondev.InventoryMgtSystem.repositories.CategoryRepository;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.repositories.SupplierRepository;
import com.phegondev.InventoryMgtSystem.services.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------
    // SAVE PRODUCT TEST
    // ----------------------------------------------------------
    @Test
    void saveProduct_success() {

        ProductDTO dto = new ProductDTO();
        dto.setName("Test Product");
        dto.setSku("SKU123");
        dto.setPrice(BigDecimal.valueOf(10.0));
        dto.setStockQuantity(5);
        dto.setCategoryId(1L);
        dto.setSupplierId(2L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Category A");

        Supplier supplier = new Supplier();
        supplier.setId(2L);
        supplier.setName("Supplier A");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenReturn(null);

        Response response = productService.saveProduct(dto, null);

        assertEquals(200, response.getStatus());
        assertEquals("Product successfully saved", response.getMessage());
    }

    // ----------------------------------------------------------
    // GET PRODUCT BY ID
    // ----------------------------------------------------------
    @Test
    void getProductById_success() {

        Product product = Product.builder()
                .id(10L)
                .name("Phone")
                .sku("P001")
                .build();

        ProductDTO mappedDto = new ProductDTO();
        mappedDto.setId(10L);
        mappedDto.setName("Phone");

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(mappedDto);

        Response response = productService.getProductById(10L);

        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertEquals("Phone", response.getProduct().getName());
    }

    // ----------------------------------------------------------
    // DELETE PRODUCT
    // ----------------------------------------------------------
    @Test
    void deleteProduct_success() {

        Product product = new Product();
        product.setId(5L);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        Response response = productService.deleteProduct(5L);

        verify(productRepository).deleteById(5L);
        assertEquals(200, response.getStatus());
        assertEquals("Product Deleted successfully", response.getMessage());
    }

    // ----------------------------------------------------------
    // SEARCH PRODUCT
    // ----------------------------------------------------------
    @Test
    void searchProduct_success() {

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Good laptop")
                .build();

        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setName("Laptop");

        when(productRepository.findByNameContainingOrDescriptionContaining("lap", "lap"))
                .thenReturn(List.of(product));

        when(modelMapper.map(any(), any(java.lang.reflect.Type.class)))
                .thenReturn(List.of(dto));

        Response response = productService.searchProduct("lap");

        assertEquals(200, response.getStatus());
        assertEquals(1, response.getProducts().size());
    }

    // ----------------------------------------------------------
    // SEARCH - NOT FOUND
    // ----------------------------------------------------------
    @Test
    void searchProduct_notFound() {

        when(productRepository.findByNameContainingOrDescriptionContaining("abc", "abc"))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> {
            productService.searchProduct("abc");
        });
    }
}
