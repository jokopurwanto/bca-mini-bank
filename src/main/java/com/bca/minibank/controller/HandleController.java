package com.bca.minibank.controller;

import java.util.List;

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

import com.bca.minibank.configuration.MBUserPrincipal;
import com.bca.minibank.entity.TbJnsTab;
import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormRegisterUser;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbJnsTab;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbUserJnsTmp;

@Controller
public class HandleController {
	@Autowired
	DaoTbUsers DaoTbUsers;
	
	@Autowired
	DaoTbRekening DaoTbRekening;
	
	@Autowired
	DaoTbJnsTab DaoTbJnsTab;
	
	@Autowired
	DaoTbUserJnsTmp DaoTbUserJnsTmp;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/")
	public String indexdirectPage() {
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String loginPage(Model model, HttpSession session) {
		String error = (String)session.getAttribute("error");
		String message = (String)session.getAttribute("message");
		if(error!= null)
		{
			model.addAttribute("error", error);
		}
		else if(message!=null)
		{
			model.addAttribute("message", message);
		}
		session.invalidate();
		return "login";
	}
	
	@GetMapping("/admin")
	public String adminPage(Model model) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	int idUser = user.getIdUser();
    	String nama = user.getNama();
    	model.addAttribute("idUser", idUser);
    	model.addAttribute("nama", nama);
		return "admin";
	}
	
	@GetMapping("/registrasi")
	public String registrasiPage(Model model, FormRegisterUser formRegisterUser) {
		List<TbJnsTab> AllTbJnsTab = DaoTbJnsTab.getAll();
		model.addAttribute("AllTbJnsTab", AllTbJnsTab);
		return "registrasi";
	}
	
	@PostMapping("/registrasi/konfirmasi")
	public String registrasiPost(HttpServletRequest request, Model model, @Valid FormRegisterUser formRegisterUser, BindingResult bindingResult) 
	{
		boolean flagU = false;
		boolean flagE = false;
		boolean flagNoHp = false;
		boolean flagNoKtp = false;
		boolean flagCPass = false;
		if(DaoTbUsers.findTbUsersByUsername(formRegisterUser.getUsername()) != null)
		{
			flagU = true;
		}
		if(DaoTbUsers.findTbUsersByEmail(formRegisterUser.getEmail()) != null)
		{
			flagE = true;
		}
		if(DaoTbUsers.findTbUsersByNoHp(formRegisterUser.getNoHp()) != null)
		{
			flagNoHp = true;
		}
		if(DaoTbUsers.findTbUsersByNoKtp(formRegisterUser.getNoKtp()) != null)
		{
			flagNoKtp = true;
		}
		if(!formRegisterUser.getPassword().equals(formRegisterUser.getConfirmPassword()))
		{
			flagCPass = true;
		}
		if(bindingResult.hasErrors() || flagU == true || flagE == true || flagNoHp == true || flagNoKtp == true || flagCPass == true)
		{
			model.addAttribute("flagU", flagU);
			model.addAttribute("flagE", flagE);
			model.addAttribute("flagNoHp", flagNoHp);
			model.addAttribute("flagNoKtp", flagNoKtp);
			model.addAttribute("flagCPass", flagCPass);
			
			List<TbJnsTab> AllTbJnsTab = DaoTbJnsTab.getAll();
			model.addAttribute("AllTbJnsTab", AllTbJnsTab);
			return "registrasi";
		}
		else
		{
			TbUsers tbUsers = new TbUsers();
			tbUsers.setNama(formRegisterUser.getNama());
			tbUsers.setNoKtp(formRegisterUser.getNoKtp());
			tbUsers.setNoHp(formRegisterUser.getNoHp());
			tbUsers.setAlamat(formRegisterUser.getAlamat());
			tbUsers.setEmail(formRegisterUser.getEmail());
			tbUsers.setUsername(formRegisterUser.getUsername());
			tbUsers.setPassword(bCryptPasswordEncoder.encode(formRegisterUser.getPassword()));
			tbUsers.setStatusUser("PENDING");
			tbUsers.setRole("NASABAH");
			tbUsers.setKeterangan("User sedang dalam proses verifikasi dari admin!");
			request.getSession().setAttribute("tbUsersTemp", tbUsers);
			
			TbJnsTab tbJnsTab = DaoTbJnsTab.getOne(formRegisterUser.getIdJnsTab());
			TbUserJnsTmp tbUserJnsTmp = new TbUserJnsTmp();
			tbUserJnsTmp.setTbJnsTab(tbJnsTab);
			tbUserJnsTmp.setTbUsers(tbUsers);
			request.getSession().setAttribute("tbUserJnsTmpTemp", tbUserJnsTmp);
			
			model.addAttribute("tbUsers", tbUsers);
			model.addAttribute("tbUserJnsTmp", tbUserJnsTmp);
			return "registrasikonfirmasi";
		}
	}
	
	@PostMapping("/registrasi/sukses")
	public String registrasisuksesPost(HttpSession session) 
	{
		DaoTbUsers.add((TbUsers)session.getAttribute("tbUsersTemp"));
		DaoTbUserJnsTmp.add((TbUserJnsTmp)session.getAttribute("tbUserJnsTmpTemp"));
		session.invalidate(); 
		return "registrasiberhasil";
	}
}

