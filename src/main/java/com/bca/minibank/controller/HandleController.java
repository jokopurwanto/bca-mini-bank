package com.bca.minibank.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.entity.TbJnsTab;
import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormRegisterUser;
import com.bca.minibank.model.ModelJenisTabungan;
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

	//	============================================ LOGIN =========================================

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
		session.removeAttribute("error");
		session.removeAttribute("message");
		return "/handle/login";
	}

	//	============================================ ADMIN =========================================

	@GetMapping("/admin")
	public String adminPage(Model model) {

		return "redirect:/admin/listusers/terverifikasi";
	}

	//	============================================ REGISTRASI =========================================

	@GetMapping("/registrasi")
	public String registrasiPage(Model model, FormRegisterUser formRegisterUser) {
		List<TbJnsTab> AllTbJnsTab = DaoTbJnsTab.getAll();
		model.addAttribute("AllTbJnsTab", AllTbJnsTab);
		return "/handle/registrasi";
	}

	@GetMapping("/registrasi/jenistabungan")
	public String jenistabunganPage(Model model) {
		List<TbJnsTab> AllTbJnsTab = DaoTbJnsTab.getAll();
		List<ModelJenisTabungan> AllModelJenisTabungan = new ArrayList<ModelJenisTabungan>();
		
		DecimalFormat formatter = (DecimalFormat)NumberFormat.getCurrencyInstance(Locale.ROOT);
		DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.ITALY);
		symbol.setCurrencySymbol("Rp.");
		formatter.setDecimalFormatSymbols(symbol);
		
		DecimalFormat InterestFormat = new DecimalFormat("#.##");
		for(TbJnsTab TbJnsTab : AllTbJnsTab)
		{
			ModelJenisTabungan ModelJenisTabungan = new ModelJenisTabungan();
			ModelJenisTabungan.setNamaJnsTab(TbJnsTab.getNamaJnsTab());
			ModelJenisTabungan.setLimitTransaksi(formatter.format(TbJnsTab.getLimitTransaksi()));
			ModelJenisTabungan.setBiayaAdmin(formatter.format(TbJnsTab.getBiayaAdmin()));
			ModelJenisTabungan.setBunga(InterestFormat.format(TbJnsTab.getBunga()*100) + " %");
			AllModelJenisTabungan.add(ModelJenisTabungan);
		}
		model.addAttribute("AllJnsTab", AllModelJenisTabungan);
		return "/handle/registrasijenistabungan";
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
			return "/handle/registrasi";
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
			tbUsers.setKeterangan("Akun anda sedang dalam proses verifikasi dari admin!");
			request.getSession().setAttribute("tbUsersTemp", tbUsers);

			TbJnsTab tbJnsTab = DaoTbJnsTab.getOne(formRegisterUser.getIdJnsTab());
			TbUserJnsTmp tbUserJnsTmp = new TbUserJnsTmp();
			tbUserJnsTmp.setTbJnsTab(tbJnsTab);
			tbUserJnsTmp.setTbUsers(tbUsers);
			request.getSession().setAttribute("tbUserJnsTmpTemp", tbUserJnsTmp);

			model.addAttribute("tbUsers", tbUsers);
			model.addAttribute("tbUserJnsTmp", tbUserJnsTmp);
			return "/handle/registrasikonfirmasi";
		}
	}

	@PostMapping("/registrasi/sukses")
	public String registrasisuksesPost(HttpSession session) 
	{
		DaoTbUsers.add((TbUsers)session.getAttribute("tbUsersTemp"));
		DaoTbUserJnsTmp.add((TbUserJnsTmp)session.getAttribute("tbUserJnsTmpTemp"));
		session.invalidate(); 
		return "/handle/registrasiberhasil";
	}
}
