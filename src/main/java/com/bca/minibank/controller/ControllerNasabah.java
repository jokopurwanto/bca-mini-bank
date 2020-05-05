package com.bca.minibank.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.Model.ModelTransaksi;
import com.bca.minibank.configuration.MBUserPrincipal;
import com.bca.minibank.entity.TbJnsTab;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormBikinPin;
import com.bca.minibank.form.FormMasukanPin;
import com.bca.minibank.form.FormRegisterUser;
import com.bca.minibank.form.FormTransaksi;
import com.bca.minibank.form.Password;
import com.bca.minibank.form.Pin;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbTransaksi;
import com.bca.minibank.repository.RepositoryTbUsers;
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
	
	@Autowired
	RepositoryTbUsers repositoryTbUsers;
	
	@Autowired
	RepositoryTbRekening repositoryTbRekening;
	
	@Autowired
	RepositoryTbTransaksi repositoryTbTransaksi;
	
	ModelTransaksi modelTransaksi;
	
	@GetMapping("/home") //URL Tidak Fix
	public String homePage(HttpServletRequest request, Model model) {
    	MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      	model.addAttribute("nama", user.getNama());
		return "Home";
	}
	
	@GetMapping("/home/ceksaldo") //URL Tidak Fix
	public String cekSaldoPage(Model model, FormMasukanPin formMasukanPin) 
	{
		return "ceksaldopinrequest";
	}
	
	@PostMapping("/home/ceksaldo") //URL Tidak Fix
	public String cekSaldoPinRequestPost(Model model, @Valid FormMasukanPin formMasukanPin, BindingResult bindingResult, HttpSession session, HttpServletRequest request) 
	{
		boolean flagPin = false;
		boolean flagBlock = false;
    	MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
				tbRekening.setStatusRek("NOT ACTIVE");
				DaoTbRekening.update(tbRekening.getNoRek(), tbRekening);
			}
		}
		if(bindingResult.hasErrors() || flagPin == true || flagBlock == true)
		{
			model.addAttribute("flagPin", flagPin);
			model.addAttribute("flagBlock", flagBlock);
			model.addAttribute("pinattempt", session.getAttribute("pinattempt"));
			return "ceksaldopinrequest";
		}
		else
		{
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
		
	@GetMapping("/konfirmasi") //fungsi Fix, URL tidak fix
	public String konfirmasiPage(Model model) {
    	MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
    	model.addAttribute("noRek", TbRekeningTemp.getNoRek());
      	model.addAttribute("noKartu", TbRekeningTemp.getNoKartu());
      	model.addAttribute("nama", user.getNama());
      	model.addAttribute("username", user.getUsername());
      	model.addAttribute("jenisTabungan", TbRekeningTemp.getTbJnsTab().getNamaJnsTab());
		return "userterverifikasi";
	}		
	
	@GetMapping("/konfirmasi/buatpin")
	public String buatPinPage(FormBikinPin formBikinPin) 
	{
		return "bikinpin";
	}
	
	@PostMapping("/konfirmasi/buatpin")
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
			MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
			TbRekeningTemp.setPin(bCryptPasswordEncoder.encode(formBikinPin.getPin()));
			DaoTbRekening.update(user.getNoRek(), TbRekeningTemp);
			return "bikinpinberhasil";
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
	
	// --- UBAH PASSWORD
		@GetMapping("/home/Ubahpassword")
		public String changePasswordpage(Model model,Password password) {
		
			return "UbahPassword";
		}
		
		// --- UPDATE PASSWORD
		@PostMapping("/home/Updatepassword")
		public String updatePasswordpage(Model model, @Valid Password password , BindingResult rs, HttpServletRequest request) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
			MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbUsers tbUsers = this.repositoryTbUsers.getOne(user.getIdUser());
			if(encoder.matches(password.getOldpassword(), tbUsers.getPassword())) {
					tbUsers.setPassword(encoder.encode(password.getNewpassword()));
						System.out.println(password.getNewpassword());
					repositoryTbUsers.save(tbUsers);
					return "UbahPassword";
			}
			return "UbahPassword";
		}

		// --- UBAH PIN
		@GetMapping("/home/Ubahpin")
		public String changePinpage(Model model,Pin pin) {
		
			return "UbahPin";
		}
		
		// --- UPDATE PIN
		@PostMapping("/home/Updatepin")
		public String updatePinpage(Model model, @Valid Pin pin , BindingResult rs, HttpServletRequest request) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
			MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening tbRekening = this.repositoryTbRekening.getOne(user.getNoRek());
				if(encoder.matches(pin.getOldPin(), tbRekening.getPin())) {
					tbRekening.setPin(encoder.encode(pin.getNewPin()));
						System.out.println(pin.getNewPin());
					repositoryTbRekening.save(tbRekening);
					return "UbahPin";		
			}
			return "UbahPin";
		}
		
		// SETOR TUNAI
		
