package com.bca.minibank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.bca.minibank.entity.TbUsers;

@Controller
public class HandleController {
	
	@GetMapping("/login")
	public String loginPage() {
		return "login.html";
	}
	
	@GetMapping("/register")
	public String registerPage(TbUsers tbUsers) {
		return "register.html";
	}
	
	@GetMapping("/home")
	public String homePage(TbUsers tbUsers) {
		return "home.html";
	}
	
}
