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
	
	@GetMapping("/konfirmasi") //fungsi Fix, URL tidak fix
	public String konfirmasiPage(Model model) {
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
    	model.addAttribute("noRek", TbRekeningTemp.getNoRek());
      	model.addAttribute("noKartu", TbRekeningTemp.getNoKartu());
      	model.addAttribute("nama", user.getNama());
      	model.addAttribute("username", user.getUsername());
      	model.addAttribute("jenisTabungan", TbRekeningTemp.getTbJnsTab().getNamaJnsTab());
		return "userterverifikasi";
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
