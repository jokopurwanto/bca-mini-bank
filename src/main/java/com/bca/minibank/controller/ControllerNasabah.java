package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import com.bca.minibank.dao.DaoTbMutasi;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbSetting;
import com.bca.minibank.dao.DaoTbTransaksi;
import com.bca.minibank.dao.DaoTbUsers;
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
import com.bca.minibank.utils.UtilsSession;

@Controller
public class ControllerNasabah {

	@Autowired
	private DaoTbUsers daoTbUsers;
	
	@Autowired
	private DaoTbRekening daoTbRekening;
	
	@Autowired
	private DaoTbTransaksi daoTbTransaksi;
	
	@Autowired
	private DaoTbMutasi daoTbMutasi;
	
	@Autowired
	private DaoTbSetting daoSetting;

	
	private static final String JNS_TRANSAKSI_TRANSFER = "TRANSFER";
	private static final String STATUS_TRANSAKSI_SUCCESS = "SUCCESS";	
	private static final String JNS_SEMUA_MUTASI = "SEMUA";
	private static final String JNS_MUTASI_MASUK = "UANG MASUK";
	private static final String JNS_MUTASI_KELUAR = "UANG KELUAR";
	private static final String PETTERN_DATE_TIME = "dd-MM-yyyy HH:mm:ss";
	private static final String PETTERN_DATE = "dd-MMM-yyyy";
	private static final String STATUS_REK_NOT_ACTIVE = "NOT ACTIVE";
	private static final String STATUS_USER_BLOCK = "BLOCK";
	private static final String KETERANGAN_BLOCK = "Akun anda terblokir dikarenakan salah password atau salah pin sebanyak 3x berturut-turut";

//	============================================START TRANSFER=========================================
	@GetMapping("/nasabah/transfer")
	public String getTransfer(Model model, HttpServletRequest req) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = daoTbUsers.findByUsername(auth.getName());
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
//		validasi no rek tujuan
		TbRekening NoRekTujuanExist = this.daoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		if ((!formTransferPage.getNoRekTujuan().equals("")) && (null == NoRekTujuanExist)) {
			result.rejectValue("noRekTujuan", "error.formTransferPage", "Maaf, no rek yg d tuju salah");
			return "Transfer-1";
		}

		if (result.hasErrors()) {
			return "Transfer-1";
		}

