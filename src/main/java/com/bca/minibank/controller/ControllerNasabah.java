package com.bca.minibank.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormBikinPin;
import com.bca.minibank.form.FormMasukanPin;
import com.bca.minibank.form.FormTransaksi;
import com.bca.minibank.form.Password;
import com.bca.minibank.form.Pin;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbTransaksi;
import com.bca.minibank.repository.RepositoryTbUsers;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbTransaksi;
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
	DaoTbTransaksi DaoTbTransaksi;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	RepositoryTbUsers repositoryTbUsers;

	@Autowired
	RepositoryTbRekening repositoryTbRekening;

	@Autowired
	RepositoryTbTransaksi repositoryTbTransaksi;

	ModelTransaksi modelTransaksi;

	@GetMapping("/verifikasi") //fungsi Fix, URL tidak fix
	public String verifikasiPage(Model model) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
		model.addAttribute("noRek", TbRekeningTemp.getNoRek());
		model.addAttribute("noKartu", TbRekeningTemp.getNoKartu());
		model.addAttribute("nama", user.getNama());
		model.addAttribute("username", user.getUsername());
		model.addAttribute("jenisTabungan", TbRekeningTemp.getTbJnsTab().getNamaJnsTab());
		return "/nasabah/userterverifikasi";
	}		

	@GetMapping("/verifikasi/buatpin")
	public String buatPinPage(FormBikinPin formBikinPin) 
	{
		return "/nasabah/bikinpin";
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
			return "/nasabah/bikinpin";
		}
		else
		{
			MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening TbRekeningTemp = DaoTbRekening.getOne(user.getNoRek());
			TbRekeningTemp.setPin(bCryptPasswordEncoder.encode(formBikinPin.getPin()));
			DaoTbRekening.update(user.getNoRek(), TbRekeningTemp);
			return "/nasabah/bikinpinberhasil";
		}
	}

	@GetMapping("/beranda")
	public String homePage(HttpServletRequest request, Model model) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("nama", user.getNama());
		return "/nasabah/beranda";
	}

	@GetMapping("/nasabah/pin") 
	public String pinRequestPage(Model model, FormMasukanPin formMasukanPin) 
	{
		return "/nasabah/pinrequest";
	}

	@PostMapping("/nasabah/pin") 
	public String pinRequestPost(Model model, @Valid FormMasukanPin formMasukanPin, BindingResult bindingResult, HttpSession session, HttpServletRequest request) 
	{
		boolean flagPin = false;
		boolean flagBlock = false;
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
				tbRekening.setStatusRek("NOT ACTIVE");
				tbUsers.setStatusUser("BLOCK");
				tbUsers.setKeterangan("Akun anda terblokir dikarenakan salah pin sebanyak 3x berturut-turut");
				DaoTbRekening.update(tbRekening.getNoRek(), tbRekening);
				DaoTbUsers.update(tbUsers.getIdUser(), tbUsers);
				return "redirect:/logout";
			}
		}
		if(bindingResult.hasErrors() || flagPin == true || flagBlock == true)
		{
			model.addAttribute("flagPin", flagPin);
			model.addAttribute("flagBlock", flagBlock);
			model.addAttribute("pinattempt", session.getAttribute("pinattempt"));
			return "nasabah/pinrequest";
		}
		else
		{
			//Reset jumlah pinattempt
			request.getSession().setAttribute("pinattempt", 0);
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
			MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening tbRekening = DaoTbRekening.getOne(user.getNoRek());

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

			return "/nasabah/ceksaldo";
		}
	}	

	// --- UBAH PASSWORD
	@GetMapping("/nasabah/ubahpassword")
	public String changePasswordpage(Model model,Password password) {

		return "/nasabah/cekpassword";
	}

	// --- UPDATE PASSWORD
	@PostMapping("/nasabah/ubahpassword")
	public String Passwordpage(Model model, @Valid Password password , BindingResult rs, HttpServletRequest request) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TbUsers tbUsers = this.repositoryTbUsers.getOne(user.getIdUser());
		String msg = "";
		if(!(rs.hasErrors()))
		{
			if((encoder.matches(password.getOldpassword(), tbUsers.getPassword()))) {
				return "/nasabah/UbahPassword";

			} else {
				msg = "Password anda salah";
				model.addAttribute("msg", msg);
				return "/nasabah/cekpassword";
			}
		}
		return "/nasabah/UbahPassword";
	}			


	@PostMapping("/nasabah/simpan/password")
	public String updatePasswordpage(Model model, @Valid Password password , BindingResult rs, HttpServletRequest request) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		TbUsers tbUsers = this.repositoryTbUsers.getOne(user.getIdUser());
		String msg = "";
		if(password.getNewpassword().equals(password.getConfirmpass())) {
			tbUsers.setPassword(passwordEncoder.encode(password.getNewpassword()));
			repositoryTbUsers.save(tbUsers);
			msg = "Password berhasil diubah.";
		} else {
			msg="Konfirmasi password berbeda";
		}
		model.addAttribute("msg", msg);
		return "/nasabah/UbahPassword";
	}

	// --- UBAH PIN
	@GetMapping("/nasabah/ubahpin")
	public String changePinpage(Model model,Pin pin) {

		return "/nasabah/cekpin";
	}

	// --- UPDATE PIN
	@PostMapping("/nasabah/ubahpin")
	public String Pinpage(Model model, String noRek, @Valid Pin pin , BindingResult rs, HttpServletRequest request) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TbRekening tbRekening = this.repositoryTbRekening.getOne(user.getNoRek());
		String msg = "";
		if(!(rs.hasErrors()))
		{
			if((encoder.matches(pin.getOldPin(), tbRekening.getPin()))) {
				return "/nasabah/UbahPin";
			} else {
				msg = "Pin Anda Salah";
				model.addAttribute("msg", msg);
				return "/nasabah/cekpin";
			}
		}
		return "/nasabah/UbahPin";
	}	

	@PostMapping("/nasabah/simpan/pin")
	public String updatePinpage(Model model, @Valid Pin pin , BindingResult rs, HttpServletRequest request) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		TbRekening tbRekening = this.repositoryTbRekening.getOne(user.getNoRek());
		String msg = "";
		if(pin.getNewPin().equals(pin.getConfirmpin())) {
			tbRekening.setPin(passwordEncoder.encode(pin.getNewPin()));
			repositoryTbRekening.save(tbRekening);
			msg = "Pin berhasil diubah.";
		} else {
			msg="Konfirmasi pin berbeda";
		}
		model.addAttribute("msg", msg);
		return "/nasabah/UbahPin";
	}

	@GetMapping("/nasabah/setor")
	public String setorForm(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.DaoTbUsers.findByUsername(auth.getName());
		System.out.println(tbUsers.getEmail());

		FormTransaksi formTransaksi = new FormTransaksi();
		formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
		model.addAttribute("formTransaksi", formTransaksi);

		return "/nasabah/setor";
	}

	@PostMapping("/nasabah/setor/konfirmasi")
	public String setor(Model model, @Valid FormTransaksi formTransaksi ,BindingResult rs) {
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

		return "/nasabah/SetorTunai-2";

	}

	@PostMapping("/nasabah/setor/pengajuan-sukses")
	public String save(Model model,@Valid FormTransaksi formTransaksi) {

		TbTransaksi tbTransaksi = new TbTransaksi();

		tbTransaksi.setTglPengajuan(new Date());
		tbTransaksi.setJnsTransaksi("SETOR TUNAI");
		tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
		tbTransaksi.setStatusTransaksi("PENDING");
		tbTransaksi.setNominal(formTransaksi.getNominal());

		TbRekening tbRekening = this.DaoTbRekening.noRek(formTransaksi.getNoRek());
		
		tbTransaksi.setTbRekening(tbRekening);
	
		this.DaoTbTransaksi.add(tbTransaksi);
		model.addAttribute("formTransaksi",tbTransaksi);

		return"/nasabah/Success";

	}

	@GetMapping("/nasabah/setor/status")
	public  String statusSetor(Model model){

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.DaoTbUsers.findByUsername(auth.getName());


		TbTransaksi tbTransaksi = new TbTransaksi();

		FormTransaksi formTransaksi = new FormTransaksi();
		formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
		tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
		tbTransaksi.setJnsTransaksi("SETOR TUNAI");
		//model.addAttribute("status", this.daoTbTransaksi.findByNoRekTujuan(tbTransaksi.getNoRekTujuan()));
		model.addAttribute("status", this.repositoryTbTransaksi.findByNoRekTujuanANDJnsTransaksi(tbTransaksi.getNoRekTujuan(),tbTransaksi.getJnsTransaksi()));
		return "/nasabah/SetorTunai-3";

	}

}