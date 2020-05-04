package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.bca.minibank.form.FormMutasi;
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
	
		return "Transfer-1";
	}
	
	
	@PostMapping("/transfer")
	public String postTransfer(@Valid FormTransferPage formTransferPage, BindingResult result, Model model, HttpServletRequest req) {
		
		System.out.println(formTransferPage.getNoRekTujuan());
		System.out.println("hayoo");
		
//		validasi no rek tujuan
		TbRekening NoRekTujuanExist = this.repoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
        if((!formTransferPage.getNoRekTujuan().equals("")) && (null == NoRekTujuanExist)) {
        	System.out.println("maaf, no rek yg d tuju salah");
        	result.rejectValue("noRekTujuan", "error.formTransferPage", "maaf, no rek yg d tuju salah");
        	return "Transfer-1";
        }
        
		if(result.hasErrors()) {
			return "Transfer-1";
		}

        int nominal = Integer.parseInt(formTransferPage.getNominal());
        
        
//      2. validasi limit transaksi berdasarkan sisa limit harian
       TbRekening cekSaldo = this.repoTbRekening.findByNoRek(formTransferPage.getNoRek());
       System.out.println("batas limit kamu");
       System.out.println(cekSaldo.getTbJnsTab().getLimitTransaksi());
       if(cekSaldo.getTransaksiHarian() + nominal > cekSaldo.getTbJnsTab().getLimitTransaksi()) {
       	System.out.println("maaf, sisa limit transfer harian kamu adalah " + (cekSaldo.getTbJnsTab().getLimitTransaksi() - cekSaldo.getTransaksiHarian()));
       	result.rejectValue("nominal", "error.formTransferPage", "maaf, sisa limit transfer harian kamu adalah " + (cekSaldo.getTbJnsTab().getLimitTransaksi() - cekSaldo.getTransaksiHarian()));
       	return "Transfer-1";
       }
       
//     1. limit transfer berdasarkan limit transaksi jenis tabungan    
//       if(formTransferPage.getNominal() > cekSaldo.getTbJnsTab().getLimitTransaksi()) {
//       	System.out.println("maaf, batas nominal transaksi kamu "+ cekSaldo.getTbJnsTab().getLimitTransaksi());
//       	return "form-transfer";
//       }
        
        
//     2. validasi limit transaksi harian
//       System.out.println("batas limit kamu");
//       System.out.println(cekSaldo.getTbJnsTab().getLimitTransaksi());
//       if(cekSaldo.getTransaksiHarian() >= cekSaldo.getTbJnsTab().getLimitTransaksi()) {
//       	System.out.println("maaf, anda sudah melebihi batas limit harian transfer");
//       	return "form-transfer";
//       }
        
//      3. validasi saldo tidak mencukupi
        System.out.println("saldoku");
        System.out.println(cekSaldo.getSaldo());
        if(nominal > cekSaldo.getSaldo()) {
        	System.out.println("saldo tidak mencukupi");
        	result.rejectValue("nominal", "error.formTransferPage", "Maaf, saldo tidak mencukupi");
        	return "Transfer-1";
        }
        

  
        
        
//        TbTransaksi tbTransaksi = this.repoTbTransaksi.findByNoRek(formTransferPage.getNoRekTujuan());
//        System.out.println(tbTransaksi.getJnsTransaksi());
//        
//        
//        model.addAttribute("data", this.repoTbTransaksi.findByNoRek(tbTransaksi.getNoRek));
        
    
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = new ModelTransferPage(formTransferPage);
		modelSession.setModelTransferPage(modelTransferPage);
		
	
		return "redirect:/transferValidation";
	}
	
	@GetMapping("/transferValidation")
	public String getTransferValidation(Model model, HttpServletRequest req) {
		
	
		

		
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
//		jika null direct ke home (refresh halaman direct ke transfer)
		if(null == modelTransferPage) {
			return "redirect:/transfer";
		}
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		
		
		FormTransferValidationPage formTransferValidationPage = new FormTransferValidationPage();
		formTransferValidationPage.setNoRek(formTransferPage.getNoRek());
		formTransferValidationPage.setKeterangan(formTransferPage.getKeterangan());
		formTransferValidationPage.setNominal(formTransferPage.getNominal());
		formTransferValidationPage.setNoRekTujuan(formTransferPage.getNoRekTujuan());
		
		formTransferValidationPage.setJnsTransaksi("Transfer");
		
//		menampilkan tanggal
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		formTransferValidationPage.setTglTransaksi(date);
		
		TbRekening tbRekening = this.repoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setNamaPenerima(tbRekening.getTbUsers().getNama());
		
		model.addAttribute("formTransferValidationPage", formTransferValidationPage);
		
		
		
		return "Transfer-2";
	}
	
	@PostMapping("/transferValidation")
	public String postTransferValidation(@Valid FormTransferValidationPage formTransferValidationPage, BindingResult result, Model model, HttpServletRequest req) {
		if(result.hasErrors()) {
			return "Transfer-2";
		}
		
//		MEMBUAT SECURITY JIKA NO REK TUJUAN DI GANTI VIA HTML
		
//		SET PENGIRIM
		TbRekening rekPengirim = this.repoTbRekening.findByNoRek(formTransferValidationPage.getNoRek()); //get data norek untuk di set pada table transaksi 
		System.out.println(formTransferValidationPage.getPin());

		
//		Validasi pin pengirim
		if(!formTransferValidationPage.getPin().equals(rekPengirim.getPin())) {
			System.out.println("maaf pin yang kamu masukan salah");
			result.rejectValue("pin", "error.formTransferPage", "Maaf, pin yang kamu masukan salah");
			return "Transfer-2";
		}

//		SET PENERIMA
		TbRekening rekPenerima = this.repoTbRekening.findByNoRek(formTransferValidationPage.getNoRekTujuan());
		
//		convert nominal to integer
		int nominal = Integer.parseInt(formTransferValidationPage.getNominal());
		
//		insert to table transaksi
		TbTransaksi tbTransaksi = new TbTransaksi();
		tbTransaksi.setJnsTransaksi(formTransferValidationPage.getJnsTransaksi());
		tbTransaksi.setNominal(nominal);
		tbTransaksi.setNoRekTujuan(formTransferValidationPage.getNoRekTujuan());
		tbTransaksi.setStatusTransaksi("SUCCESS");
		tbTransaksi.setNote(formTransferValidationPage.getKeterangan());
		tbTransaksi.setTglTransaksi(new Timestamp(System.currentTimeMillis()));

		tbTransaksi.setTbRekening(rekPengirim);
		this.repoTbTransaksi.save(tbTransaksi);
		
////		set tmp saldo pengirim for mutasi
//		double tmpSaldoPengirim = rekPengirim.getSaldo();
//		
////		mengurangi saldo pengirim
//		rekPengirim.setSaldo(rekPengirim.getSaldo() - nominal);		
//		
////		menambah nilai saldo limit harian pengirim
//		rekPengirim.setTransaksiHarian(rekPengirim.getTransaksiHarian() + nominal);
//		this.repoTbRekening.save(rekPengirim);
//		
////		menambah saldo penerima
//		TbRekening rekPenerima = this.repoTbRekening.findByNoRek(formTransferValidationPage.getNoRekTujuan());
////		set tmp saldo penerima for mutasi
//		double tmpSaldoPenerima = rekPenerima.getSaldo();
//		rekPenerima.setSaldo(rekPenerima.getSaldo() + nominal);
//		this.repoTbRekening.save(rekPenerima);
		
		
//		meanmbah ke tabel mutasi [pengirim]
		TbMutasi mutasiPengirim = new TbMutasi();
		mutasiPengirim.setJnsMutasi("UANG KELUAR");
		mutasiPengirim.setSaldoAkhir(rekPengirim.getSaldo() - nominal);
		mutasiPengirim.setTglMutasi(new Timestamp(System.currentTimeMillis()));
		mutasiPengirim.setNoRek(rekPengirim.getNoRek());
		mutasiPengirim.setTbTransaksi(tbTransaksi);

		
//		meanmbah ke tabel mutasi [penerima]
		TbMutasi mutasiPenerima = new TbMutasi();
		mutasiPenerima.setJnsMutasi("UANG MASUK");
		mutasiPenerima.setSaldoAkhir(rekPenerima.getSaldo() + nominal);
		mutasiPenerima.setTglMutasi(new Timestamp(System.currentTimeMillis()));
		mutasiPenerima.setNoRek(formTransferValidationPage.getNoRekTujuan());
		mutasiPenerima.setTbTransaksi(tbTransaksi);
		
//		insert to table mutasi
		this.repoTbMutasi.save(mutasiPengirim);
		this.repoTbMutasi.save(mutasiPenerima);
		
		
//		UPDATE TABEL REKENING PENGIRIM
//		mengurangi saldo pengirim
		rekPengirim.setSaldo(rekPengirim.getSaldo() - nominal);		
//		menambah nilai saldo limit harian pengirim
		rekPengirim.setTransaksiHarian(rekPengirim.getTransaksiHarian() + nominal);
		this.repoTbRekening.save(rekPengirim);

//		UPDATE TABEL REKENING PENERIMA
//		menambah saldo penerima
		rekPenerima.setSaldo(rekPenerima.getSaldo() + nominal);
		this.repoTbRekening.save(rekPenerima);
		
		
		
		return "redirect:/transferSuccess";
	}
	
	@GetMapping("/transferSuccess")
	public String getTransferSuccess(Model model, HttpServletRequest req) {

		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
//		jika null direct ke home (refresh halaman direct ke transfer)
		if(null == modelTransferPage) {
			return "redirect:/transfer";
		}
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
		
//		hapus data variabel session
		UtilsSession.removeModelInfo(req);
		
		return "Transfer-3";
	}
	
