package com.Docker.demo.controller;

import com.Docker.demo.entity.Product;
import com.Docker.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello from springboot";
    }

    @PostMapping
    public String createProduct(@RequestBody Product product){

        productService.createProduct(product);
        return "product has been created.";
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProduct();
    }
}
