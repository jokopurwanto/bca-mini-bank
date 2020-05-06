package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormMasukanPin;
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
		
		
		 try {   
	            Properties properties=new Properties();
	            properties.put("mail.smtps.host","smtp.gmail.com");
	            properties.put("mail.smtps.auth","true");
	            properties.put("mail.smtps.ssl.enable","true");
	            properties.put("mail.smtps.port", "465");//default port dari smptp
	             
	            Session session=Session.getInstance(properties);
	            session.setDebug(true);
	             
	            MimeMessage pesan=new MimeMessage(session);
	            pesan.setFrom("westbankPKWT@gmail.com");//isi dengan gmail kalian sendiri, biasanya sama nanti dengan username
	            pesan.setRecipient(Message.RecipientType.TO, new InternetAddress("jokopurwanto1996@gmail.com"));//isi dengan tujuan email
	            pesan.setSubject("Transfer Transaction Journal");
	            pesan.setText("Transfer success");
	             
	            String username="westbankPKWT@gmail.com";//isi dengan gmail kalian sendiri
	            String password="westbankPKWT03";//isi dengan password sendiri
	             
	            Transport transport = session.getTransport("smtps");
	            transport.connect(username, password);
	            transport.sendMessage(pesan, pesan.getAllRecipients());
	            transport.close();
	        } catch (MessagingException ex) {
	            ex.printStackTrace();
	        }
		
		
		
		return "redirect:/transferSuccess";
	}
	
	@GetMapping("/transferSuccess")
	public String getTransferSuccess(Model model, HttpServletRequest req) {

		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
//		jika null direct ke transfer (refresh halaman direct ke transfer)
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
			return "form-mutasi";
		}
		
		if(null == formMutasi.getPeriode()) {
			return "form-mutasi";
		}
		
		

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		System.out.println(formMutasi.getJnsMutasi());
		System.out.println(formMutasi.getStartDate());
		System.out.println(formMutasi.getEndDate());
		
		
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
		String endDate = simpleDateFormat.format(new Date());
		System.out.println("coba - "+endDate);
		System.out.println(new Date());
		
//		String startDateUI = simpleDateFormat.format(formMutasi.getStartDate());
//		String endDateUI = simpleDateFormat.format(formMutasi.getEndDate());	
//		System.out.println("startDateUI - "+startDateUI);
//		System.out.println("endDateUI - "+endDateUI);		
		
		if(formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("semua fix");
	        if(formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
	        	cal.add(Calendar.DAY_OF_MONTH, 0);
	        	String startDate = simpleDateFormat.format(cal.getTime());
	        	System.out.println("masuk sehari");
	        	System.out.println("start date = "+startDate);
	            System.out.println("end date = "+endDate);
	            model.addAttribute("mutasi", this.repoTbMutasi.findByAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
	        }else if(formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
	        	cal.add(Calendar.DAY_OF_MONTH, -7);
	            String startDate = simpleDateFormat.format(cal.getTime());
	        	System.out.println("masuk seminggu");
	            System.out.println("start date = "+startDate);
	            System.out.println("end date = "+endDate);
	            model.addAttribute("mutasi", this.repoTbMutasi.findByAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
	        }else if(formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
	        	cal.add(Calendar.DAY_OF_MONTH, -30);
	            String startDate = simpleDateFormat.format(cal.getTime());
	        	System.out.println("masuk sebulan");
	            System.out.println("start date = "+startDate);
	            System.out.println("end date = "+endDate);
	            model.addAttribute("mutasi", this.repoTbMutasi.findByAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
	        }else {
	        	result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
	        	return "form-mutasi";
	        }	
		}else if(formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk") || (formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar"))) {
			System.out.println("masuk fix");
			 if(formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
		        	cal.add(Calendar.DAY_OF_MONTH, 0);
		        	String startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk sehari");
		        	System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		            model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksi(tbUsers.getTbRekening().getNoRek(),formMutasi.getJnsMutasi().toUpperCase(), startDate, endDate));
		        }else if(formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
		        	cal.add(Calendar.DAY_OF_MONTH, -7);
		            String startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk seminggu");
		            System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		            model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksi(tbUsers.getTbRekening().getNoRek(),formMutasi.getJnsMutasi().toUpperCase(), startDate, endDate));
		        }else if(formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
		        	cal.add(Calendar.DAY_OF_MONTH, -30);
		            String startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk sebulan");
		            System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		            model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksi(tbUsers.getTbRekening().getNoRek(),formMutasi.getJnsMutasi().toUpperCase(), startDate, endDate));
		        }else {
		        	result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
		        	return "form-mutasi";
		        }	
		}else {
			System.out.println("reject value");
		}
		
      
		
		
      
