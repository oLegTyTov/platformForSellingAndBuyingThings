package com.example.platformaccountsproducts.filters;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
   private String name;
   private Double priceMin;
   private Double priceMax;
   private String category;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date dateMin;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date dateMax;
}
