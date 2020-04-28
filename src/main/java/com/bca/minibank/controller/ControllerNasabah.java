package com.bca.minibank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.configuration.MyUserPrincipal;
import com.bca.minibank.repository.RepositoryTbJnsTab;
import com.bca.minibank.dao.DaoTbUsers;


@Controller
public class ControllerNasabah {

	//Testing
	@Autowired
	private RepositoryTbJnsTab repoJnsTab;
		
	@GetMapping("/")
	public String test() {
		return "index";
	}
	
	@GetMapping("/list")
	public String getAll(Model model) {
		model.addAttribute("jnsTab", this.repoJnsTab.findAll());
		System.out.println(repoJnsTab.findAll());
		return "list";
	}
	////////////////////////////////////

	DaoTbUsers DaoTbUsers;
	
	@GetMapping("/konfirmasi/buatpin")
	public String buatPinPage(Model model) {
		return "buatpin";
	}
	
	@PostMapping("/konfirmasi/buatpin")
	public String buatPinPost(Model model, String pin) {
		MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return "redirect:/home";
	}
}
