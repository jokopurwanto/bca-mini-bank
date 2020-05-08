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
import java.util.Random;

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

import com.bca.minibank.dao.DaoTbSetting;
import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormMasukanPin;
import com.bca.minibank.form.FormMutasi;
import com.bca.minibank.form.FormTransferPage;
import com.bca.minibank.form.FormTransferValidationPage;
import com.bca.minibank.mail.ContentEmailWestBankPKWT;
import com.bca.minibank.mail.SendEmailSMTP;
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
	
	
	@Autowired
	private DaoTbSetting daoSetting;

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

	@GetMapping("/nasabah/transfer")
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

	@PostMapping("/nasabah/transfer")
	public String postTransfer(@Valid FormTransferPage formTransferPage, BindingResult result, Model model,
			HttpServletRequest req) {

		System.out.println(formTransferPage.getNoRekTujuan());
		System.out.println("hayoo");

//		validasi no rek tujuan
		TbRekening NoRekTujuanExist = this.repoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		if ((!formTransferPage.getNoRekTujuan().equals("")) && (null == NoRekTujuanExist)) {
			System.out.println("maaf, no rek yg d tuju salah");
			result.rejectValue("noRekTujuan", "error.formTransferPage", "maaf, no rek yg d tuju salah");
			return "Transfer-1";
		}

		if (result.hasErrors()) {
			return "Transfer-1";
		}

		int nominal = Integer.parseInt(formTransferPage.getNominal());

//      2. validasi limit transaksi berdasarkan sisa limit harian
		TbRekening cekSaldo = this.repoTbRekening.findByNoRek(formTransferPage.getNoRek());
		System.out.println("batas limit kamu");
		System.out.println(cekSaldo.getTbJnsTab().getLimitTransaksi());
		if (cekSaldo.getTransaksiHarian() + nominal > cekSaldo.getTbJnsTab().getLimitTransaksi()) {
			System.out.println("maaf, sisa limit transfer harian kamu adalah "
					+ (cekSaldo.getTbJnsTab().getLimitTransaksi() - cekSaldo.getTransaksiHarian()));
			result.rejectValue("nominal", "error.formTransferPage", "maaf, sisa limit transfer harian kamu adalah "
					+ (cekSaldo.getTbJnsTab().getLimitTransaksi() - cekSaldo.getTransaksiHarian()));
			return "Transfer-1";
		}

//      3. validasi saldo tidak mencukupi
		System.out.println("saldoku");
		System.out.println(cekSaldo.getSaldo());
		if (nominal > cekSaldo.getSaldo()) {
			System.out.println("saldo tidak mencukupi");
			result.rejectValue("nominal", "error.formTransferPage", "Maaf, saldo tidak mencukupi");
			return "Transfer-1";
		}

		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = new ModelTransferPage(formTransferPage);
		modelSession.setModelTransferPage(modelTransferPage);

		return "redirect:/nasabah/transfer/konfirmasi";
	}

	@GetMapping("/nasabah/transfer/konfirmasi")
	public String getTransferValidation(Model model, HttpServletRequest req) {

		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
//		jika null direct ke home (refresh halaman direct ke transfer)
		if (null == modelTransferPage) {
			return "redirect:/nasabah/transfer";
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

	@PostMapping("/nasabah/transfer/konfirmasi")
	public String postTransferValidation(@Valid FormTransferValidationPage formTransferValidationPage,
			BindingResult result, Model model, HttpServletRequest req) {
		if (result.hasErrors()) {
			return "Transfer-2";
		}

//		SET PENGIRIM
		TbRekening rekPengirim = this.repoTbRekening.findByNoRek(formTransferValidationPage.getNoRek()); 
		System.out.println(formTransferValidationPage.getPin());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(formTransferValidationPage.getPin(), tbUsers.getTbRekening().getPin())) {		
			String codes = (String) req.getSession().getAttribute("code");
			if(codes == null) {
				req.getSession().setAttribute("code", "1");
			}else {
				int code = Integer.parseInt(codes);
				code++;
				req.getSession().setAttribute("code", String.valueOf(code));	
				if(code == 3) {
					TbRekening tbRekening = this.repoTbRekening.findByNoRek(tbUsers.getTbRekening().getNoRek());
					tbRekening.setStatusRek("NOT ACTIVE");
					tbUsers.setStatusUser("BLOCK");
					tbUsers.setKeterangan("Akun anda terblokir dikarenakan salah password atau salah pin sebanyak 3x berturut-turut");
					this.repoTbRekening.save(tbRekening);
					this.repoTbUsers.save(tbUsers);
					return "redirect:/logout";
				}
			}
			
			int limitAttempt = 3 - Integer.parseInt((String) req.getSession().getAttribute("code"));
			result.rejectValue("pin", "error.formTransferPage", "Maaf, pin anda salah, tersisa "+limitAttempt+"x percobaan");
			
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

		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentSuccessSendTransfer(201, new Date(), "TRANSFER", "123123123", "123123124", "Andi mulyadi", "Uang Kostan Bulan Mei", 450000);
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				"happinessofmoonlight@gmail.com", //email penerima
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		
		sendEmailSMTP.sendEmail();
		
		contentEmail.getContentSuccessReceiveTransfer(201, new Date(), "TRANSFER", "123123123", "Ana nadiani", "Uang Kostan Bulan Mei", 450000);
		SendEmailSMTP receiveEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				"joko_purwanto62@yahoo.com", //email penerima
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		
		receiveEmailSMTP.sendEmail();
		
		return "redirect:/nasabah/transfer/sukses";
	}

	@GetMapping("/nasabah/transfer/sukses")
	public String getTransferSuccess(Model model, HttpServletRequest req) {
		//reset null percobaan pin
		req.getSession().setAttribute("code", null);	
		
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
//		jika null direct ke transfer (refresh halaman direct ke transfer)
		if (null == modelTransferPage) {
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

	@GetMapping("/nasabah/mutasi")
	public String getMutasi1(Model model, HttpSession session, HttpServletRequest req) {
		if ((Boolean) session.getAttribute("pintervalidasi") == null
				|| (Boolean) session.getAttribute("pintervalidasi") == false) {
			req.getSession().setAttribute("url", "redirect:/nasabah/mutasi");
			return "redirect:/nasabah/pin";
		} else {
			req.getSession().setAttribute("pintervalidasi", false);
			req.getSession().setAttribute("pinattempt", 0);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());

			FormMutasi formMutasi = new FormMutasi();
			formMutasi.setNoRek(tbUsers.getTbRekening().getNoRek());
			formMutasi.setStatusJangkaWaktu("jangkawaktu");
			formMutasi.setStatusPeriode("periode");
			model.addAttribute("formMutasi", formMutasi);

//			return "form-transaksi";
			return "CekMutasi-1";
		}
	}

	@GetMapping("/nasabah/mutasi/jangkawaktu")
	public String getMutasi12() {
		return "redirect:/nasabah/mutasi";
	}
	
	@PostMapping("/nasabah/mutasi/jangkawaktu")
	public String postTransaksi(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "CekMutasi-1";
		}

		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		System.out.println(formMutasi.getJnsMutasi());
		System.out.println(formMutasi.getStartDate());
		System.out.println(formMutasi.getEndDate());
		System.out.println(formMutasi.getStatusJangkaWaktu());
		System.out.println(formMutasi.getStatusPeriode());

		if ((null == formMutasi.getStartDate()) || (null == formMutasi.getEndDate())) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal harus disi semua");
			return "CekMutasi-1";
		}

		int compareStartDate = new Date().compareTo(formMutasi.getStartDate());	
		int compareEndDate = new Date().compareTo(formMutasi.getEndDate());
		int compareRangeDate = formMutasi.getEndDate().compareTo(formMutasi.getStartDate());		
		
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(formMutasi.getStartDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(formMutasi.getEndDate());
		
		String hasil = String.valueOf(daysBetween(cal1, cal2));
		int rangeDate = (int)daysBetween(cal1, cal2);
		System.out.println("hasil compare "+ compareStartDate+ " Selisih nya = "+rangeDate);
		if(compareStartDate < 0){
			System.out.println("Tanggal awal lebih besar dari tanggal hari ini");
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal awal lebih besar dari tanggal hari ini");
			return "CekMutasi-1";
		}else if(compareEndDate < 0){
			System.out.println("Tanggal akhir lebih besar dari tanggal hari ini");
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal akhir lebih besar dari tanggal hari ini");
			return "CekMutasi-1";		
		}else if(compareRangeDate < 0) {
			System.out.println("Tanggal awal lebih besar dari tanggal alkhir");
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal awal lebih besar dari tanggal alkhir");
			return "CekMutasi-1";
		}else if(rangeDate > 30) {
			System.out.println("Tanggal awal lebih besar dari tanggal alkhir");
			result.rejectValue("endDate", "error.formMutasi", "Maaf, periode mutasi yang dapat dipilih 30 hari");
			return "CekMutasi-1";
		}
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());

//			SET CELENDER
		Calendar cal = Calendar.getInstance();
//	      Timestamp a = new Timestamp(cal.getTimeInMillis());

		String pattern = "dd-MMM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String startDateUI = simpleDateFormat.format(formMutasi.getStartDate());
		String endDateUI = simpleDateFormat.format(formMutasi.getEndDate());
		System.out.println("startDateUI - " + startDateUI);
		System.out.println("endDateUI - " + endDateUI);

		List queryOut = this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDateUI,
				endDateUI);
		List queryIn = this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDateUI,
				endDateUI);
		queryOut.addAll(queryIn);
		System.out.println(queryOut);

		if (formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("if semua");
			model.addAttribute("mutasi", queryOut);
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk")) {
			System.out.println("if uang masuk");
			model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(),
					startDateUI, endDateUI));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar")) {
			System.out.println("if uang keluar");
			model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(),
					startDateUI, endDateUI));
		} else {
			result.rejectValue("jnsMutasi", "error.formMutasi", "Maaf, jenis transaksi yang kamu masukan salah");
			return "CekMutasi-1";
		}

		return "CekMutasi-2";

	}

	@GetMapping("/nasabah/mutasi/periode")
	public String getMutasi11() {
		return "redirect:/nasabah/mutasi";
	}

	@PostMapping("/nasabah/mutasi/periode")
	public String postMutasiPeriode(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "CekMutasi-1";
		}

		if (null == formMutasi) {
			return "redirect:/transfer";
		}

		System.out.println(formMutasi.getNoRek());
		System.out.println(formMutasi.getPeriode());
		System.out.println(formMutasi.getJnsMutasi());
		System.out.println(formMutasi.getStartDate());
		System.out.println(formMutasi.getEndDate());

		if (null == formMutasi.getPeriode()) {
			result.rejectValue("periode", "error.formMutasi", "Maaf, periode tidak boleh kosong");
			return "CekMutasi-1";
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
		System.out.println("coba - " + endDate);
		System.out.println(new Date());

		if (formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("jns semua");
			if (formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
				cal.add(Calendar.DAY_OF_MONTH, 0);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk sehari");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else if (formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
				cal.add(Calendar.DAY_OF_MONTH, -7);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk seminggu");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else if (formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
				cal.add(Calendar.DAY_OF_MONTH, -30);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk sebulan");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else {
				result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
				return "CekMutasi-1";
			}
			List queryOut = this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDate,
					endDate);
			List queryIn = this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDate,
					endDate);
			queryOut.addAll(queryIn);
			model.addAttribute("mutasi",
					this.repoTbMutasi.findByAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk")) {
			System.out.println("jns masuk");
			if (formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
				cal.add(Calendar.DAY_OF_MONTH, 0);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk sehari");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else if (formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
				cal.add(Calendar.DAY_OF_MONTH, -7);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk seminggu");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else if (formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
				cal.add(Calendar.DAY_OF_MONTH, -30);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk sebulan");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else {
				result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
				return "CekMutasi-1";
			}
			model.addAttribute("mutasi",
					this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar")) {
			System.out.println("jns keluar");
			if (formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
				cal.add(Calendar.DAY_OF_MONTH, 0);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk sehari");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else if (formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
				cal.add(Calendar.DAY_OF_MONTH, -7);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk seminggu");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else if (formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
				cal.add(Calendar.DAY_OF_MONTH, -30);
				startDate = simpleDateFormat.format(cal.getTime());
				System.out.println("masuk sebulan");
				System.out.println("start date = " + startDate);
				System.out.println("end date = " + endDate);
			} else {
				result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
				return "CekMutasi-1";
			}
			model.addAttribute("mutasi",
					this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		} else {
			result.rejectValue("jnsMutasi", "error.formMutasi", "Maaf, jenis transaksi yang kamu masukan salah");
			return "CekMutasi-1";
		}

		return "CekMutasi-2";
	}

	@PostMapping("/transaksi2")
	public String postTransaksi2(@Valid FormMutasi formMutasi, BindingResult result, Model model) {

		if (null == formMutasi.getPeriode()) {

		}

		if (result.hasErrors()) {
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
		System.out.println("startDateUI - " + startDateUI);
		System.out.println("endDateUI - " + endDateUI);

		List queryOut = this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), startDateUI,
				endDateUI);
		List queryIn = this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), startDateUI,
				endDateUI);
		queryOut.addAll(queryIn);
		System.out.println(queryOut);

		if (formMutasi.getJnsMutasi().equalsIgnoreCase("Semua")) {
			System.out.println("if semua");
			model.addAttribute("mutasi", queryOut);
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase("uang masuk")) {
			System.out.println("if uang masuk");
			model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(),
					startDateUI, endDateUI));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase("uang keluar")) {
			System.out.println("if uang keluar");
			model.addAttribute("mutasi", this.repoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(),
					startDateUI, endDateUI));
		} else {
			System.out.println("reject value");
		}

		return "CekMutasi-2";

	}

	@GetMapping("/nasabah/pin")
	public String pinRequestPage(Model model, FormMasukanPin formMasukanPin) {
		return "pinrequest";
	}

	@PostMapping("/nasabah/pin")
	public String cekSaldoPinRequestPost(Model model, @Valid FormMasukanPin formMasukanPin, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean flagPin = false;
		boolean flagBlock = false;
		TbUsers tbUsers = this.repoTbUsers.findByUsername(auth.getName());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println("joko " + formMasukanPin.getPin());
		System.out.println(tbUsers.getTbRekening().getPin());

		if (!encoder.matches(formMasukanPin.getPin(), tbUsers.getTbRekening().getPin())) {
			System.out.println("beda");
			int pinattempt = (Integer) session.getAttribute("pinattempt");
			pinattempt++;
			request.getSession().setAttribute("pinattempt", pinattempt);
			flagPin = true;
			if (pinattempt >= 3) {
				flagBlock = true;
				TbRekening tbRekening = this.repoTbRekening.findByNoRek(tbUsers.getTbRekening().getNoRek());
				tbRekening.setStatusRek("NOT ACTIVE");
				tbUsers.setStatusUser("BLOCK");
				tbUsers.setKeterangan("Akun anda terblokir dikarenakan salah password atau salah pin sebanyak 3x berturut-turut");
				this.repoTbRekening.save(tbRekening);
				this.repoTbUsers.save(tbUsers);
				return "redirect:/logout";
			}
		}

		if (bindingResult.hasErrors() || flagPin == true || flagBlock == true) {
			model.addAttribute("flagPin", flagPin);
			model.addAttribute("flagBlock", flagBlock);
			model.addAttribute("pinattempt", session.getAttribute("pinattempt"));
			return "pinrequest";
		} else {
			System.out.println("sama");
			request.getSession().setAttribute("pintervalidasi", true);
			return (String) session.getAttribute("url");
		}
	}
	
	
	private static long daysBetween(Calendar tanggalAwal, Calendar tanggalAkhir) {
        long lama = 0;
        Calendar tanggal = (Calendar) tanggalAwal.clone();
        while (tanggal.before(tanggalAkhir)) {
            tanggal.add(Calendar.DAY_OF_MONTH, 1);
            lama++;
        }
        return lama;
    }
	

}
