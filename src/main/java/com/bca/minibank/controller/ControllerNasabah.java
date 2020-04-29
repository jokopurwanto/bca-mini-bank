package com.bca.minibank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.configuration.MyUserPrincipal;
import com.bca.minibank.entity.TbRekening;
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

		return "buatpin.html";
	}
	
	@PostMapping("/konfirmasi/buatpin")
	public String buatPinPost(Model model, String pin) {
		MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
		TbRekeningTemp.setPin(bCryptPasswordEncoder.encode(pin));
		DaoTbRekening.update(user.getNoRek(), TbRekeningTemp);
		return "redirect:/logout";
	}
}
