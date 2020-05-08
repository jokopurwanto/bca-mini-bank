package com.bca.minibank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bca.minibank.repository.RepositoryTbJnsTab;


@Controller
public class ControllerNasabah {

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
	
}
