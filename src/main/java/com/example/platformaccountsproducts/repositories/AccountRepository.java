package com.example.platformaccountsproducts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.platformaccountsproducts.entites.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account,Long>{
void deleteByEmail(String email);
Account findByEmail(String email);
boolean existsByEmail(String email);
}
