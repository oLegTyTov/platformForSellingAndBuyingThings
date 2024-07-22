package com.example.platformaccountsproducts.controllers;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import com.example.platformaccountsproducts.entites.*;
import com.example.platformaccountsproducts.exceptions.account.NotAuthentificatedAccountException;
import com.example.platformaccountsproducts.exceptions.product.DeleteProductException;
import com.example.platformaccountsproducts.services.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Base64;
import java.text.SimpleDateFormat;

//контролер який відповідає за операції для залогінених юзерів
@Controller
@RequestMapping("/myAccount")
public class AuthenticatedAccountController {
    private ProductBoughtService productBoughtService;
    private ProductCanceledService productCanceledService;
    private ProductSoldService productSoldService;
    private ProductService productService;
    private AccountService accountService;
    public static final String defaultSizePage = "2";

    public AuthenticatedAccountController(ProductBoughtService productBoughtService,
            ProductCanceledService productCanceledService, ProductService productService, AccountService accountService,
            ProductSoldService productSoldService) {
        this.accountService = accountService;
        this.productBoughtService = productBoughtService;
        this.productCanceledService = productCanceledService;
        this.productService = productService;
        this.productSoldService = productSoldService;
    }

    @GetMapping
    public String accountPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByEmail(userDetails.getUsername());
        model.addAttribute("name", account.getUsername());
        model.addAttribute("balance", account.getBalance());
        return "html/accountPage";
    }

    @GetMapping("/cancelProduct")//список всіх продуктів які в тебе зараз в продажу і ти хочеш видалити якийсь продукт
    public String cancelProductPage(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = defaultSizePage) int size) throws NotAuthentificatedAccountException {
        Page<Product> productPage = productService.getProductsOfUser(page, size);

        // Encode photo to Base64 for each product
        productPage.getContent().forEach(product -> {
            String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
            product.setBase64Image(base64Image);
        });

        model.addAttribute("productPage", productPage); // Додати productPage в модель
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "html/cancelProduct";
    }

    @Transactional
    @PostMapping("/cancelProduct")
    public String cancelProduct(@RequestParam Long id) {
        Product product = productService.findById(id);
        productCanceledService.addCanceledProduct(product);
        return "redirect:/myAccount";
    }

    @GetMapping("/addProduct")
    public String addProductPage() {
        return "html/addProduct";
    }

@PostMapping("/addProduct")
public String addProduct(@ModelAttribute Product product,@RequestParam("photo") MultipartFile photo) throws IOException,NotAuthentificatedAccountException {
    // Зберігаємо продукт
    if (!photo.isEmpty()) {
        byte[] photoBytes = photo.getBytes();
        // Збережіть фото у продукт
        product.setPhoto(photoBytes);
    }
    productService.addProduct(product);
    return "redirect:/myAccount";
}

    @GetMapping("/getMyAllProductsInSelling")
    public String getMyAllProductsInSelling(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = defaultSizePage) int size)
            throws NotAuthentificatedAccountException {
        Page<Product> pageMyAllProductsInSelling = productService.getProductsOfUser(page, size);
        pageMyAllProductsInSelling.getContent().forEach(product -> {
            String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
            product.setBase64Image(base64Image);
        });
        model.addAttribute("pageMyAllProductsInSelling", pageMyAllProductsInSelling);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageMyAllProductsInSelling.getTotalPages());
        return "html/getMyAllProductsInSelling";
    }

    // функціонал ProductBoughtService
    @GetMapping("/getAllBoughtProducts")
    public String getAllBoughtProducts(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = defaultSizePage, required = false) int size)
            throws NotAuthentificatedAccountException {
        Page<ProductBought> pageBoughtProducts = productBoughtService.getProductsBought(page, size);

        pageBoughtProducts.getContent().forEach(product -> {
            String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
            product.setBase64Image(base64Image);
        });
        model.addAttribute("pageBoughtProducts", pageBoughtProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageBoughtProducts.getTotalPages());
        return "html/getAllBoughtProducts";
    }

    @GetMapping("/getBoughtProduct")//todo
    public String getBoughtProduct(Model model, @RequestParam Long id) {
        ProductBought productBought = productBoughtService.getProductBought(id);
        model.addAttribute("productBought", productBought);
        return "html/getProductBought";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true, 10));
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws ServletException {

        // Convert multipart object to byte[]
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

    }

    @Transactional
    @GetMapping("/addProductBought")
    public String addProductBought(Model model, @RequestParam Long id, @AuthenticationPrincipal UserDetails userDetails)
            throws DeleteProductException, NotAuthentificatedAccountException {
        Account account = accountService.findByEmail(userDetails.getUsername());
        Product product = productService.findProduct(id);
        if (account.getBalance() > product.getPrice())

        {
            if (!account.getEmail().equals(product.getAccountowner().getEmail())) {
                productBoughtService.addBoughtProduct(product);
                account = accountService.findByEmail(userDetails.getUsername());// отримуємо оновленого юзера
                model.addAttribute("name", account.getUsername());
                model.addAttribute("balance", account.getBalance());
                return "redirect:/getAllProducts";
            } else {
                return "redirect:/getProduct?id=" + id + "&sameaccount=" + true;// юзер хоче купити свій же
                                                                                // продукт,помилка
            }
        } else {
            return "redirect:/getProduct?id=" + id + "&balance=" + false;// не хватає коштів,помилка
        }
    }
    @GetMapping("/addBalance")
    public String addBalancePage() {
        return "html/addBalance";
    }
    
    @PostMapping("/addBalance") 
    public String addBalance(@RequestParam double balance, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByEmail(userDetails.getUsername());
        account.setBalance(account.getBalance()+balance);
        accountService.updateAccount(account);
        return "redirect:/myAccount";
    }

    // функціонал ProductSoldService
    @GetMapping("/getAllSoldProducts") 
    public String getAllSoldProducts(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = defaultSizePage) int size) throws NotAuthentificatedAccountException {
        Page<ProductSold> pageSoldProducts = productSoldService.getSoldProducts(page, size);
        pageSoldProducts.getContent().forEach(product -> {
            String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
            product.setBase64Image(base64Image);
        });
        model.addAttribute("pageSoldProducts", pageSoldProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSoldProducts.getTotalPages());
        return "html/getAllSoldProducts";
    }

    // функціонал ProductCancaledService
    @GetMapping("/getAllCanceledProducts")
    public String getAllCanceledProducts(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = defaultSizePage, required = false) int size)
            throws NotAuthentificatedAccountException {
        Page<ProductCanceled> pageCanceledProducts = productCanceledService.getAllCanceledProducts(
                page, size);
        pageCanceledProducts.getContent().forEach(product -> {
            String base64Image = Base64.getEncoder().encodeToString(product.getPhoto());
            product.setBase64Image(base64Image);
        });
        model.addAttribute("productsCanceledPage", pageCanceledProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageCanceledProducts.getTotalPages());
        return "html/getAllCanceledProducts";
    }

    @GetMapping("/deleteAllCanceledProducts")
    public String deleteAllCanceledProducts() {
        productCanceledService.deleteAllCanceledProducts();
        return "redirect:/myAccount/getAllCanceledProducts";
    }

}