		int nominal = Integer.parseInt(formTransferPage.getNominal());
//      validasi limit transaksi
		TbRekening cekSaldo = this.daoTbRekening.findByNoRek(formTransferPage.getNoRek());
		if (cekSaldo.getTransaksiHarian() + nominal > cekSaldo.getTbJnsTab().getLimitTransaksi()) {
			result.rejectValue("nominal", "error.formTransferPage", "maaf, sisa limit transfer harian kamu adalah "
					+ (cekSaldo.getTbJnsTab().getLimitTransaksi() - cekSaldo.getTransaksiHarian()));
			return "Transfer-1";
		}

//      validasi saldo tidak mencukupi
		if (nominal > cekSaldo.getSaldo()) {
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
//		jika null direct ke transfer (refresh halaman direct ke transfer)
		if (null == modelTransferPage) {
			return "redirect:/nasabah/transfer";
		}
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		FormTransferValidationPage formTransferValidationPage = new FormTransferValidationPage();
		formTransferValidationPage.setNoRek(formTransferPage.getNoRek());
		formTransferValidationPage.setKeterangan(formTransferPage.getKeterangan());
		formTransferValidationPage.setNominal(formTransferPage.getNominal());
		formTransferValidationPage.setNoRekTujuan(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setJnsTransaksi(JNS_TRANSAKSI_TRANSFER);

//		menampilkan tanggal
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		formTransferValidationPage.setTglTransaksi(date);

		TbRekening rekTujuan = this.daoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setNamaPenerima(rekTujuan.getTbUsers().getNama());
		model.addAttribute("formTransferValidationPage", formTransferValidationPage);

		return "Transfer-2";
	}

	@PostMapping("/nasabah/transfer/konfirmasi")
	public String postTransferValidation(@Valid FormTransferValidationPage formTransferValidationPage,
			BindingResult result, Model model, HttpServletRequest req) {
		if (result.hasErrors()) {
			return "Transfer-2";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers userPengirim = this.daoTbUsers.findByUsername(auth.getName());
//		SET PENGIRIM
		TbRekening rekPengirim = this.daoTbRekening.findByNoRek(userPengirim.getTbRekening().getNoRek()); 
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(formTransferValidationPage.getPin(), userPengirim.getTbRekening().getPin())) {		
			String codes = (String) req.getSession().getAttribute("code");
			if(codes == null) {
				req.getSession().setAttribute("code", "1");
			}else {
				int pinAttempt = Integer.parseInt(codes);
				pinAttempt++;
				req.getSession().setAttribute("code", String.valueOf(pinAttempt));	
				if(pinAttempt >= 3) {			
					rekPengirim.setStatusRek(STATUS_REK_NOT_ACTIVE);
					userPengirim.setStatusUser(STATUS_USER_BLOCK);
					userPengirim.setKeterangan(KETERANGAN_BLOCK);
					this.daoTbRekening.update(rekPengirim);
					this.daoTbUsers.update(userPengirim);
					return "redirect:/logout";
				}
			}
			int limitAttempt = 3 - Integer.parseInt((String) req.getSession().getAttribute("code"));
			result.rejectValue("pin", "error.formTransferPage", "Maaf, pin anda salah, tersisa "+limitAttempt+"x percobaan");
			return "Transfer-2";
		}

//		SET PENERIMA
		TbRekening rekPenerima = this.daoTbRekening.findByNoRek(formTransferValidationPage.getNoRekTujuan());

//		convert nominal integer
		int nominal = Integer.parseInt(formTransferValidationPage.getNominal());

//		insert tabel transaksi
		TbTransaksi transaksi = new TbTransaksi();
		transaksi.setJnsTransaksi(formTransferValidationPage.getJnsTransaksi());
		transaksi.setNominal(nominal);
		transaksi.setNoRekTujuan(formTransferValidationPage.getNoRekTujuan());
		transaksi.setStatusTransaksi(STATUS_TRANSAKSI_SUCCESS);
		transaksi.setNote(formTransferValidationPage.getKeterangan());
		transaksi.setTglTransaksi(new Timestamp(System.currentTimeMillis()));
		transaksi.setTbRekening(rekPengirim);
		this.daoTbTransaksi.add(transaksi);
		
//		meanmbah ke tabel mutasi [pengirim]
		TbMutasi mutasiPengirim = new TbMutasi();
		mutasiPengirim.setJnsMutasi(JNS_MUTASI_KELUAR);
		mutasiPengirim.setSaldoAkhir(rekPengirim.getSaldo() - nominal);
//		mutasiPengirim.setTglMutasi(new Timestamp(System.currentTimeMillis()));
//		mutasiPengirim.setNoRek(rekPengirim.getNoRek());
		mutasiPengirim.setTbTransaksi(transaksi);

//		meanmbah ke tabel mutasi [penerima]
		TbMutasi mutasiPenerima = new TbMutasi();
		mutasiPenerima.setJnsMutasi(JNS_MUTASI_MASUK);
		mutasiPenerima.setSaldoAkhir(rekPenerima.getSaldo() + nominal);
//		mutasiPenerima.setTglMutasi(new Timestamp(System.currentTimeMillis()));
//		mutasiPenerima.setNoRek(formTransferValidationPage.getNoRekTujuan());
		mutasiPenerima.setTbTransaksi(transaksi);
		
//		insert to table mutasi
		this.daoTbMutasi.add(mutasiPengirim);
		this.daoTbMutasi.add(mutasiPenerima);

//		- saldo pengirim
		rekPengirim.setSaldo(rekPengirim.getSaldo() - nominal);
//		+ limit harian pengirim
		rekPengirim.setTransaksiHarian(rekPengirim.getTransaksiHarian() + nominal);
		this.daoTbRekening.update(rekPengirim);

//		+ saldo penerima
		rekPenerima.setSaldo(rekPenerima.getSaldo() + nominal);
		this.daoTbRekening.update(rekPenerima);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PETTERN_DATE_TIME);
		String date = simpleDateFormat.format(new Date());
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentSuccessSendTransfer(transaksi.getIdTransaksi(), 
				date, 
				JNS_TRANSAKSI_TRANSFER, 
				userPengirim.getTbRekening().getNoRek(), 
				formTransferValidationPage.getNoRekTujuan(), 
				formTransferValidationPage.getNamaPenerima(), 
				formTransferValidationPage.getKeterangan(), 
				nominal);
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				rekPengirim.getTbUsers().getEmail(), //email pengirim
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		
		contentEmail.getContentSuccessReceiveTransfer(transaksi.getIdTransaksi(), 
				date, 
				JNS_TRANSAKSI_TRANSFER, 
				userPengirim.getTbRekening().getNoRek(), 
				userPengirim.getNama(), 
				formTransferValidationPage.getKeterangan(), 
				nominal);
		SendEmailSMTP receiveEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				rekPenerima.getTbUsers().getEmail(), //email penerima
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		
		sendEmailSMTP.sendEmail();
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
			return "redirect:/nasabah/transfer";
		}
		
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		FormTransferValidationPage formTransferValidationPage = new FormTransferValidationPage();
		formTransferValidationPage.setNoRek(formTransferPage.getNoRek());
		formTransferValidationPage.setKeterangan(formTransferPage.getKeterangan());
		formTransferValidationPage.setNominal(formTransferPage.getNominal());
		formTransferValidationPage.setNoRekTujuan(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setJnsTransaksi(JNS_TRANSAKSI_TRANSFER);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PETTERN_DATE);
		String date = simpleDateFormat.format(new Date());
		formTransferValidationPage.setTglTransaksi(date);