//        if(formMutasi.getPeriode().equals("sehari")) {
//        	cal.add(Calendar.DAY_OF_MONTH, 0);
//        	String startDate = simpleDateFormat.format(cal.getTime());
//        	System.out.println("masuk sehari");
//        	System.out.println("start date = "+startDate);
//            System.out.println("end date = "+endDate);
//            model.addAttribute("mutasi", this.repoTbMutasi.findByPeriode(tbUsers.getTbRekening().getNoRek(), "UANG MASUK", startDate, endDate));
//        }else if(formMutasi.getPeriode().equals("seminggu")) {
//        	cal.add(Calendar.DAY_OF_MONTH, -7);
//            String startDate = simpleDateFormat.format(cal.getTime());
//        	System.out.println("masuk seminggu");
//            System.out.println("start date = "+startDate);
//            System.out.println("end date = "+endDate);
//            model.addAttribute("mutasi", this.repoTbMutasi.findByPeriode(tbUsers.getTbRekening().getNoRek(), "UANG KELUAR", startDate, endDate));
//        }else if(formMutasi.getPeriode().equals("sebulan")) {
//        	cal.add(Calendar.DAY_OF_MONTH, -30);
//            String startDate = simpleDateFormat.format(cal.getTime());
//        	System.out.println("masuk sebulan");
//            System.out.println("start date = "+startDate);
//            System.out.println("end date = "+endDate);
//            model.addAttribute("mutasi", this.repoTbMutasi.findByPeriode(tbUsers.getTbRekening().getNoRek(), "UANG MASUK", startDate, endDate));
//        }else {
//        	result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
//        	return "form-mutasi";
//        }
       
		return "list-mutasi";
	}
	
	
