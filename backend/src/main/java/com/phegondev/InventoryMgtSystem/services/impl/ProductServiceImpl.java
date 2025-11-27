package com.phegondev.InventoryMgtSystem.services.impl;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    // All images will be stored in: <backend-root>/uploads/
    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads/";

    // -----------------------------------------------------------
    // SAVE PRODUCT
    // -----------------------------------------------------------
    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {

        log.info("🔥 DEBUG: saveProduct() called");
        log.info("DTO: {}", productDTO);

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> {
                    log.error("❌ Category NOT FOUND: {}", productDTO.getCategoryId());
                    return new NotFoundException("Category Not Found");
                });

        log.info("✔ Category found: {}", category.getName());

        Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                .orElseThrow(() -> {
                    log.error("❌ Supplier NOT FOUND: {}", productDTO.getSupplierId());
                    return new NotFoundException("Supplier Not Found");
                });

        log.info("✔ Supplier found: {}", supplier.getName());

        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .description(productDTO.getDescription())
                .category(category)
                .supplier(supplier)
                .build();

        log.info("🔥 Product BEFORE SAVE: {}", productToSave);

        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("Saving image...");
            String imagePath = saveImage(imageFile);   // ✅ use saveImage(), not saveImage2
            productToSave.setImageUrl(imagePath);
            log.info("✔ Image saved: {}", imagePath);
        } else {
            log.info("⚠ No image uploaded");
        }

        log.info("🚨 ATTEMPTING TO SAVE PRODUCT TO DB NOW...");

        try {
            productRepository.save(productToSave);
            log.info("✔✔✔ Product saved successfully!");
        } catch (Exception e) {
            log.error("❌❌❌ ERROR saving product: {}", e.getMessage(), e);
            throw e;
        }

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
            String imagePath = saveImage(imageFile);   // ✅ use same upload dir
            existingProduct.setImageUrl(imagePath);
        }

        // Category update
        if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            existingProduct.setCategory(category);
        }

        // Supplier update
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

        productDTOList.forEach(dto -> {
                Product product = productRepository.findById(dto.getId()).get();
                dto.setSupplierName(product.getSupplier().getName());
    });


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
    // IMAGE SAVE METHOD - BACKEND /uploads/ FOLDER
    // -----------------------------------------------------------
    @SuppressWarnings("null")
    private String saveImage(MultipartFile imageFile) {

        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files allowed");
        }

        File directory = new File(UPLOAD_DIR);

        if (!directory.exists()) {
            directory.mkdirs();
            log.info("Created image directory: {}", UPLOAD_DIR);
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String absolutePath = UPLOAD_DIR + fileName;

        try {
            imageFile.transferTo(new File(absolutePath));
        } catch (Exception e) {
            log.error("Error saving image: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving image");
        }

        // Store relative path in DB
        return "uploads/" + fileName;
    }
}
