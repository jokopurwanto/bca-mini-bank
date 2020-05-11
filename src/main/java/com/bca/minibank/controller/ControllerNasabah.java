package com.bca.minibank.controller;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.bca.minibank.form.FormBikinPin;
import com.bca.minibank.form.FormMasukanPin;
import com.bca.minibank.form.FormMutasi;
import com.bca.minibank.form.FormTransferPage;
import com.bca.minibank.form.FormTransferValidationPage;
import com.bca.minibank.mail.ContentEmailWestBankPKWT;
import com.bca.minibank.mail.SendEmailSMTP;
import com.bca.minibank.model.ModelSession;
import com.bca.minibank.model.ModelTransferPage;
import com.bca.minibank.utils.UtilsSession;
import com.bca.minibank.model.ModelTransaksi;
import com.bca.minibank.configuration.MBUserPrincipal;
import com.bca.minibank.form.FormTransaksi;
import com.bca.minibank.form.Password;
import com.bca.minibank.form.Pin;
import com.bca.minibank.repository.RepositoryTbRekening;
import com.bca.minibank.repository.RepositoryTbTransaksi;
import com.bca.minibank.repository.RepositoryTbUsers;
import com.bca.minibank.dao.DaoTbJnsTab;


@Controller
public class ControllerNasabah {
	@Autowired
	DaoTbUsers DaoTbUsers;

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

	@Autowired
	DaoTbJnsTab daoTbJnsTab;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	RepositoryTbUsers repositoryTbUsers;

	@Autowired
	RepositoryTbRekening repositoryTbRekening;

	@Autowired
	RepositoryTbTransaksi repositoryTbTransaksi;

	ModelTransaksi modelTransaksi;


	private static final String JNS_TRANSAKSI_TRANSFER = "TRANSFER";
	private static final String STATUS_TRANSAKSI_SUCCESS = "SUCCESS";	
	private static final String JNS_SEMUA_MUTASI = "SEMUA";
	private static final String JNS_MUTASI_MASUK = "UANG MASUK";
	private static final String JNS_MUTASI_KELUAR = "UANG KELUAR";
	private static final String PETTERN_DATE_TIME = "dd-MM-yyyy HH:mm:ss";
	private static final String PETTERN_DATE = "dd-MMM-yyyy";
	private static final String STATUS_REK_NOT_ACTIVE = "NOT ACTIVE";
	private static final String STATUS_USER_BLOCK = "BLOCK";
	private static final String PERIODE_SEHARI = "SEHARI";
	private static final String PERIODE_SEMINGGU = "SEMINGGU";
	private static final String PERIODE_SEBULAN = "SEBULAN";
	private static final String KETERANGAN_BLOCK = "Akun anda terblokir dikarenakan salah password atau salah pin sebanyak 3x berturut-turut";

	//	============================================ VERIFIKASI =========================================

