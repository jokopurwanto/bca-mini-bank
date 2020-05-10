package com.bca.minibank.configuration;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.RequestDispatcher;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MBErrorHandler implements ErrorController  {
 
    @GetMapping("/error")
    public String handleError(Model model, HttpServletRequest request) 
    {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("errorCode", statusCode);
            if(statusCode == HttpStatus.NOT_FOUND.value()) 
            {
                model.addAttribute("errorMessage", "Maaf, halaman anda yang cari tidak ditemukan!");
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) 
            {
            	model.addAttribute("errorMessage", "Maaf, terdapat kendala pada server kami!");
            }
        }
        return "error";
    }
    
    @Override
    public String getErrorPath() {
        return "/error";
    }
}