		TbRekening tbRekening = this.daoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setNamaPenerima(tbRekening.getTbUsers().getNama());

		model.addAttribute("formTransferValidationPage", formTransferValidationPage);

//		hapus data variabel session
		UtilsSession.removeModelInfo(req);

		return "Transfer-3";
	}
//	============================================END TRANSFER=========================================

//	============================================START MUTASI=========================================
	@GetMapping("/nasabah/mutasi")
	public String getMutasi1(Model model, HttpSession session, HttpServletRequest req) {
		if ((Boolean) session.getAttribute("pinTervalidasi") == null
				|| (Boolean) session.getAttribute("pinTervalidasi") == false) {
			req.getSession().setAttribute("url", "redirect:/nasabah/mutasi");
			return "redirect:/nasabah/pin1";
		} else {
			req.getSession().setAttribute("pinTervalidasi", false);
			req.getSession().setAttribute("pinattempt", 0);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());
			FormMutasi formMutasi = new FormMutasi();
			formMutasi.setNoRek(tbUsers.getTbRekening().getNoRek());
			model.addAttribute("formMutasi", formMutasi);
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

		if ((null == formMutasi.getStartDate()) || (null == formMutasi.getEndDate())) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal harus disi semua");
			return "CekMutasi-1";
		}

//		validasi calender
		int compareStartDate = new Date().compareTo(formMutasi.getStartDate());	
		int compareEndDate = new Date().compareTo(formMutasi.getEndDate());
		int compareRangeDate = formMutasi.getEndDate().compareTo(formMutasi.getStartDate());			
        Calendar calStartDate = Calendar.getInstance();
        calStartDate.setTime(formMutasi.getStartDate());
        Calendar calEndtDate = Calendar.getInstance();
        calEndtDate.setTime(formMutasi.getEndDate());
		int rangeDate = (int)daysBetween(calStartDate, calEndtDate);
		if(compareStartDate < 0){
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal awal lebih besar dari tanggal hari ini");
			return "CekMutasi-1";
		}else if(compareEndDate < 0){
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal akhir lebih besar dari tanggal hari ini");
			return "CekMutasi-1";		
		}else if(compareRangeDate < 0) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal awal lebih besar dari tanggal alkhir");
			return "CekMutasi-1";
		}else if(rangeDate > 30) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, periode mutasi yang dapat dipilih 30 hari");
			return "CekMutasi-1";
		}
			
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PETTERN_DATE);
		String startDate = simpleDateFormat.format(formMutasi.getStartDate());
		String endDate = simpleDateFormat.format(formMutasi.getEndDate());

		if (formMutasi.getJnsMutasi().equalsIgnoreCase(JNS_SEMUA_MUTASI)) {
			model.addAttribute("mutasi", genAllTransaksi(tbUsers.getTbRekening().getNoRek(), startDate, endDate));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase(JNS_MUTASI_MASUK)) {
			model.addAttribute("mutasi", this.daoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(),
					startDate, endDate));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase(JNS_MUTASI_KELUAR)) {
			model.addAttribute("mutasi", this.daoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(),
					startDate, endDate));
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
		
