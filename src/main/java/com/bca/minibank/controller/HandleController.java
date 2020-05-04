package com.bca.minibank.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbUsers;

@Controller
public class HandleController {
	
	@Autowired
	private RepositoryTbUsers repositoryTbUsers;
	
	@Autowired
	private RepositoryTbRekening repositoryTbRekening;
	
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	@GetMapping("/register")
	public String registerPage(TbUsers tbUsers) {
		return "register";
	}
	
	@GetMapping("/home")
	public String homePage() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repositoryTbUsers.findByUsername(auth.getName());
//		System.out.println(auth.getName());
		System.out.println(tbUsers.getEmail());
		System.out.println(tbUsers.getNoHp());
		System.out.println(tbUsers.getStatusUser());
		System.out.println(tbUsers.getTbRekening().getNoRek());
		
		System.out.println("TB REKENING");
		TbRekening tbRekening = this.repositoryTbRekening.findByNoRek("089643");
		System.out.println(tbRekening.getSaldo());
		System.out.println(tbRekening.getStatusRek());
		System.out.println(tbRekening.getTbUsers().getNama());
		
		int i = Integer.parseInt("20000");
		int j = 550 + i;
		System.out.println("hasil "+j);
		
		
		Calendar cal = Calendar.getInstance(); 
        cal.add(Calendar.DAY_OF_MONTH, -2);
        System.out.println(cal.getTime());
        String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(cal.getTime());
		System.out.println(date);
		
		return "home";
	}
	
}
