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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bca.minibank.Form.FormTransaksi;
import com.bca.minibank.Model.ModelTransaksi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbUsers;
import com.bca.minibank.repository.RepostitoryTbTransaksi;


	@Controller
	//@RequestMapping("/setor")
	public class HandleController {
		@Autowired
		private RepostitoryTbTransaksi daoTbTransaksi;
		
		
		@Autowired
		private RepositoryTbUsers repositoryTbUsers;
		
		@Autowired
		private RepositoryTbRekening repositoryTbRekening;
	


		
		ModelTransaksi modelTransaksi;
		
		@GetMapping("/login")
		public String loginPage() {
			return "login";
		}
		
		@GetMapping("/register")
		public String registerPage(TbUsers tbUsers) {
			return "register";
		}
		@GetMapping("/home")
		public String homePage(Model model) {
			
			
			return "home";
		}
		
		@GetMapping("/nasabah/setor/minibank")
		public String SetorHome(Model model) {
			
			
			return "SetorTunai-1";
		}
		@GetMapping("/nasabah/setor")
		public String setorForm(Model model) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.repositoryTbUsers.findByUsername(auth.getName());
			System.out.println(tbUsers.getEmail());
		
			FormTransaksi formTransaksi = new FormTransaksi();
			formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
			model.addAttribute("formTransaksi", formTransaksi);
			
			return "setor";
		}
		
		@PostMapping("/nasabah/setor/konfirmasi")
		public String setor(Model model, @Valid FormTransaksi formTransaksi ,BindingResult rs) {
			//setorValidator.validate(formTransaksi, rs);
			if(rs.hasErrors()) {
				
				return"setor.html";
			}
	
			 	modelTransaksi = new ModelTransaksi(formTransaksi);
				System.out.println(modelTransaksi.getNominal());
				System.out.println(modelTransaksi.getNoRek());
				
				return "redirect:/nasabah/setor/konfirmasi";
			
		}
	
	
		@GetMapping("/nasabah/setor/konfirmasi")
		public String validateSetor(Model model) {
			
				FormTransaksi formTransaksi= new FormTransaksi();
				
				formTransaksi.setNominal(modelTransaksi.getNominal());
				formTransaksi.setNoRek(modelTransaksi.getNoRek());
				
				model.addAttribute("formTransaksi", formTransaksi);
			
				return "SetorTunai-2";
		
		}
		
		@PostMapping("/nasabah/setor/pengajuan-sukses")
		public String save(Model model,@Valid FormTransaksi formTransaksi) {
			
			TbTransaksi tbTransaksi = new TbTransaksi();
			
			tbTransaksi.setTglPengajuan(new Date());
			tbTransaksi.setJnsTransaksi("Setor Tunai");
			tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
			tbTransaksi.setStatusTransaksi("PENDING");
			tbTransaksi.setNominal(formTransaksi.getNominal());
			
			TbRekening tbRekening = this.repositoryTbRekening.findByNoRek(formTransaksi.getNoRek());
			
			tbTransaksi.setTbRekening(tbRekening);
		
			this.daoTbTransaksi.save(tbTransaksi);
			model.addAttribute("formTransaksi",tbTransaksi);
			
			return"Success";
		
		}
		
		@GetMapping("/nasabah/setor/status")
		public  String statusSetor(Model model){
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.repositoryTbUsers.findByUsername(auth.getName());
			
	
			TbTransaksi tbTransaksi = new TbTransaksi();
			
			FormTransaksi formTransaksi = new FormTransaksi();
			formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
			tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
			tbTransaksi.setJnsTransaksi("Setor Tunai");
			//model.addAttribute("status", this.daoTbTransaksi.findByNoRekTujuan(tbTransaksi.getNoRekTujuan()));
			model.addAttribute("status", this.daoTbTransaksi.findByNoRekTujuanANDJnsTransaksi(tbTransaksi.getNoRekTujuan(),tbTransaksi.getJnsTransaksi()));
			return "SetorTunai-3";
			
		}
		
		
		
	}
	
