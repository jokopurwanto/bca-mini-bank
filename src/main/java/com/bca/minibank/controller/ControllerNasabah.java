package com.bca.minibank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbUsers;

@Controller
public class ControllerNasabah {

	@Autowired
	private RepositoryTbUsers repoTbUser;
	
	@GetMapping("/")
	public String test() {
		return "index";
	}
	
	@GetMapping("/list")
	public String getAll(Model model) {
		model.addAttribute("users", this.repoTbUser.findAll());
		return "list";
	}
	
}
