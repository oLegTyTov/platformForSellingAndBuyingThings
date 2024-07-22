package com.example.platformaccountsproducts.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.platformaccountsproducts.entites.Account;
import com.example.platformaccountsproducts.exceptions.account.AddAccountException;
import com.example.platformaccountsproducts.exceptions.account.DeleteAccountException;
import com.example.platformaccountsproducts.repositories.AccountRepository;
import java.util.ArrayList;

@Service
public class AccountService implements UserDetailsService {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public void addAccount(Account account) throws AddAccountException// і також може викидуватись дефолтний виняток про
                                                                      // unique
    {
        if (!accountRepository.existsByEmail(account.getEmail())) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
        } else {
            throw new AddAccountException();
        }
    }

    public void deleteAccount(String email, String rawPassword) throws DeleteAccountException {
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new DeleteAccountException();
        }

        if (passwordEncoder.matches(rawPassword, account.getPassword())) {
            accountRepository.deleteByEmail(email);
        } else {
            throw new DeleteAccountException();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                account.getEmail(), account.getPassword(), new ArrayList<>());
    }

    public void updateAccount(Account account)// тільки для аутентфиікованих юзерів
    {
        accountRepository.save(account);
    }
}