	@GetMapping("/verifikasi") //fungsi Fix, URL tidak fix
	public String verifikasiPage(Model model) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TbRekening TbRekeningTemp = daoTbRekening.getOne(user.getNoRek());
		model.addAttribute("noRek", TbRekeningTemp.getNoRek());
		model.addAttribute("noKartu", TbRekeningTemp.getNoKartu());
		model.addAttribute("nama", user.getNama());
		model.addAttribute("username", user.getUsername());
		model.addAttribute("jenisTabungan", TbRekeningTemp.getTbJnsTab().getNamaJnsTab());
		return "/nasabah/userterverifikasi";
	}		

	//	============================================ BUAT PIN =========================================

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
			TbRekening TbRekeningTemp = daoTbRekening.getOne(user.getNoRek());
			TbRekeningTemp.setPin(bCryptPasswordEncoder.encode(formBikinPin.getPin()));
			daoTbRekening.update(user.getNoRek(), TbRekeningTemp);
			return "/nasabah/bikinpinberhasil";
		}
	}

	@GetMapping("/registrasi/konfirmasi")
	public String registarasiKonfirmasiGet(Model model) 
	{

		return "redirect:/registrasi";
	}

	@GetMapping("/registrasi/sukses")
	public String registarasiSuksesGet(Model model) {

		return "redirect:/registrasi";
	}



	//	============================================ BERANDA =========================================

	@GetMapping("/beranda")
	public String homePage(HttpServletRequest request, Model model) {
		MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(user.getStatusRek().equals("NOT ACTIVE"))
		{
			model.addAttribute("message1", "Status Rekening : Tidak Aktif");
			model.addAttribute("message2", "Rekening anda tidak dapat mengakses fitur WestBank. Silahkan kontak admin untuk mengaktifkan rekening anda!");
		}
		model.addAttribute("nama", user.getNama());
		return "/nasabah/beranda";
	}

	@GetMapping("/nasabah")
	public String getNasabah(Model model) {

		return "redirect:/beranda";
	}


	//	============================================ CEK SALDO =========================================

	@GetMapping("/nasabah/ceksaldo") 
	public String cekSaldo(Model model, HttpSession session, HttpServletRequest request) 
	{	
		if((Boolean)session.getAttribute("pinTervalidasi") == null || (Boolean)session.getAttribute("pinTervalidasi") == false)
		{
			request.getSession().setAttribute("validateUrl", true);
			request.getSession().setAttribute("url", "redirect:/nasabah/ceksaldo");
			return "redirect:/nasabah/pin";
		}
		else
		{
			request.getSession().setAttribute("pinTervalidasi", false);
			MBUserPrincipal user = (MBUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			TbRekening tbRekening = daoTbRekening.getOne(user.getNoRek());

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

	//	============================================ UBAH PASSWORD =========================================

	@GetMapping("/nasabah/ubahpassword")
	public String changePasswordpage(Model model,Password password) {

		return "/nasabah/cekpassword";
	}

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
		if(!(rs.hasErrors()))
		{
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
		return "/nasabah/UbahPassword";
	}

	//	============================================ UBAH PIN =========================================

	@GetMapping("/nasabah/ubahpin")
	public String changePinpage(Model model,Pin pin) {

		return "/nasabah/cekpin";
	}

	@PostMapping("/nasabah/ubahpin")
	public String Pinpage(Model model, @Valid Pin pin , BindingResult rs, HttpServletRequest request) {
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
		if(!(rs.hasErrors()))
		{
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
		return "/nasabah/UbahPin";

	}

	//	============================================ SETOR TUNAI =========================================

	@GetMapping("/nasabah/setor")
	public String setorForm(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());
		System.out.println(tbUsers.getEmail());

		FormTransaksi formTransaksi = new FormTransaksi();
		formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
		model.addAttribute("formTransaksi", formTransaksi);

		return "/nasabah/setor";
	}



	@PostMapping("/nasabah/setor")
	public String setor(Model model, @Valid FormTransaksi formTransaksi ,BindingResult rs) {
		if(rs.hasErrors()) {

			return"/nasabah/setor";
		}

		modelTransaksi = new ModelTransaksi(formTransaksi);
		System.out.println(modelTransaksi.getNominal());
		System.out.println(modelTransaksi.getNoRek());

		formTransaksi.setNominal(modelTransaksi.getNominal());
		formTransaksi.setNoRek(modelTransaksi.getNoRek());

		model.addAttribute("formTransaksi", formTransaksi);

		return "/nasabah/SetorTunai-2";

	}

	@GetMapping("/nasabah/setor/pengajuan-sukses")
	public String getSetorKonfirmasi() {

		return "redirect:/nasabah/setor";
	}

	@PostMapping("/nasabah/setor/pengajuan-sukses")
	public String save(Model model,@Valid FormTransaksi formTransaksi) {

		TbTransaksi tbTransaksi = new TbTransaksi();

		tbTransaksi.setTglPengajuan(new Date());
		tbTransaksi.setJnsTransaksi("SETOR TUNAI");
		tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
		tbTransaksi.setStatusTransaksi("PENDING");
		tbTransaksi.setNominal(formTransaksi.getNominal());

		TbRekening tbRekening = this.daoTbRekening.noRek(formTransaksi.getNoRek());

		tbTransaksi.setTbRekening(tbRekening);

		this.daoTbTransaksi.add(tbTransaksi);

		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentPengajuanSetorTunai(tbTransaksi.getIdTransaksi(), tbTransaksi.getTglPengajuan(), tbTransaksi.getJnsTransaksi(),formTransaksi.getNoRek(), tbTransaksi.getNoRekTujuan(), formTransaksi.getNominal());
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"),
				daoSetting.getValue("PORT"),
				daoSetting.getValue("USERNAME"),
				daoSetting.getValue("PASSWORD"),
				daoSetting.getValue("EMAIL"), 
				daoTbTransaksi.getOne(tbTransaksi.getIdTransaksi()).getTbRekening().getTbUsers().getEmail(), "",
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		sendEmailSMTP.sendEmail();
		model.addAttribute("formTransaksi",tbTransaksi);
		return"/nasabah/Success";

	}

	@GetMapping("/nasabah/setor/status")
	public  String statusSetor(Model model){

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());


		TbTransaksi tbTransaksi = new TbTransaksi();

		FormTransaksi formTransaksi = new FormTransaksi();
		formTransaksi.setNoRek(tbUsers.getTbRekening().getNoRek());
		tbTransaksi.setNoRekTujuan(formTransaksi.getNoRek());
		tbTransaksi.setJnsTransaksi("SETOR TUNAI");
		model.addAttribute("status", getAllTransaksiSetor(tbTransaksi.getStatusTransaksi(),tbTransaksi.getNoRekTujuan(),tbTransaksi.getJnsTransaksi()));

		return "/nasabah/SetorTunai-3";
	}
	
	private List getAllTransaksiSetor(String statusTransaksi,String noRekTujuan, String jnsTransaksi) {
		
		List queryPending = this.daoTbTransaksi.findByNoRekTujuanANDJnsTransaksiANDStatusTransaksi( "PENDING",noRekTujuan, jnsTransaksi);
		List querySuccess = this.daoTbTransaksi.findTop7ByStatusTransaksiAndNoRekTujuanAndJnsTransaksiOrderByTglTransaksi("SUCCESS", noRekTujuan, jnsTransaksi);
		queryPending.addAll(querySuccess);
		return queryPending;

	}

	//	============================================ TRANSFER =========================================

	@GetMapping("/nasabah/transfer")
	public String getTransfer(Model model, HttpServletRequest req) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUsers tbUsers = daoTbUsers.findByUsername(auth.getName());
		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = modelSession.getModelTransferPage();
		FormTransferPage formTransferPage = new FormTransferPage(modelTransferPage);
		formTransferPage.setNoRek(tbUsers.getTbRekening().getNoRek());
		model.addAttribute("formTransferPage", formTransferPage);

		return "/nasabah/Transfer-1";
	}

	@PostMapping("/nasabah/transfer")
	public String postTransfer(@Valid FormTransferPage formTransferPage, BindingResult result, Model model,
			HttpServletRequest req) {
		//		validasi no rek tujuan
		TbRekening NoRekTujuanExist = this.daoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		if ((!formTransferPage.getNoRekTujuan().equals("")) && (null == NoRekTujuanExist)) {
			result.rejectValue("noRekTujuan", "error.formTransferPage", "Maaf, no rek yg d tuju salah");
			return "/nasabah/Transfer-1";
		}

		if (result.hasErrors()) {
			return "/nasabah/Transfer-1";
		}

		int nominal = Integer.parseInt(formTransferPage.getNominal());
		//      validasi limit transaksi
		TbRekening cekSaldo = this.daoTbRekening.findByNoRek(formTransferPage.getNoRek());
		if (cekSaldo.getTransaksiHarian() + nominal > cekSaldo.getTbJnsTab().getLimitTransaksi()) {
			result.rejectValue("nominal", "error.formTransferPage", "maaf, sisa limit transfer harian kamu adalah "
					+ (formatRp(cekSaldo.getTbJnsTab().getLimitTransaksi() - cekSaldo.getTransaksiHarian())));
			return "/nasabah/Transfer-1";
		}

		//      validasi saldo tidak mencukupi
		if (nominal > cekSaldo.getSaldo()) {
			result.rejectValue("nominal", "error.formTransferPage", "Maaf, saldo tidak mencukupi, sisa saldo kamu "+ formatRp(cekSaldo.getSaldo()));
			return "/nasabah/Transfer-1";
		}



		ModelSession modelSession = UtilsSession.getTransferInSession(req);
		ModelTransferPage modelTransferPage = new ModelTransferPage(formTransferPage);
		modelSession.setModelTransferPage(modelTransferPage);

		FormTransferValidationPage formTransferValidationPage = new FormTransferValidationPage();
		formTransferValidationPage.setNoRek(formTransferPage.getNoRek());
		formTransferValidationPage.setKeterangan(formTransferPage.getKeterangan());
		formTransferValidationPage.setNominal(formTransferPage.getNominal());
		formTransferValidationPage.setNoRekTujuan(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setJnsTransaksi(JNS_TRANSAKSI_TRANSFER);

		//		menampilkan tanggal
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PETTERN_DATE_TIME);
		String date = simpleDateFormat.format(new Date());
		formTransferValidationPage.setTglTransaksi(date);

		TbRekening rekTujuan = this.daoTbRekening.findByNoRek(formTransferPage.getNoRekTujuan());
		formTransferValidationPage.setNamaPenerima(rekTujuan.getTbUsers().getNama());
		model.addAttribute("formTransferValidationPage", formTransferValidationPage);

		return "/nasabah/Transfer-2";
	}

	@GetMapping("/nasabah/transfer/konfirmasi")
	public String getTransferValidation() {
		return "redirect:/nasabah/transfer";
	}

	@PostMapping("/nasabah/transfer/konfirmasi")
	public String postTransferValidation(@Valid FormTransferValidationPage formTransferValidationPage,
			BindingResult result, Model model, HttpServletRequest req) {

		if (result.hasErrors()) {
			return "/nasabah/Transfer-2";
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
					this.daoTbRekening.updateData(rekPengirim);
					this.daoTbUsers.updateData(userPengirim);
					return "redirect:/logout";
				}
			}
			int limitAttempt = 3 - Integer.parseInt((String) req.getSession().getAttribute("code"));
			result.rejectValue("pin", "error.formTransferPage", "Maaf, pin anda salah, tersisa "+limitAttempt+"x percobaan");
			return "/nasabah/Transfer-2";
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
		transaksi.setNote("TF - "+ rekPengirim.getTbUsers().getNama()+" ke "+rekPenerima.getNoRek()+" ("+rekPenerima.getTbUsers().getNama()+") "+formTransferValidationPage.getKeterangan());
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
		this.daoTbRekening.updateData(rekPengirim);

		//		+ saldo penerima
		rekPenerima.setSaldo(rekPenerima.getSaldo() + nominal);
		this.daoTbRekening.updateData(rekPenerima);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PETTERN_DATE_TIME);
		String date = simpleDateFormat.format(new Date());
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentSuccessSendTransfer(transaksi.getIdTransaksi(), 
				date, 
				JNS_TRANSAKSI_TRANSFER, 
				userPengirim.getTbRekening().getNoRek(), 
				formTransferValidationPage.getNoRekTujuan(), 
				formTransferValidationPage.getNamaPenerima(), 
				"TF - "+ rekPengirim.getTbUsers().getNama()+" ke "+rekPenerima.getNoRek()+" ("+rekPenerima.getTbUsers().getNama()+") "+formTransferValidationPage.getKeterangan(), 
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
				"TF - "+ rekPengirim.getTbUsers().getNama()+" ke "+rekPenerima.getNoRek()+" ("+rekPenerima.getTbUsers().getNama()+") "+formTransferValidationPage.getKeterangan(), 
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

		return "/nasabah/Transfer-3";
	}
	//	============================================ MUTASI =========================================
	@GetMapping("/nasabah/mutasi")
	public String getMutasi(Model model, HttpSession session, HttpServletRequest req) {
		if ((Boolean) session.getAttribute("pinTervalidasi") == null
				|| (Boolean) session.getAttribute("pinTervalidasi") == false) {
			req.getSession().setAttribute("url", "redirect:/nasabah/mutasi");
			req.getSession().setAttribute("validateUrl", true);
			return "redirect:/nasabah/pin";
		} else {
			req.getSession().setAttribute("pinTervalidasi", false);

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			TbUsers tbUsers = this.daoTbUsers.findByUsername(auth.getName());
			FormMutasi formMutasi = new FormMutasi();
			formMutasi.setNoRek(tbUsers.getTbRekening().getNoRek());
			model.addAttribute("formMutasi", formMutasi);
			return "/nasabah/CekMutasi-1";
		}
	}

	@GetMapping("/nasabah/mutasi/jangkawaktu")
	public String getMutasiJangkaWaktu() {
		return "redirect:/nasabah/mutasi";
	}

	@PostMapping("/nasabah/mutasi/jangkawaktu")
	public String postMutasiJangkaWaktu(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "/nasabah/CekMutasi-1";
		}

		if ((null == formMutasi.getStartDate()) || (null == formMutasi.getEndDate())) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal harus disi semua");
			return "/nasabah/CekMutasi-1";
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
			return "/nasabah/CekMutasi-1";
		}else if(compareEndDate < 0){
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal akhir lebih besar dari tanggal hari ini");
			return "/nasabah/CekMutasi-1";		
		}else if(compareRangeDate < 0) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, tanggal awal lebih besar dari tanggal alkhir");
			return "/nasabah/CekMutasi-1";
		}else if(rangeDate > 30) {
			result.rejectValue("endDate", "error.formMutasi", "Maaf, periode mutasi yang dapat dipilih 30 hari");
			return "/nasabah/CekMutasi-1";
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
			return "/nasabah/CekMutasi-1";
		}	

		String rangePeriode = startDate + " s.d " + endDate;
		model.addAttribute("rangePeriode", rangePeriode);
		model.addAttribute("jnsMutasi", formMutasi.getJnsMutasi());
		model.addAttribute("tbUsers", tbUsers);
		return "/nasabah/CekMutasi-2";
	}

	@GetMapping("/nasabah/mutasi/periode")
	public String getMutasiPeriode() {
		return "redirect:/nasabah/mutasi";
	}

	@PostMapping("/nasabah/mutasi/periode")
	public String postMutasiPeriode(@Valid FormMutasi formMutasi, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "/nasabah/CekMutasi-1";
		}

		//		jika null direct ke transfer
		if (null == formMutasi) {
			return "redirect:/nasabah/transfer";
		}

		if (null == formMutasi.getPeriode()) {
			result.rejectValue("periode", "error.formMutasi", "Maaf, periode tidak boleh kosong");
			return "/nasabah/CekMutasi-1";
		}

		if(!formMutasi.getPeriode().equalsIgnoreCase(PERIODE_SEHARI) && !formMutasi.getPeriode().equalsIgnoreCase(PERIODE_SEMINGGU) && !formMutasi.getPeriode().equalsIgnoreCase(PERIODE_SEBULAN)){
			result.rejectValue("periode", "error.formMutasi", "maaf, periode yg kamu masukan salah");
			return "/nasabah/CekMutasi-1";
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
			return "/nasabah/CekMutasi-1";
		}

		//		genReturn(genStartDate(formMutasi), endDate, model, formMutasi, tbUsers);
		String rangePeriode = genStartDate(formMutasi) + " s.d " + endDate;
		model.addAttribute("rangePeriode", rangePeriode);
		model.addAttribute("jnsMutasi", formMutasi.getJnsMutasi());
		model.addAttribute("tbUsers", tbUsers);
		return "/nasabah/CekMutasi-2";
	}

	@GetMapping("/nasabah/pin")
	public String pinRequestPage(Model model, FormMasukanPin formMasukanPin, HttpSession session) {
		if(session.getAttribute("validateUrl") == null || (Boolean)session.getAttribute("validateUrl") == false)
		{
			return "redirect:/beranda";
		}
		else
		{
			session.removeAttribute("validateUrl");
			return "/nasabah/pinrequest";
		}
	}

	@PostMapping("/nasabah/pin")
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
				this.daoTbRekening.updateData(tbRekening);
				this.daoTbUsers.updateData(tbUsers);
				return "redirect:/logout";
			}
		}

		if (bindingResult.hasErrors() || flagPin == true || flagBlock == true) {
			model.addAttribute("flagPin", flagPin);
			model.addAttribute("flagBlock", flagBlock);
			model.addAttribute("pinattempt", session.getAttribute("pinattempt"));
			return "/nasabah/pinrequest";
		} else {
			request.getSession().setAttribute("pinattempt", 0);
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
		if (formMutasi.getPeriode().equalsIgnoreCase(PERIODE_SEHARI)) {
			cal.add(Calendar.DAY_OF_MONTH, 0);
			startDate = simpleDateFormat.format(cal.getTime());
		} else if (formMutasi.getPeriode().equalsIgnoreCase(PERIODE_SEMINGGU)) {
			cal.add(Calendar.DAY_OF_MONTH, -7);
			startDate = simpleDateFormat.format(cal.getTime());
		} else if (formMutasi.getPeriode().equalsIgnoreCase(PERIODE_SEBULAN)) {
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

	public String formatRp(double nominal) {
		DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

		formatRp.setCurrencySymbol("Rp. ");
		formatRp.setMonetaryDecimalSeparator(',');
		formatRp.setGroupingSeparator('.');

		kursIndonesia.setDecimalFormatSymbols(formatRp);
		return kursIndonesia.format(nominal);
	}

	public String genReturn(String startDate, String endDate, Model model, FormMutasi formMutasi, TbUsers tbUsers) {
		String rangePeriode = startDate + " s.d " + endDate;
		model.addAttribute("rangePeriode", rangePeriode);
		model.addAttribute("jnsMutasi", formMutasi.getJnsMutasi());
		model.addAttribute("tbUsers", tbUsers);
		return "/nasabah/CekMutasi-2";
	}


}

