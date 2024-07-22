package com.example.platformaccountsproducts.exceptions;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.platformaccountsproducts.exceptions.account.AddAccountException;
import com.example.platformaccountsproducts.exceptions.account.DeleteAccountException;
@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController {




    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleUniqueFieldExp()//якщо хтось зробив запис з unique атрибутом який вже є в бд
    {
    return "html/UniqueFieldExeption";
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public String exceptionNotFoundPage()//якщо хтось зробив запис з unique атрибутом який вже є в бд
    {
    return "html/exceptionNotFoundPage";
    }
    @ExceptionHandler(AddAccountException.class)
    public String addAccountException(Model model)
    {
        model.addAttribute("sameAccount",true);
        return "html/signup";
    }
    @ExceptionHandler(DeleteAccountException.class)
    public String deleteAccountException(Model model)
    {
        model.addAttribute("foundAccount",false);
        return "html/deleteAccount";
    }
}
