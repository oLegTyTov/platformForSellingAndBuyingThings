package com.example.platformaccountsproducts.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.platformaccountsproducts.entites.*;
import com.example.platformaccountsproducts.exceptions.account.NotAuthentificatedAccountException;
import com.example.platformaccountsproducts.exceptions.product.DeleteProductException;
import com.example.platformaccountsproducts.filters.ProductFilter;
import com.example.platformaccountsproducts.repositories.ProductRepository;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private AccountService accountService;

    @Autowired
    public ProductService(ProductRepository productRepository, AccountService accountService) {
        this.productRepository = productRepository;
        this.accountService = accountService;
    }

    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    public void addProduct(Product product) throws NotAuthentificatedAccountException {
        product.setAccountowner(getCurrentUser());
        productRepository.save(product);
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

    public void deleteProduct(Long id) throws DeleteProductException {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new DeleteProductException();
        }
    }

    private Specification<Product> getSpecidSpecification(ProductFilter productFilter) {
        Specification<Product> spec = Specification.where(null);

        if (productFilter.getName() != null && !productFilter.getName().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + productFilter.getName() + "%"));
        }
        if (productFilter.getCategory() != null && !productFilter.getCategory().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), productFilter.getCategory()));
        }
        if (productFilter.getPriceMin() != null) {
            spec = spec
                    .and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), productFilter.getPriceMin()));
        }
        if (productFilter.getPriceMax() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), productFilter.getPriceMax()));
        }
        if (productFilter.getDateMin() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("datecreatingproduct"),
                    productFilter.getDateMin()));
        }
        if (productFilter.getDateMax() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("datecreatingproduct"),
                    productFilter.getDateMax()));
        }
        return spec;
    }

    public Page<Product> getProductsOfUser( int page, int size)
            throws NotAuthentificatedAccountException {
                return productRepository.findByAccountowner(getCurrentUser(),PageRequest.of(page, size));
    }

    public Page<Product> getAllProducts(ProductFilter productFilter, int page, int size) { // продукти з всьої платформи
        Specification<Product> spec = getSpecidSpecification(productFilter);
        return productRepository.findAll(spec, PageRequest.of(page, size));
    }

    public boolean existsByIdAndAccountowner(Long id, Account currentUser) {
        return productRepository.existsByIdAndAccountowner(id, currentUser);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).get();
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