//	============================================END TRANSFER=========================================
	
	@GetMapping("/cekmutasi")
	public String getAllMutasi(Model model) {
		model.addAttribute("mutasi", this.repoTbMutasi.findAll());
		System.out.println(this.repoTbMutasi.findAll());
		return "list-mutasi";
	}
	
	
	@GetMapping("/mutasi")
	public String getMutasi(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		FormMutasi formMutasi = new FormMutasi();
		formMutasi.setNoRek(tbUsers.getTbRekening().getNoRek());
		model.addAttribute("formMutasi", formMutasi);
		return "form-mutasi";
	}
	
	@PostMapping("/mutasi")
	public String postMutasi(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if(result.hasErrors()) {
			return "Transfer-1";
		}
		
		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		
//		===CONFIGURE FORMAT TIMESTAMP===
//		TbTransaksi tbTransaksi = this.repoTbTransaksi.getOne(77);
//		System.out.println(tbTransaksi.getTglTransaksi());	
//		String pattern = "dd-MM-yyyy HH:mm:ss";
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//		String date = simpleDateFormat.format(tbTransaksi.getTglTransaksi());
//		System.out.println(date);
//		===END CONFIGURE FORMAT TIMESTAMP===
		
//      ====JIKA MENGGUNAKAN TIMESTAMP====
//        Timestamp a = new Timestamp(cal.getTimeInMillis());
//        Timestamp b = new Timestamp(System.currentTimeMillis());
//		  model.addAttribute("mutasi", this.repoTbMutasi.findByPeriode(a,b));
//		  model.addAttribute("mutasi", this.repoTbMutasi.findBySemua(formMutasi.getNoRek()));
//      ====END JIKA MENGGUNAKAN TIMESTAMP====  
		
//		SET CELENDER
		Calendar cal = Calendar.getInstance(); 
//      Timestamp a = new Timestamp(cal.getTimeInMillis());
      
		String pattern = "dd-MMM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		System.out.println("coba - "+date);
		System.out.println(new Date());
      
      
        if(formMutasi.getPeriode().equals("sehari")) {
        	System.out.println("masuk sehari");
            model.addAttribute("mutasi", this.repoTbMutasi.findByPeriodeDay(date));
        }else if(formMutasi.getPeriode().equals("seminggu")) {
        	System.out.println("masuk seminggu");
        	cal.add(Calendar.DAY_OF_MONTH, -7);
            System.out.println(cal.getTime());
            model.addAttribute("mutasi", this.repoTbMutasi.findByPeriode(cal.getTime(), new Date()));
        }else if(formMutasi.getPeriode().equals("sebulan")) {
        	System.out.println("masuk sebulan");
        	cal.add(Calendar.DAY_OF_MONTH, -30);
            System.out.println(cal.getTime());
            model.addAttribute("mutasi", this.repoTbMutasi.findByPeriode(cal.getTime(), new Date()));
        } 
       
		return "list-mutasi";
	}
	
	
	
	
	
	
	
	
	
	
	
}
