package com.example.platformaccountsproducts.entites;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Temporal(TemporalType.DATE)
    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datecreatingaccount = LocalDate.now();

    @Column(unique = true)
    private String username;

    private String password;
    private int rating; // 0-5
    private String country;

    @Builder.Default
    private double balance = 1000;

    @OneToMany(mappedBy = "accountowner", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Product> productsforsell;

    @OneToMany(mappedBy = "accountowner",  cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<ProductSold> productssold;

    @OneToMany(mappedBy = "accountowner",  cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<ProductCanceled> productscanceled;

    @OneToMany(mappedBy = "accountowner", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<ProductBought> productboughts;
    
    public Account(Account account) {
        this.balance = account.getBalance();
        this.country = account.getCountry();
        this.datecreatingaccount = account.getDatecreatingaccount();
        this.email = account.getEmail();
        this.id = account.id;
        this.password = account.password;
        this.productboughts = account.productboughts;
        this.productscanceled = account.getProductscanceled();
        this.productsforsell = account.getProductsforsell();
        this.productssold = account.getProductssold();
        this.rating = account.rating;
        this.username = account.username;
    }
}
