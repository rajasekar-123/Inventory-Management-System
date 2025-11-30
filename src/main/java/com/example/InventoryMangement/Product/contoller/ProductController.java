package com.example.InventoryMangement.Product.contoller;

import java.util.List;
import java.util.Optional;

import com.example.InventoryMangement.Product.Entity.Product;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.myservice.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;

@RestController
@RequestMapping("api/product")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {



    @Autowired
    private ProductRepo repo;


	@Autowired
    ProductService productService;
	
	@PostMapping
	public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product)
	{
		Product savedProduct = productService.addProduct(product);
		return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduct(@Valid @PathVariable Integer id,@RequestBody Product updatedProduct)
	{
		Product product = productService.updateProduct(id, updatedProduct);
		if(product !=null)
		{
			return new ResponseEntity<>(product,HttpStatus.OK);
		}
		
		else
		{
			return new ResponseEntity<>("Product not found with Id: "+id,HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProduct( @PathVariable Integer id)
	{
		try {
			String result=productService.deleteProduct(id);
			return new ResponseEntity<>(result,HttpStatus.OK);
		}
		catch(Exception e)
		{
			return new ResponseEntity<>("Product not found with Id: "+id,HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> viewProductById(@PathVariable Integer id)
	{
		Optional<Product> product= productService.viewProductsById(id);
		if(product.isPresent())
		{
			return new ResponseEntity<>(product.get(),HttpStatus.OK);
		}
		else {
            return new ResponseEntity<>("Product not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
	}
	
	@GetMapping
	public ResponseEntity<List<Product>> viewAllProduct()
	{
		List<Product> products = productService.viewAllProducts();
		if(products.isEmpty())
		{
			 return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
		}
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = repo.findAll();
        return ResponseEntity.ok(products);
    }



}
