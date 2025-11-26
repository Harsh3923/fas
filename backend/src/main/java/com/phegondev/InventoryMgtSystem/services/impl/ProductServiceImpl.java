package com.phegondev.InventoryMgtSystem.services.impl;

import com.phegondev.InventoryMgtSystem.dtos.ProductDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Category;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.models.Supplier;
import com.phegondev.InventoryMgtSystem.repositories.CategoryRepository;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.repositories.SupplierRepository;
import com.phegondev.InventoryMgtSystem.services.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;   //  NEW

    private static final String IMAGE_DIRECTORY =
            System.getProperty("user.dir") + "/product-images/";

    private static final String IMAGE_DIRECTORY_2 =
            "/Users/dennismac/phegonDev/ims-react/public/products/";

    // -----------------------------------------------------------
    // SAVE PRODUCT
    // -----------------------------------------------------------
    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category Not Found"));

        Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())   // ✅ NEW
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .description(productDTO.getDescription())
                .category(category)
                .supplier(supplier)     // ✅ NEW
                .build();

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage2(imageFile);
            productToSave.setImageUrl(imagePath);
        }

        productRepository.save(productToSave);

        return Response.builder()
                .status(200)
                .message("Product successfully saved")
                .build();
    }

    // -----------------------------------------------------------
    // UPDATE PRODUCT
    // -----------------------------------------------------------
    @Override
    public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {

        Product existingProduct = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage2(imageFile);
            existingProduct.setImageUrl(imagePath);
        }

        // Category update
        if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            existingProduct.setCategory(category);
        }

        // Supplier update  ✅ NEW
        if (productDTO.getSupplierId() != null && productDTO.getSupplierId() > 0) {
            Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new NotFoundException("Supplier Not Found"));
            existingProduct.setSupplier(supplier);
        }

        if (productDTO.getName() != null) existingProduct.setName(productDTO.getName());
        if (productDTO.getSku() != null) existingProduct.setSku(productDTO.getSku());
        if (productDTO.getDescription() != null) existingProduct.setDescription(productDTO.getDescription());
        if (productDTO.getPrice() != null) existingProduct.setPrice(productDTO.getPrice());
        if (productDTO.getStockQuantity() != null) existingProduct.setStockQuantity(productDTO.getStockQuantity());

        productRepository.save(existingProduct);

        return Response.builder()
                .status(200)
                .message("Product Updated successfully")
                .build();
    }

    // -----------------------------------------------------------
    // GET ALL PRODUCTS
    // -----------------------------------------------------------
    @Override
    public Response getAllProducts() {

        List<Product> productList =
                productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<ProductDTO> productDTOList =
                modelMapper.map(productList, new TypeToken<List<ProductDTO>>(){}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }

    // -----------------------------------------------------------
    // GET PRODUCT BY ID
    // -----------------------------------------------------------
    @Override
    public Response getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        return Response.builder()
                .status(200)
                .message("success")
                .product(modelMapper.map(product, ProductDTO.class))
                .build();
    }

    // -----------------------------------------------------------
    // DELETE PRODUCT
    // -----------------------------------------------------------
    @Override
    public Response deleteProduct(Long id) {

        productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        productRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Product Deleted successfully")
                .build();
    }

    // -----------------------------------------------------------
    // SEARCH PRODUCT
    // -----------------------------------------------------------
    @Override
    public Response searchProduct(String input) {

        List<Product> products =
                productRepository.findByNameContainingOrDescriptionContaining(input, input);

        if (products.isEmpty()) {
            throw new NotFoundException("Product Not Found");
        }

        List<ProductDTO> productDTOList =
                modelMapper.map(products, new TypeToken<List<ProductDTO>>(){}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }

    // -----------------------------------------------------------
    // IMAGE SAVE METHOD 1 (backend folder)
    // -----------------------------------------------------------
    private String saveImage(MultipartFile imageFile) {

        if (!imageFile.getContentType().startsWith("image/")
                || imageFile.getSize() > 1024 * 1024 * 1024) {
            throw new IllegalArgumentException("Only image files under 1GB allowed");
        }

        File directory = new File(IMAGE_DIRECTORY);

        if (!directory.exists()) {
            directory.mkdir();
            log.info("Directory created");
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = IMAGE_DIRECTORY + fileName;

        try {
            imageFile.transferTo(new File(imagePath));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving image: " + e.getMessage());
        }

        return imagePath;
    }

    // -----------------------------------------------------------
    // IMAGE SAVE METHOD 2 (frontend public/products)
    // -----------------------------------------------------------
    private String saveImage2(MultipartFile imageFile) {

        if (!imageFile.getContentType().startsWith("image/")
                || imageFile.getSize() > 1024 * 1024 * 1024) {
            throw new IllegalArgumentException("Only image files under 1GB allowed");
        }

        File directory = new File(IMAGE_DIRECTORY_2);

        if (!directory.exists()) {
            directory.mkdir();
            log.info("Directory created");
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = IMAGE_DIRECTORY_2 + fileName;

        try {
            imageFile.transferTo(new File(imagePath));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving image: " + e.getMessage());
        }

        return "products/" + fileName;
    }
}
