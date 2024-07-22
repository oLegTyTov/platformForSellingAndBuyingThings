package com.example.platformaccountsproducts.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.entites.Product;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByNameAndAccountowner(String name, Account accountowner);
    void deleteByNameAndAccountowner(String name, Account accountowner);
    boolean existsByIdAndAccountowner(Long id, Account accountowner);
    Page<Product> findByAccountowner(Account accountowner, Pageable pageable);
}