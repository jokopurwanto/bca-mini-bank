package com.bca.minibank.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.catalina.core.ApplicationContext;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;


import com.bca.minibank.Model.ModelTransaksi;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormTransaksi;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbUsers;
import com.bca.minibank.repository.RepositoryTbTransaksi;

import com.bca.minibank.configuration.MBUserPrincipal;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbRekening;


	@Controller
	public class HandleController {
		
		@Autowired
		private DaoTbUsers daoTbUsers;
	  
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
	    	MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	int idUser = user.getIdUser();
	    	String nama = user.getNama();
	    	String keterangan = user.getKeterangan();
	    	model.addAttribute("idUser", idUser);
	    	model.addAttribute("nama", nama);
			return "admin";
		}	
		
//		@GetMapping("/login")
//		public String loginPage() {
//			return "login";
//		}
		
//		@GetMapping("/register")
//		public String registerPage(TbUsers tbUsers) {
//			return "register";
//		}
//		@GetMapping("/home")
//		public String homePage(Model model) {
//			
//			
//			return "home";
//		}
		
}

