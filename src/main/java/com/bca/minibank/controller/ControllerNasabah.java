package com.bca.minibank.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.configuration.MyUserPrincipal;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbJnsTab;

@Controller
public class ControllerNasabah {
	@Autowired
	DaoTbUsers DaoTbUsers;
	
	@Autowired
	DaoTbRekening DaoTbRekening;
	
	@Autowired
	DaoTbJnsTab DaoTbJnsTab;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/konfirmasi/buatpin")
	public String buatPinPage(Model model) {

		return "bikinpin";
	}
	
	@PostMapping("/konfirmasi/buatpin")
	public String buatPinPost(Model model, String pin) {
		MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
		TbRekeningTemp.setPin(bCryptPasswordEncoder.encode(pin));
		DaoTbRekening.update(user.getNoRek(), TbRekeningTemp);
		return "redirect:/logout";
	}
	
	@GetMapping("/registrasi")
	public String registrasiPage(TbUsers tbUsers) {
		return "registrasi";
	}
	
	@GetMapping("/registrasi/tes")
	public String registrasitesPage(Model model, TbUsers tbUsers) {
	//	TbUsers TbUsers = DaoTbUsers.findTbUsersByEmail("Block@block.co.id");
		TbUsers TbUsers = DaoTbUsers.findTbUsersByNoHp("081299990000");
//		TbUsers.setStatusUser("PENDING");
//		TbUsers.setRole("NASABAH");
//		TbUsers.setKeterangan("User sedang dalam proses verifikasi dari admin!");
		model.addAttribute("tbUsers", TbUsers);
		return "registrasikonfirmasi";
	}
	
	@PostMapping("/registrasi/konfirmasi")
	public String registrasiPost(HttpServletRequest request, Model model, @Valid TbUsers tbUsers, BindingResult bindingResult) 
	{
		boolean flagU = false;
		boolean flagE = false;
		boolean flagNoHp = false;
		boolean flagNoKtp = false;
		if(DaoTbUsers.findTbUsersByUsername(tbUsers.getUsername()) != null)
		{
			flagU = true;
		}
		if(DaoTbUsers.findTbUsersByEmail(tbUsers.getEmail()) != null)
		{
			flagE = true;
		}
		if(DaoTbUsers.findTbUsersByNoHp(tbUsers.getNoHp()) != null)
		{
			flagNoHp = true;
		}
		if(DaoTbUsers.findTbUsersByNoKtp(tbUsers.getNoKtp()) != null)
		{
			flagNoKtp = true;
		}
		if(bindingResult.hasErrors() || flagU == true || flagE == true || flagNoHp == true || flagNoKtp == true)
		{
			model.addAttribute("flagU", flagU);
			model.addAttribute("flagE", flagE);
			model.addAttribute("flagNoHp", flagNoHp);
			model.addAttribute("flagNoKtp", flagNoKtp);
			return "registrasi";
		}
		else
		{
			tbUsers.setPassword(bCryptPasswordEncoder.encode(tbUsers.getPassword()));
			tbUsers.setStatusUser("PENDING");
			tbUsers.setRole("NASABAH");
			tbUsers.setKeterangan("User sedang dalam proses verifikasi dari admin!");
			request.getSession().setAttribute("tbUsersTemp", tbUsers);
			model.addAttribute("tbUsers", tbUsers);
			return "registrasikonfirmasi";
		}
	}
	
	@PostMapping("/registrasi/sukses")
	public String registrasisuksesPost(HttpSession session) 
	{
		DaoTbUsers.add((TbUsers)session.getAttribute("tbUsersTemp"));
		session.invalidate(); 
		return "registrasiberhasil";
	}
}
