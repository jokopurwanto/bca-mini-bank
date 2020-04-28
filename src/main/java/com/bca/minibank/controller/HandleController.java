package com.bca.minibank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;

import com.bca.minibank.configuration.MyUserPrincipal;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.dao.DaoTbUsers;

@Controller
public class HandleController {
	@Autowired
	DaoTbUsers DaoTbUsers;
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
//    @RequestMapping("/default")
//    public String defaultAfterLoginNasabah() 
//    {
//    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    	if(user.getPin() == null)
//    	{
//    		return "redirect:/konfirmasi";
//    	}
//    	else
//    	{
//    		return "redirect:/home";		
//    	}
//    }
	
	@GetMapping("/admin")
	public String adminPage(Model model) {
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	int idUser = user.getIdUser();
    	String nama = user.getNama();
    	String keterangan = user.getKeterangan();
    	model.addAttribute("idUser", idUser);
    	model.addAttribute("nama", nama);
		return "admin";
	}
	
	@GetMapping("/konfirmasi")
	public String konfirmasiPage(Model model) {
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	int idUser = user.getIdUser();
    	String nama = user.getNama();
    	String keterangan = user.getKeterangan();
    	model.addAttribute("idUser", idUser);
      	model.addAttribute("nama", nama);
      	model.addAttribute("keterangan", keterangan);
		return "konfirmasi";
	}
	
	@GetMapping("/register")
	public String registerPage(TbUsers tbUsers) {
		return "register";
	}
	
	@GetMapping("/home")
	public String homePage(Model model) {
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	int idUser = user.getIdUser();
    	String nama = user.getNama();
    	String keterangan = user.getKeterangan();
    	model.addAttribute("idUser", idUser);
      	model.addAttribute("nama", nama);
      	model.addAttribute("keterangan", keterangan);
		return "home";
	}
	
}
