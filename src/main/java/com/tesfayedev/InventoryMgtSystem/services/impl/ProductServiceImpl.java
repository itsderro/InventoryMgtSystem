package com.tesfayedev.InventoryMgtSystem.services.impl;

import com.tesfayedev.InventoryMgtSystem.dtos.ProductDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.exceptions.NotFoundException;
import com.tesfayedev.InventoryMgtSystem.models.Category;
import com.tesfayedev.InventoryMgtSystem.models.Product;
import com.tesfayedev.InventoryMgtSystem.repositories.CategoryRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.ProductRepository;
import com.tesfayedev.InventoryMgtSystem.services.ProductService;
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
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir")+"/product-images/";

    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()-> new NotFoundException("Category not found"));

        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .description(productDTO.getDescription())
                .category(category)
                .build();

        if(imageFile != null && !imageFile.isEmpty()){
            log.info("Image file exists");
            String imagePath = saveImage(imageFile);
            productToSave.setImageUrl(imagePath);
        }

        //save the product entity
        productRepository.save(productToSave);

        return  Response.builder()
                .status(200)
                .message("Product successfully saved")
                .build();
    }

    @Override
    public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(productDTO.getProductId())
                .orElseThrow(()->new NotFoundException("Product Not Found"));

        if(imageFile != null && !imageFile.isEmpty()){
            String imagePath = saveImage(imageFile);
            existingProduct.setImageUrl(imagePath);
        }

        //check if category is to be changed for the products
        if(productDTO.getCategory() != null && productDTO.getCategoryId() > 0){
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(()-> new NotFoundException("Category not found"));
            existingProduct.setCategory(category);
        }

        //check if product fields is to be changed and update
        if(productDTO.getName() != null && !productDTO.getName().isBlank()){
            existingProduct.setName(productDTO.getName());
        }

        if(productDTO.getSku() != null && !productDTO.getSku().isBlank()){
            existingProduct.setSku(productDTO.getSku());
        }

        if(productDTO.getDescription() != null && !productDTO.getDescription().isBlank()){
            existingProduct.setDescription(productDTO.getDescription());
        }

        if(productDTO.getPrice() != null && productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0){
            existingProduct.setPrice(productDTO.getPrice());
        }

        if(productDTO.getStockQuantity() != null && productDTO.getStockQuantity() >= 0){
            existingProduct.setStockQuantity(productDTO.getStockQuantity());
        }
        //Update the product
        productRepository.save(existingProduct);

        //Build our Response
        return Response.builder()
                .status(200)
                .message("Product updated successfully")
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));

        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }

    @Override
    public Response getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Product not found"));

        return Response.builder()
                .status(200)
                .message("success")
                .product(modelMapper.map(product, ProductDTO.class))
                .build();
    }

    @Override
    public Response deleteProduct(Long id) {
        productRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Product not found"));

        productRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Product deleted successfully")
                .build();
    }

    @Override
    public Response searchProduct(String input) {
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);
        if(products.isEmpty()){
            throw new NotFoundException("Product Not Found");
        }

        List<ProductDTO> productDTOList = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("Success")
                .products(productDTOList)
                .build();
    }

    private String saveImage(MultipartFile imageFile){
        //validate image and check if it is greater than 1GB
        if(!imageFile.getContentType().startsWith("image/") || imageFile.getSize() > 1024*1024*1024){
            throw new IllegalArgumentException("Only image files under 1GB is allowed");
        }

        //create the directory if it doesn't exist
        File directory = new File(IMAGE_DIRECTORY);
        if(!directory.exists()){
            directory.mkdir();
            log.info("Directory was created");
        }

        //generate unique file name for the image
        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        //get the absolute path of the image
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try{
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);//We are transfering the image to this folder
        }catch (Exception e){
            throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
        }

        return imagePath;
    }
}
