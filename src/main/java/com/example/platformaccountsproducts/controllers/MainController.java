package com.example.platformaccountsproducts.controllers;

import java.util.Base64;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.entites.Product;
import com.example.platformaccountsproducts.filters.ProductFilter;
import com.example.platformaccountsproducts.services.AccountService;
import com.example.platformaccountsproducts.services.ProductService;

//контролер який відповідає за повернення усього ринку та отримання продукту всі методи доступні до публічного доступу
@Controller
public class MainController {
    private ProductService productService;
    private AccountService accountService;

    public MainController(AccountService accountService, ProductService productService) {
        this.accountService = accountService;
        this.productService = productService;
    }

    @GetMapping
    public String mainPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account=null;
        if(userDetails!=null)
        {
             account = accountService.findByEmail(userDetails.getUsername());
        
        }
        boolean isAuthenticated;
        if (account != null) {
            model.addAttribute("name", account.getUsername());
            isAuthenticated = true;// true якщо він залогінений
            model.addAttribute("isAuthenticated", isAuthenticated);
            return "html/mainPage";
        } else {
            isAuthenticated = false;
            model.addAttribute("isAuthenticated", isAuthenticated);
            return "html/mainPage";
        }

    }

    @GetMapping("/getAllProducts")
    public String getAllProducts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = AuthenticatedAccountController.defaultSizePage) int size,
            Model model, @ModelAttribute ProductFilter productFilter) {
        Page<Product> productPage = productService.getAllProducts(productFilter, page, size);
        productPage.getContent().forEach(product -> {
            String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
            product.setBase64Image(base64Image);
        });
        model.addAttribute("productFilter", productFilter);
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "html/getAllProducts";
    }

    @GetMapping("/getProduct")
    public String getProduct(@RequestParam Long id, Model model, @RequestParam(required = false) Boolean balance,
            @RequestParam(required = false) Boolean sameaccount) {
        Product product = productService.findProduct(id);
        String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
        product.setBase64Image(base64Image);
        if (balance != null) {
            model.addAttribute("balance", balance);// balance=false
        }
        if (sameaccount != null) {
            model.addAttribute("sameaccount", sameaccount);
        }
        model.addAttribute("product", product);
        return "html/getProduct";
    }
}