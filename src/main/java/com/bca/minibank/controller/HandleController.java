package com.bca.minibank.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
	public String loginPage(Model model, HttpSession session) {
		String error = (String)session.getAttribute("error");
		if(error!= null)
		{
			model.addAttribute("error", error);
		}
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