//	@GetMapping("/mutasi1")
//	public String getMutasi1(Model model) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
//		
//		FormMutasi formMutasi = new FormMutasi();
//		formMutasi.setNoRek(tbUsers.getTbRekening().getNoRek());
//		model.addAttribute("formMutasi", formMutasi);
//		return "form-mutasi1";
//	}
	
	
	
	@PostMapping("/mutasi1")
	public String postMutasi1(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if(result.hasErrors()) {
			return "form-mutasi1";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		System.out.println(formMutasi.getJnsMutasi());
		System.out.println(formMutasi.getStartDate());
		System.out.println(formMutasi.getEndDate());
		
		
//		SET CELENDER
		Calendar cal = Calendar.getInstance(); 
//      Timestamp a = new Timestamp(cal.getTimeInMillis());
      
		String pattern = "dd-MMM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);		
		String startDateUI = simpleDateFormat.format(formMutasi.getStartDate());
		String endDateUI = simpleDateFormat.format(formMutasi.getEndDate());	
		System.out.println("startDateUI - "+startDateUI);
		System.out.println("endDateUI - "+endDateUI);		
		
		

		if(formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("semua fix");
	        model.addAttribute("mutasi", this.repoTbMutasi.findByAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDateUI, endDateUI));
		}else if(formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk") || (formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar"))) {
			System.out.println("masuk fix");
			model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksi(tbUsers.getTbRekening().getNoRek(),formMutasi.getJnsMutasi().toUpperCase(), startDateUI, endDateUI));
		}else {
			System.out.println("reject value");
		}
		
		return "list-mutasi";
		
	}
	
	
	@GetMapping("/transaksi")
	public String getMutasi1(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		FormMutasi formMutasi = new FormMutasi();
		formMutasi.setNoRek(tbUsers.getTbRekening().getNoRek());
		model.addAttribute("formMutasi", formMutasi);
		return "form-transaksi";
	}
	
	@PostMapping("/transaksi")
	public String postTransaksi(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		
		
		
		
		if(result.hasErrors()) {
			return "form-transaksi";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		System.out.println(formMutasi.getJnsMutasi());
		System.out.println(formMutasi.getStartDate());
		System.out.println(formMutasi.getEndDate());
		
		
//		SET CELENDER
		Calendar cal = Calendar.getInstance(); 
//      Timestamp a = new Timestamp(cal.getTimeInMillis());
      
		String pattern = "dd-MMM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);		
		String startDateUI = simpleDateFormat.format(formMutasi.getStartDate());
		String endDateUI = simpleDateFormat.format(formMutasi.getEndDate());	
		System.out.println("startDateUI - "+startDateUI);
		System.out.println("endDateUI - "+endDateUI);		
		
		List queryOut =  this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDateUI, endDateUI);
		List queryIn =  this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDateUI, endDateUI);
		queryOut.addAll(queryIn);
		System.out.println(queryOut);
		
		if(formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("if semua");
	        model.addAttribute("mutasi", queryOut);
		}else if(formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk")) {
			System.out.println("if uang masuk");
	        model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDateUI, endDateUI));
		}else if(formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar")) {
			System.out.println("if uang keluar");
			model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDateUI, endDateUI));
		}else {
			System.out.println("reject value");
		}
		
		return "CekMutasi3";
		
	}
	
	@PostMapping("/transaksi1")
	public String postMutasiPeriode(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if(result.hasErrors()) {
			return "form-transaksi";
		}
		
		if(null == formMutasi.getPeriode()) {
			return "form-transaksi";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		System.out.println(formMutasi.getJnsMutasi());
		System.out.println(formMutasi.getStartDate());
		System.out.println(formMutasi.getEndDate());
		
//		SET CELENDER
		Calendar cal = Calendar.getInstance(); 
      
		String pattern = "dd-MMM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String endDate = simpleDateFormat.format(new Date());
		String startDate = null;
		System.out.println("coba - "+endDate);
		System.out.println(new Date());
		
		
		
		if(formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("jns semua");
	        if(formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
	        	cal.add(Calendar.DAY_OF_MONTH, 0);
	        	startDate = simpleDateFormat.format(cal.getTime());
	        	System.out.println("masuk sehari");
	        	System.out.println("start date = "+startDate);
	            System.out.println("end date = "+endDate);
	        }else if(formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
	        	cal.add(Calendar.DAY_OF_MONTH, -7);
	            startDate = simpleDateFormat.format(cal.getTime());
	        	System.out.println("masuk seminggu");
	            System.out.println("start date = "+startDate);
	            System.out.println("end date = "+endDate);
	        }else if(formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
	        	cal.add(Calendar.DAY_OF_MONTH, -30);
	            startDate = simpleDateFormat.format(cal.getTime());
	        	System.out.println("masuk sebulan");
	            System.out.println("start date = "+startDate);
	            System.out.println("end date = "+endDate);
	        }else {
	        	result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
	        	return "form-transaksi";
	        }
    		List queryOut =  this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDate, endDate);
    		List queryIn =  this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDate, endDate);
    		queryOut.addAll(queryIn);
            model.addAttribute("mutasi", this.repoTbMutasi.findByAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		}else if(formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk")) {
			 System.out.println("jns masuk");
			 if(formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
		        	cal.add(Calendar.DAY_OF_MONTH, 0);
		        	startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk sehari");
		        	System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		        }else if(formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
		        	cal.add(Calendar.DAY_OF_MONTH, -7);
		            startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk seminggu");
		            System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		        }else if(formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
		        	cal.add(Calendar.DAY_OF_MONTH, -30);
		            startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk sebulan");
		            System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		        }else {
		        	result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
		        	return "form-transaksi";
		        }	
		        model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		}else if(formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar")) {
			 System.out.println("jns keluar");
			 if(formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
		        	cal.add(Calendar.DAY_OF_MONTH, 0);
		        	startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk sehari");
		        	System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		        }else if(formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
		        	cal.add(Calendar.DAY_OF_MONTH, -7);
		            startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk seminggu");
		            System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		        }else if(formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
		        	cal.add(Calendar.DAY_OF_MONTH, -30);
		            startDate = simpleDateFormat.format(cal.getTime());
		        	System.out.println("masuk sebulan");
		            System.out.println("start date = "+startDate);
		            System.out.println("end date = "+endDate);
		        }else {
		        	result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
		        	return "form-transaksi";
		        }	
		        model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		}else {
			System.out.println("reject value");
		}
		
		return "CekMutasi3";
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
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();  
		if(tbUsers.getTbRekening().getStatusRek().equals("NOT ACTIVE"))
		{
			flagBlock = true;
		}
		else if(!encoder.matches(formMasukanPin.getPin(), tbUsers.getTbRekening().getPin()))
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

	
	
	
	
	
	
	
}
