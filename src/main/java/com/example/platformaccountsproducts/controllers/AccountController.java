package com.example.platformaccountsproducts.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.exceptions.account.AddAccountException;
import com.example.platformaccountsproducts.exceptions.account.DeleteAccountException;
import com.example.platformaccountsproducts.services.AccountService;

import jakarta.transaction.Transactional;
//контролер який відповідає за базовими операціями акакунтів створення,видалення,логін і тд
@Controller
public class AccountController {
    private AccountService accountService;
    public AccountController(AccountService accountService)
    {
    this.accountService=accountService;
    }
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        return "html/login";
    }
    @GetMapping("/signup")
    public String signup() {
        return "html/signup";
    }

    
    @PostMapping("/signup")
    public String signup(@ModelAttribute Account account) throws AddAccountException{
       accountService.addAccount(account);
       return "redirect:/login";
    }
    @GetMapping("/deleteAccount")
    public String deleteAccountPage() {
        return "html/deleteAccount";
    }
    @Transactional
    @PostMapping("/deleteAccount")
    public String deleteAccount(@RequestParam String email,@RequestParam String password) throws DeleteAccountException
    {
    accountService.deleteAccount(email, password);
    return "redirect:/";
    }
}
