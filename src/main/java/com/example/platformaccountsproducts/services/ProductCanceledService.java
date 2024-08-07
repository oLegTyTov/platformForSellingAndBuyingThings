package com.example.platformaccountsproducts.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.entites.Product;
import com.example.platformaccountsproducts.entites.ProductCanceled;
import com.example.platformaccountsproducts.exceptions.account.NotAuthentificatedAccountException;
import com.example.platformaccountsproducts.exceptions.product.DeleteProductException;
import com.example.platformaccountsproducts.repositories.ProductCanceledRepository;

@Service
public class ProductCanceledService {
    private ProductCanceledRepository productCanceledRepository;
    private AccountService accountService;
    private ProductService productService;
    public ProductCanceledService(ProductCanceledRepository productCanceledRepository, AccountService accountService,
            ProductService productService) {
        this.productCanceledRepository = productCanceledRepository;
        this.accountService = accountService;
        this.productService = productService;
    }

    private Account getCurrentUser() throws NotAuthentificatedAccountException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal != null) {
            username = principal.toString();
        } else {
            throw new NotAuthentificatedAccountException("User is not authenticated");
        }

        Account account = accountService.findByEmail(username);
        if (account == null) {
            throw new NotAuthentificatedAccountException("User not found");
        }

        return account;
    }

    public void addCanceledProduct(Product product) {
        ProductCanceled productCanceled = new ProductCanceled(product);
        productCanceledRepository.save(productCanceled);
        productService.deleteById(product.getId());
    }

    public void deletecanceledProduct(Long id) throws DeleteProductException, NotAuthentificatedAccountException {
        if (productService.existsByIdAndAccountowner(id, getCurrentUser())) {
            Product product = productService.findById(id);
            ProductCanceled productCanceled = new ProductCanceled(product);
            productCanceledRepository.save(productCanceled); // видалення продукту = його додавання в список відмінених
                                                             // продуктів
            productService.deleteById(id);
        } else {
            throw new DeleteProductException();
        }
    }
    public Page<ProductCanceled> getAllCanceledProducts(int page, int size) throws NotAuthentificatedAccountException {
        return productCanceledRepository.findByAccountowner(getCurrentUser(), PageRequest.of(page, size) );
    }
    

    public void deleteAllCanceledProducts() {
        productCanceledRepository.deleteAll();
    }
}
