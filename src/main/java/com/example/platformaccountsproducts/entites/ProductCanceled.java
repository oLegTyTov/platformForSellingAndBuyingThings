package com.example.platformaccountsproducts.entites;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@NoArgsConstructor
@Table(name = "products_canceled")
public class ProductCanceled {
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datecanceled = LocalDate.now();

    public ProductCanceled(Product product) {
        this.name = product.getName();
        this.price = product.getPrice();
        this.photo = product.getPhoto() != null ? product.getPhoto().clone() : null;
        this.description = product.getDescription();
        this.category = product.getCategory();
        this.accountowner = product.getAccountowner();
        this.datecreatingproduct = product.getDatecreatingproduct();
    }
}