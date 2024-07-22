package com.example.platformaccountsproducts.entites;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "products_sold")
public class ProductSold {
    // класичні поля класа Product
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;

    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;
    private String description;
    private String category;
    @Transient
    private String base64Image;
    @ManyToOne( cascade = CascadeType.MERGE)
    private Account accountowner;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datecreatingproduct = LocalDate.now();
    // додаткові поля
    @ManyToOne(cascade = CascadeType.MERGE)
    private Account accountbuyer;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate timesold = LocalDate.now();

    public ProductSold(Product product, Account accountbuyer) {
        this.name = product.getName();
        this.price = product.getPrice();
        this.photo = product.getPhoto() != null ? product.getPhoto().clone() : null;
        this.description = product.getDescription();
        this.category = product.getCategory();
        this.accountowner = product.getAccountowner();
        this.datecreatingproduct = product.getDatecreatingproduct();
        this.accountbuyer = accountbuyer;
    }
}