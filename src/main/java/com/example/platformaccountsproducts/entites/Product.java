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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "products")
public class Product {
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

    public Product(Product product) {
        this.id = product.id;
        this.name = product.name;
        this.price = product.price;
        this.photo = product.photo;
        this.description = product.description;
        this.category = product.category;
        this.accountowner = product.accountowner;
        this.datecreatingproduct = product.datecreatingproduct;
    }
}