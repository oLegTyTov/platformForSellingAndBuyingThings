package com.example.platformaccountsproducts.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.entites.Product;
import com.example.platformaccountsproducts.entites.ProductBought;
import com.example.platformaccountsproducts.entites.ProductSold;
import com.example.platformaccountsproducts.exceptions.account.NotAuthentificatedAccountException;
import com.example.platformaccountsproducts.exceptions.product.DeleteProductException;
import com.example.platformaccountsproducts.repositories.ProductBoughtRepository;

@Service
public class ProductBoughtService {
    private ProductBoughtRepository productBoughtRepository;
    private ProductSoldService productSoldService;
    private AccountService accountService;
    private ProductService productService;

    @Autowired
    public ProductBoughtService(ProductBoughtRepository productBoughtRepository, ProductSoldService productSoldService,
            AccountService accountService, ProductService productService) {
        this.productBoughtRepository = productBoughtRepository;
        this.accountService = accountService;
        this.productService = productService;
        this.productSoldService = productSoldService;
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

    public void addBoughtProduct(Product product) throws DeleteProductException, NotAuthentificatedAccountException {
        Account accountBuyerAccount = getCurrentUser();// юзер який робить покупку
        accountBuyerAccount.setBalance(accountBuyerAccount.getBalance() - product.getPrice());// змінюємо рахунки
        Account accountSeller = accountService.findByEmail(product.getAccountowner().getEmail());
        accountSeller.setBalance(accountSeller.getBalance() + product.getPrice());
        accountService.updateAccount(accountBuyerAccount);
        accountService.updateAccount(accountSeller);
        // step 1
        product.setAccountowner(accountBuyerAccount);// змінюємо кому тепер належить цей продукт
        ProductBought productBought = new ProductBought(product, accountSeller);
        productBoughtRepository.save(productBought);
        // step 2
        productService.deleteById(product.getId());
        // step 3
        Product product2 = new Product(product);
        product2.setAccountowner(accountSeller);
        ProductSold productSold = new ProductSold(product2, accountBuyerAccount);
        productSoldService.addSoldProduct(productSold, accountSeller);
    }

    public Page<ProductBought> getProductsBought( int page, int size)
    throws NotAuthentificatedAccountException {

return productBoughtRepository.findByAccountowner(getCurrentUser(), PageRequest.of(page, size));
}

    public ProductBought getProductBought(Long id) {
        return productBoughtRepository.findById(id).get();
    }

}