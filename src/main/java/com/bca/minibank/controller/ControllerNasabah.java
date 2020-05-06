package com.bca.minibank.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
import com.bca.minibank.entity.TbJnsTab;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormBikinPin;
import com.bca.minibank.form.FormMasukanPin;
import com.bca.minibank.form.FormRegisterUser;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbUserJnsTmp;
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
	DaoTbUserJnsTmp DaoTbUserJnsTmp;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/home") //URL Tidak Fix
	public String homePage(HttpServletRequest request, Model model) {
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      	model.addAttribute("nama", user.getNama());
		return "Home";
	}
	
	@GetMapping("/nasabah/pin") 
	public String pinRequestPage(Model model, FormMasukanPin formMasukanPin) 
	{
		return "pinrequest";
	}
	
	@PostMapping("/nasabah/pin") 
	public String cekSaldoPinRequestPost(Model model, @Valid FormMasukanPin formMasukanPin, BindingResult bindingResult, HttpSession session, HttpServletRequest request) 
	{
		boolean flagPin = false;
		boolean flagBlock = false;
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	TbUsers tbUsers = DaoTbUsers.getOne(user.getIdUser());
    	TbRekening tbRekening = DaoTbRekening.getOne(user.getNoRek());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();  
		if(tbRekening.getStatusRek().equals("NOT ACTIVE"))
		{
			flagBlock = true;
		}
		else if(!encoder.matches(formMasukanPin.getPin(), tbRekening.getPin()))
		{
			  
			int pinattempt = (Integer)session.getAttribute("pinattempt");
			pinattempt++;
			request.getSession().setAttribute("pinattempt", pinattempt);
			flagPin = true;
			if(pinattempt >= 3)
			{
				flagBlock = true;
//				tbRekening.setStatusRek("NOT ACTIVE");
//				tbUsers.setStatusUser("BLOCK");
//				tbUsers.setKeterangan("Akun anda terblokir dikarenakan salah password atau salah pin sebanyak 3x berturut-turut");
//				DaoTbRekening.update(tbRekening.getNoRek(), tbRekening);
//				DaoTbUsers.update(tbUsers.getIdUser(), tbUsers);
//				request.getSession().setAttribute("error", "Rekening dan users anda terblokir dikarenakan salah pin sebanyak 3x berturut-turut");
				return "redirect:/logout";
//				return "redirect:/login";
//				return "redirect:/login?error=true";
			}
		}
		if(bindingResult.hasErrors() || flagPin == true || flagBlock == true)
		{
			model.addAttribute("flagPin", flagPin);
			model.addAttribute("flagBlock", flagBlock);
			model.addAttribute("pinattempt", session.getAttribute("pinattempt"));
			return "pinrequest";
		}
		else
		{
			request.getSession().setAttribute("pintervalidasi", true);
			return (String)session.getAttribute("url");
		}
	}
		
	@GetMapping("/nasabah/ceksaldo") 
	public String cekSaldo(Model model, HttpSession session, HttpServletRequest request) 
	{
		if((Boolean)session.getAttribute("pintervalidasi") == null || (Boolean)session.getAttribute("pintervalidasi") == false)
		{
			request.getSession().setAttribute("url", "redirect:/nasabah/ceksaldo");
			return "redirect:/nasabah/pin";
		}
		else
		{
			request.getSession().setAttribute("pintervalidasi", false);
			MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening tbRekening = DaoTbRekening.getOne(user.getNoRek());
			
			//Reset jumlah pinattempt
			request.getSession().setAttribute("pinattempt", 0);
			
			//Mengubah saldo menjadi format currency
			DecimalFormat formatter = (DecimalFormat)NumberFormat.getCurrencyInstance(Locale.ROOT);
	        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.ITALY);
	        symbol.setCurrencySymbol("Rp.");
	        formatter.setDecimalFormatSymbols(symbol);
	        
	        //Tanggal saat ini
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");  
	        LocalDateTime now = LocalDateTime.now();  
	        
	        model.addAttribute("tanggal", dtf.format(now));
	        model.addAttribute("saldo", formatter.format(tbRekening.getSaldo()));
	    	model.addAttribute("nama", user.getNama());
			model.addAttribute("tbRekening", tbRekening);
			
			return "ceksaldo";
		}
	}	
	
	
	
	@GetMapping("/verifikasi") //fungsi Fix, URL tidak fix
	public String verifikasiPage(Model model) {
    	MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
    	model.addAttribute("noRek", TbRekeningTemp.getNoRek());
      	model.addAttribute("noKartu", TbRekeningTemp.getNoKartu());
      	model.addAttribute("nama", user.getNama());
      	model.addAttribute("username", user.getUsername());
      	model.addAttribute("jenisTabungan", TbRekeningTemp.getTbJnsTab().getNamaJnsTab());
		return "userterverifikasi";
	}		
	
	@GetMapping("/verifikasi/buatpin")
	public String buatPinPage(FormBikinPin formBikinPin) 
	{
		return "bikinpin";
	}
	
	@PostMapping("/verifikasi/buatpin")
	public String buatPinPost(Model model, @Valid FormBikinPin formBikinPin, BindingResult bindingResult) {
		boolean flagPin = false;
		if(!formBikinPin.getConfirmPin().equals(formBikinPin.getPin()))
		{
			flagPin = true;
		}
		if(bindingResult.hasErrors() || flagPin == true)
		{
			model.addAttribute("flagPin", flagPin);
			return "bikinpin";
		}
		else
		{
			MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
			TbRekeningTemp.setPin(bCryptPasswordEncoder.encode(formBikinPin.getPin()));
			DaoTbRekening.update(user.getNoRek(), TbRekeningTemp);
			return "redirect:/logout";
		}
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