//		@GetMapping("/home/SetorHome")
//		public String SetorHome(Model model, FormTransaksi formTransaksi) {
//			
//			
//			return "SetorTunai-2";
//		}
		
		@GetMapping("/home/setorForm")
		public String setorForm(Model model) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.repositoryTbUsers.findByUsername(auth.getName());
			System.out.println(tbUsers.getEmail());
		
			FormTransaksi formTransaksi = new FormTransaksi();
			formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
			model.addAttribute("formTransaksi", formTransaksi);
			
			return "setor";
		}
		
		@PostMapping("/home/setor")
		public String setor(Model model, @Valid FormTransaksi formTransaksi ,BindingResult rs) {
			//setorValidator.validate(formTransaksi, rs);
			if(rs.hasErrors()) {
				
				return"setor.html";
			}
	
			 	modelTransaksi = new ModelTransaksi(formTransaksi);
				System.out.println(modelTransaksi.getNominal());
				System.out.println(modelTransaksi.getNoRek());
				
				return "SetorTunai-2";
		}
	
		@GetMapping("/home/validate")
		public String validateSetor(Model model) {
			
				FormTransaksi formTransaksi= new FormTransaksi();
				
				formTransaksi.setNominal(modelTransaksi.getNominal());
				formTransaksi.setNoRek(modelTransaksi.getNoRek());
				
				model.addAttribute("formTransaksi", formTransaksi);
			
				return "SetorTunai-2";
		
		}
		
		@PostMapping("/home/save")
		public String save(Model model,@Valid FormTransaksi formTransaksi) {
			
			TbTransaksi tbTransaksi = new TbTransaksi();
			
			tbTransaksi.setTglTransaksi(new Date());
			tbTransaksi.setJnsTransaksi("Setor Tunai");
			tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
			tbTransaksi.setStatusTransaksi("PENDING");
			tbTransaksi.setNominal(formTransaksi.getNominal());
			
			TbRekening tbRekening = this.repositoryTbRekening.findByNoRek(formTransaksi.getNoRek());
			
			tbTransaksi.setTbRekening(tbRekening);
		
			this.repositoryTbTransaksi.save(tbTransaksi);
			model.addAttribute("formTransaksi",tbTransaksi);
			
			return"Success";
		
		}
		
		@GetMapping("/home/statusSetor")
		public  String statusSetor(Model model){
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.repositoryTbUsers.findByUsername(auth.getName());	
			TbTransaksi tbTransaksi = new TbTransaksi();
			FormTransaksi formTransaksi = new FormTransaksi();
			formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
			tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
			tbTransaksi.setJnsTransaksi("Setor Tunai");
			//model.addAttribute("status", this.daoTbTransaksi.findByNoRekTujuan(tbTransaksi.getNoRekTujuan()));
			model.addAttribute("status", this.repositoryTbTransaksi.findByNoRekTujuanANDJnsTransaksi(tbTransaksi.getNoRekTujuan(),tbTransaksi.getJnsTransaksi()));
			return "SetorTunai-3";
			
		  }			
}

