package com.example.platformaccountsproducts.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.entites.ProductBought;
import com.example.platformaccountsproducts.entites.ProductCanceled;
@Repository
public interface ProductCanceledRepository extends JpaRepository<ProductCanceled, Long> {
    Page<ProductCanceled> findByAccountowner(Account accountowner,Pageable pageable);
}
