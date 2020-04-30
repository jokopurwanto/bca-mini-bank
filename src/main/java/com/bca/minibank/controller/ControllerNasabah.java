package com.bca.minibank.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormTransferPage;
import com.bca.minibank.form.FormTransferValidationPage;
import com.bca.minibank.model.ModelSession;
import com.bca.minibank.model.ModelTransferPage;
import com.bca.minibank.repository.RepositoryTbJnsTab;
import com.bca.minibank.repository.RepositoryTbLogAdmin;
import com.bca.minibank.repository.RepositoryTbMutasi;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbUsers;
import com.bca.minibank.repository.RepostitoryTbTransaksi;
import com.bca.minibank.utils.UtilsSession;

@Controller
public class ControllerNasabah {
	
//	private static final SimpleDateFormat sdf =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	@Autowired
	private RepositoryTbJnsTab repoJnsTab;
	
	@Autowired
	private RepositoryTbUsers repoTbUsers;
	
	@Autowired
	private RepositoryTbRekening repoTbRekening;
	
	@Autowired
	private RepostitoryTbTransaksi repoTbTransaksi; 
	
	@Autowired
	private RepositoryTbMutasi repoTbMutasi;
	
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
	
	@GetMapping("/transfer")
	public String getTransfer(Model model, HttpServletRequest req) {
	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		formTransferPage.setNoRek(tbUsers.getTbRekening().getNoRek());
		model.addAttribute("formTransferPage", formTransferPage);
	
		return "form-transfer";
	}
	
	
	@PostMapping("/transfer")
	public String postTransfer(@Valid FormTransferPage formTransferPage, BindingResult result, Model model, HttpServletRequest req) {
		if(result.hasErrors()) {
			return "form-transfer";
		}
    
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = new ModelTransferPage(formTransferPage);
		modelSession.setModelTransferPage(modelTransferPage);
		
	
		return "redirect:/transferValidation";
	}
	
	@GetMapping("/transferValidation")
	public String getTransferValidation(Model model, HttpServletRequest req) {
		
	
		

		
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		
		
		FormTransferValidationPage formTransferValidationPage = new FormTransferValidationPage();
		formTransferValidationPage.setNoRek(formTransferPage.getNoRek());
		formTransferValidationPage.setKeterangan(formTransferPage.getKeterangan());
		formTransferValidationPage.setNominal(formTransferPage.getNominal());
		formTransferValidationPage.setNoRekTujuan(formTransferPage.getNoRekTujuan());
		
		formTransferValidationPage.setJnsTransaksi("Transfer");
		
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		formTransferValidationPage.setTglTransaksi(date);
		
		
		TbRekening tbRekening = this.repoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setNamaPenerima(tbRekening.getTbUsers().getNama());
		
		model.addAttribute("formTransferValidationPage", formTransferValidationPage);
		
		
		
		return "form-transfer-validation";
	}
	
	@PostMapping("/transferValidation")
	public String postTransferValidation(@Valid FormTransferValidationPage formTransferValidationPage, BindingResult result, Model model, HttpServletRequest req) {
		if(result.hasErrors()) {
			return "form-transfer-validation";
		}
		
		TbRekening rekPengirim = this.repoTbRekening.findByNoRek(formTransferValidationPage.getNoRek()); //get data norek untuk di set pada table transaksi 
		System.out.println(formTransferValidationPage.getPin());
		System.out.println(formTransferValidationPage.getPin());
		
		if(!formTransferValidationPage.getPin().equals(rekPengirim.getPin())) {
			return "form-transfer-validation";
		}
		
//		insert to table transaksi
		TbTransaksi tbTransaksi = new TbTransaksi();
		tbTransaksi.setJnsTransaksi(formTransferValidationPage.getJnsTransaksi());
		tbTransaksi.setNominal(formTransferValidationPage.getNominal());
		tbTransaksi.setNoRekTujuan(formTransferValidationPage.getNoRekTujuan());
		tbTransaksi.setStatusTransaksi("SUCCESS");
		tbTransaksi.setTglTransaksi(new Date());

		tbTransaksi.setTbRekening(rekPengirim);
		this.repoTbTransaksi.save(tbTransaksi);
		
//		set tmp saldo pengirim for mutasi
		double tmpSaldoPengirim = rekPengirim.getSaldo();
		
//		mengurangi saldo pengirim
		rekPengirim.setSaldo(rekPengirim.getSaldo() - formTransferValidationPage.getNominal());		
		
//		menambah nilai saldo limit harian pengirim
		rekPengirim.setTransaksiHarian(rekPengirim.getTransaksiHarian() + formTransferValidationPage.getNominal());
		this.repoTbRekening.save(rekPengirim);
		
//		menambah saldo penerima
		TbRekening rekPenerima = this.repoTbRekening.findByNoRek(formTransferValidationPage.getNoRekTujuan());
//		set tmp saldo penerima for mutasi
		double tmpSaldoPenerima = rekPenerima.getSaldo();
		rekPenerima.setSaldo(rekPenerima.getSaldo() + formTransferValidationPage.getNominal());
		this.repoTbRekening.save(rekPenerima);
		
		
//		meanmbah ke tabel mutasi [pengirim]
		TbMutasi mutasiPengirim = new TbMutasi();
		mutasiPengirim.setJnsMutasi("UANG KELUAR");
		mutasiPengirim.setNominal(formTransferValidationPage.getNominal());
		mutasiPengirim.setSaldoAkhir(tmpSaldoPengirim - formTransferValidationPage.getNominal());
		mutasiPengirim.setNote(formTransferValidationPage.getKeterangan());
		mutasiPengirim.setTglMutasi(new Date());
		mutasiPengirim.setNoRek(rekPengirim.getNoRek());
		mutasiPengirim.setTbTransaksi(tbTransaksi);

		
//		meanmbah ke tabel mutasi [penerma]
		TbMutasi mutasiPenerima = new TbMutasi();
		mutasiPenerima.setJnsMutasi("UANG MASUK");
		mutasiPenerima.setNominal(formTransferValidationPage.getNominal());
		mutasiPenerima.setSaldoAkhir(tmpSaldoPenerima + formTransferValidationPage.getNominal());
		mutasiPenerima.setNote(formTransferValidationPage.getKeterangan());
		mutasiPenerima.setTglMutasi(new Date());
		mutasiPenerima.setNoRek(formTransferValidationPage.getNoRekTujuan());
		mutasiPenerima.setTbTransaksi(tbTransaksi);
		
//		insert to table mutasi
		this.repoTbMutasi.save(mutasiPengirim);
		this.repoTbMutasi.save(mutasiPenerima);
		
		return "redirect:/transferSuccess";
	}
	
	@GetMapping("/transferSuccess")
	public String getTransferSuccess(Model model, HttpServletRequest req) {

		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		
		
		FormTransferValidationPage formTransferValidationPage = new FormTransferValidationPage();
		formTransferValidationPage.setNoRek(formTransferPage.getNoRek());
		formTransferValidationPage.setKeterangan(formTransferPage.getKeterangan());
		formTransferValidationPage.setNominal(formTransferPage.getNominal());
		formTransferValidationPage.setNoRekTujuan(formTransferPage.getNoRekTujuan());
		
		formTransferValidationPage.setJnsTransaksi("Transfer");
		
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		formTransferValidationPage.setTglTransaksi(date);
		
		
		TbRekening tbRekening = this.repoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setNamaPenerima(tbRekening.getTbUsers().getNama());
		
		model.addAttribute("formTransferValidationPage", formTransferValidationPage);
		
		return "form-transfer-success";
	}
	
	
	
	
	
}
