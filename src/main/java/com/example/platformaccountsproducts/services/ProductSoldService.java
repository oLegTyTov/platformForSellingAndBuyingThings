package com.example.platformaccountsproducts.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.entites.Product;
import com.example.platformaccountsproducts.entites.ProductSold;
import com.example.platformaccountsproducts.exceptions.account.NotAuthentificatedAccountException;
import com.example.platformaccountsproducts.repositories.ProductSoldRepository;

@Service
public class ProductSoldService {
    private ProductSoldRepository productSoldRepository;
    private AccountService accountService;

    @Autowired
    public ProductSoldService(AccountService accountService, ProductSoldRepository productSoldRepository) {
        this.accountService = accountService;
        this.productSoldRepository = productSoldRepository;
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

    public void addSoldProduct(Product product) throws NotAuthentificatedAccountException {// метод якщо треба добавити
                                                                                           // проданий продукт до
                                                                                           // поточного юзера
        ProductSold productSold = new ProductSold(product, getCurrentUser());
        productSoldRepository.save(productSold);
    }

    public void addSoldProduct(ProductSold productSold, Account account) {//// метод якщо треба добавити проданий
                                                                          //// продукт до певного акаунта
        productSoldRepository.save(productSold);
    }

    public Page<ProductSold> getSoldProducts( int page, int size) throws NotAuthentificatedAccountException {
        return productSoldRepository.findByAccountowner(getCurrentUser(),PageRequest.of(page, size));
    }
}