//		jika null direct ke transfer
		if (null == formMutasi) {
			return "redirect:/nasabah/transfer";
		}

		if (null == formMutasi.getPeriode()) {
			result.rejectValue("periode", "error.formMutasi", "Maaf, periode tidak boleh kosong");
			return "CekMutasi-1";
		}
		
		if(!formMutasi.getPeriode().equalsIgnoreCase("sehari") && !formMutasi.getPeriode().equalsIgnoreCase("seminggu") && !formMutasi.getPeriode().equalsIgnoreCase("sebulan")){
			result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
			return "CekMutasi-1";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PETTERN_DATE);
		String endDate = simpleDateFormat.format(new Date());
		if (formMutasi.getJnsMutasi().equalsIgnoreCase(JNS_SEMUA_MUTASI)) {			
			model.addAttribute("mutasi", genAllTransaksi(tbUsers.getTbRekening().getNoRek(), genStartDate(formMutasi), endDate));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase(JNS_MUTASI_MASUK)) {
			model.addAttribute("mutasi", this.daoTbMutasi.findByFilterTransaksiIn(tbUsers.getTbRekening().getNoRek(), genStartDate(formMutasi), endDate));
		} else if (formMutasi.getJnsMutasi().equalsIgnoreCase(JNS_MUTASI_KELUAR)) {
			model.addAttribute("mutasi", this.daoTbMutasi.findByFilterTransaksiOut(tbUsers.getTbRekening().getNoRek(), genStartDate(formMutasi), endDate));
		} else {
			result.rejectValue("jnsMutasi", "error.formMutasi", "Maaf, jenis transaksi yang kamu masukan salah");
			return "CekMutasi-1";
		}
		return "CekMutasi-2";
	}

	@GetMapping("/nasabah/pin1")
	public String pinRequestPage(Model model, FormMasukanPin formMasukanPin) {
		return "pinrequest1";
	}

	@PostMapping("/nasabah/pin1")
	public String cekSaldoPinRequestPost(Model model, @Valid FormMasukanPin formMasukanPin, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean flagPin = false;
		boolean flagBlock = false;
		TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(formMasukanPin.getPin(), tbUsers.getTbRekening().getPin())) {
			int pinattempt = (Integer) session.getAttribute("pinattempt");
			pinattempt++;
			request.getSession().setAttribute("pinattempt", pinattempt);
			flagPin = true;
			if (pinattempt >= 3) {
				flagBlock = true;
				TbRekening tbRekening = this.daoTbRekening.findByNoRek(tbUsers.getTbRekening().getNoRek());
				tbRekening.setStatusRek(STATUS_REK_NOT_ACTIVE);
				tbUsers.setStatusUser(STATUS_USER_BLOCK);
				tbUsers.setKeterangan(KETERANGAN_BLOCK);
				this.daoTbRekening.update(tbRekening);
				this.daoTbUsers.update(tbUsers);
				return "redirect:/logout";
			}
		}

		if (bindingResult.hasErrors() || flagPin == true || flagBlock == true) {
			model.addAttribute("flagPin", flagPin);
			model.addAttribute("flagBlock", flagBlock);
			model.addAttribute("pinattempt", session.getAttribute("pinattempt"));
			return "pinrequest1";
		} else {
			request.getSession().setAttribute("pinTervalidasi", true);
			return (String) session.getAttribute("url");
		}
	}
	
	
	private long daysBetween(Calendar tanggalAwal, Calendar tanggalAkhir) {
        long lama = 0;
        Calendar tanggal = (Calendar) tanggalAwal.clone();
        while (tanggal.before(tanggalAkhir)) {
            tanggal.add(Calendar.DAY_OF_MONTH, 1);
            lama++;
        }
        return lama;
    }
	
	private String genStartDate(FormMutasi formMutasi) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String startDate = null;
		if (formMutasi.getPeriode().equalsIgnoreCase("sehari")) {
			cal.add(Calendar.DAY_OF_MONTH, 0);
			startDate = simpleDateFormat.format(cal.getTime());
		} else if (formMutasi.getPeriode().equalsIgnoreCase("seminggu")) {
			cal.add(Calendar.DAY_OF_MONTH, -7);
			startDate = simpleDateFormat.format(cal.getTime());
		} else if (formMutasi.getPeriode().equalsIgnoreCase("sebulan")) {
			cal.add(Calendar.DAY_OF_MONTH, -30);
			startDate = simpleDateFormat.format(cal.getTime());
		} 
		return startDate;
	}
	
	private List genAllTransaksi(String noRek, String startDate, String endDate) {
		List queryOut = this.daoTbMutasi.findByFilterTransaksiOut(noRek, startDate, endDate);
		List queryIn = this.daoTbMutasi.findByFilterTransaksiIn(noRek, startDate, endDate);
		queryOut.addAll(queryIn);		
		return queryOut;
		
	}
//	============================================END MUTASI=========================================
}
