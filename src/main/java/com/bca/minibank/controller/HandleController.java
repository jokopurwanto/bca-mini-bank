package com.bca.minibank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;

import com.bca.minibank.configuration.MyUserPrincipal;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbRekening;

@Controller
public class HandleController {
	@Autowired
	DaoTbUsers DaoTbUsers;
	
	@Autowired
	DaoTbRekening DaoTbRekening;
	
	@GetMapping("/")
	public String indexdirectPage() {
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
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
}
