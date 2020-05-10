package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

import com.bca.minibank.dao.DaoTbLogAdmin;
import com.bca.minibank.dao.DaoTbMutasi;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbSetting;
import com.bca.minibank.dao.DaoTbTransaksi;
import com.bca.minibank.dao.DaoTbUserJnsTmp;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.entity.TbLogAdmin;
import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.form.FormAdminUbahPassword;
import com.bca.minibank.mail.ContentEmailWestBankPKWT;
import com.bca.minibank.mail.SendEmailSMTP;


@Controller
public class AdminController {
	@Autowired
	private DaoTbUsers daoUsers;
	
	@Autowired
	private DaoTbLogAdmin daoLogAdmin;
	
	@Autowired
	private DaoTbRekening daoRekening;
	
	@Autowired
	private DaoTbTransaksi daoTransaksi;
	
	@Autowired
	private DaoTbMutasi daoMutasi;
	
	@Autowired
	private DaoTbSetting daoSetting;	
	
	@Autowired
	private DaoTbUserJnsTmp daoUserJnsTmp;

	private static final String STATUSREK_ACTIVE = "ACTIVE";
	private static final String STATUSREK_NON_ACTIVE = "NOT ACTIVE";
	private static final String STATUSUSER_VERIFIED = "VERIFIED";
	private static final String STATUSUSER_NOT_VERIFIED = "NOT VERIFIED";
	private static final String STATUSUSER_PENDING = "PENDING";
	private static final String STATUSUSER_BLOCKED = "BLOCK";
	private static final String ROLE_NASABAH = "NASABAH";
	private static final String JNSMUTASI_IN = "UANG MASUK";
	private static final String ACTION_CHANGE_PASSWORD = "CHANGE PASSWORD";
	private static final String ACTION_VERIFY_NEW_USER = "VERIFY NEW USER";
	private static final String ACTION_NOT_VERIFY_NEW_USER = "NOT VERIFY NEW USER";
	private static final String ACTION_BLOCK_USER = "BLOCK USER";
	private static final String ACTION_UNBLOCK_NEW_USER = "UNBLOCK NEW USER";
	private static final String ACTION_UNBLOCK_USER = "UNBLOCK USER";
	private static final String ACTION_ACCEPT_SETOR_TUNAI = "ACCEPT SETORTUNAI";
	private static final String ACTION_DECLINE_SETOR_TUNAI = "DECLINE SETORTUNAI";
	private static final String ACTION_AUTO_DECLINE_SETOR_TUNAI = "AUTO DECLINE SETOR";
	private static final String ACTION_NON_ACTIVE_REKENING = "NON ACTIVE REKENING";
	private static final String ACTION_ACTIVE_REKENING = "ACTIVE REKENING";

	private static final String JNSTRANSAKSI_SETOR_TUNAI = "SETOR TUNAI";
	private static final String STATUSTRANSAKSI_PENDING = "PENDING";
	private static final String STATUSTRANSAKSI_SUCCESS = "SUCCESS";
	private static final String STATUSTRANSAKSI_FAILED = "FAILED";


	@GetMapping("/admin/listusers/terverifikasi")
	public String adminListuserTerverifikasi(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_VERIFIED,ROLE_NASABAH));
		return "/admin/listUserTerverifikasi.html";
	}

	@PostMapping("/admin/listusers/terverifikasi")
	public String adminListuserTerverifikasi2(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCaseOrderByIdUserAsc(STATUSUSER_VERIFIED, ROLE_NASABAH, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCaseOrderByIdUserAsc(STATUSUSER_VERIFIED,ROLE_NASABAH, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_VERIFIED,ROLE_NASABAH);
			}

		} else {
			users = daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_VERIFIED,ROLE_NASABAH);	
		}		
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "/admin/listUserTerverifikasi.html";
	}

	@GetMapping("/admin/listusers/baru")
	public String adminListUserBaru(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_PENDING,ROLE_NASABAH));		
		return "/admin/listUserBaru.html";
	}

	@PostMapping("/admin/listusers/baru")
	public String adminListUserBaru2(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCaseOrderByIdUserAsc(STATUSUSER_PENDING,ROLE_NASABAH, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCaseOrderByIdUserAsc(STATUSUSER_PENDING,ROLE_NASABAH, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_PENDING,ROLE_NASABAH);
			}

		} else {
			users = daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_PENDING,ROLE_NASABAH);	
		}	
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "/admin/listUserBaru.html";
	}

	@GetMapping("/admin/listusers/terblokir")
	public String adminListUserTerblokir(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_BLOCKED,ROLE_NASABAH));		
		return "/admin/listUserTerblokir.html";
	}

	@PostMapping("/admin/listusers/terblokir")
	public String adminListUserTerblokir2(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCaseOrderByIdUserAsc(STATUSUSER_BLOCKED,ROLE_NASABAH, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCaseOrderByIdUserAsc(STATUSUSER_BLOCKED,ROLE_NASABAH, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_BLOCKED,ROLE_NASABAH);
			}

		} else {
			users = daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_BLOCKED,ROLE_NASABAH);	
		}	
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "/admin/listUserTerblokir.html";
	}

	@GetMapping("/admin/listtransaksi/setortunai")
	public String adminListTransaksiSetor(Model model) {
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		return "/admin/listTransaksiSetorTunai.html";
	}

	@PostMapping("/admin/listtransaksi/setortunai")
	public String adminListTransaksiSetor2(Model model, String searchNoRek) {
		List<TbTransaksi> transaksi = new ArrayList<TbTransaksi>();
		if(!(searchNoRek == "")) {
			Boolean foundNoRek = daoRekening.findById(searchNoRek);
			if(foundNoRek) {
				TbRekening tbRekening = daoRekening.getOne(searchNoRek);
				transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiAndTbRekeningOrderByIdTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING, tbRekening);
				if(transaksi.isEmpty()) {
					model.addAttribute("msg", "Tidak ada Transaksi dengan No Rekening : "+ searchNoRek);
					transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING);
				}
			}else {
				model.addAttribute("msg", "No Rekening : "+ searchNoRek +" tidak terdaftar");
				transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING);
			}
		} else {
			return "redirect:/admin/listtransaksi/setortunai";
		}	
		model.addAttribute("listTransaksi", transaksi);	
		model.addAttribute("searchNoRek",searchNoRek);
		return "/admin/listTransaksiSetorTunai.html";
	}
	@GetMapping("/admin/listrekening/nonaktif")
	public String adminListRekeningNonAktif(Model model) {
		model.addAttribute("listRekening", daoRekening.getAllByStatusRekOrderByNoRekAsc( STATUSREK_NON_ACTIVE));		
		return "/admin/listRekeningNonAktif.html";
	}

	@PostMapping("/admin/listrekening/nonaktif")
	public String adminListRekeningNonAktif2(Model model, String searchNoRek) {
		List<TbRekening> rekening = new ArrayList<TbRekening>();
		if(!(searchNoRek == "")) {
			Boolean foundNoRek = daoRekening.findById(searchNoRek);
			if(foundNoRek) {
				//transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiAndTbRekening(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING, tbRekening);
				if(!(daoRekening.getOne(searchNoRek).getStatusRek().equals(STATUSREK_NON_ACTIVE))) {
					model.addAttribute("msg", "No Rekening : "+ searchNoRek + " berstatus aktif");
					rekening = daoRekening.getAllByStatusRekOrderByNoRekAsc(STATUSREK_NON_ACTIVE);
				}else {
					rekening.clear();
					rekening.add(daoRekening.getOne(searchNoRek));
				}
			}else {
				model.addAttribute("msg", "No Rekening : "+ searchNoRek +" tidak terdaftar");
				rekening = daoRekening.getAllByStatusRekOrderByNoRekAsc(STATUSREK_NON_ACTIVE);
			}
		} else {
			return "redirect:/admin/listrekening/nonaktif";
		}	
		model.addAttribute("listRekening", rekening);	
		model.addAttribute("searchNoRek",searchNoRek);
		return "/admin/listRekeningNonAktif.html";
	}

	@GetMapping("/admin/listusers/terverifikasi/ubahpassword")
	public String adminUbahPassword1() {
		return "redirect:/admin/listusers/terverifikasi";
	}

	@PostMapping("/admin/listusers/terverifikasi/ubahpassword")
	public String adminUbahPassword(Model model, int idUser, FormAdminUbahPassword formAdminUbahPassword) {
		model.addAttribute("user", daoUsers.getOne(idUser));
		return "/admin/ubahPassword.html";
	}
	
	@GetMapping("/admin/listusers/terverifikasi/ubahpassword/sukses")
	public String adminUbahPasswordSukses1() {
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terverifikasi/ubahpassword/sukses")
	public String adminUbahPasswordSukses(Model model, int idUser, @Valid FormAdminUbahPassword formAdminUbahPassword , BindingResult rs) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String msg = "";
		if(!(rs.hasErrors()))
		{
			if(formAdminUbahPassword.getPassword().equals(formAdminUbahPassword.getConfirmedPassword())) {
				String hashedPassword = passwordEncoder.encode(formAdminUbahPassword.getPassword());
				daoUsers.updatePassword(idUser, hashedPassword);
				simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(),ACTION_CHANGE_PASSWORD,idUser); //<-- daoUsers.findByUsername(auth.getName()) nanti diupdate mengambil dari session
				msg = "Password berhasil diubah.";
			}else {
				msg = "Password dan konfirmasi password tidak sama..";
			}
		}
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("msg", msg);
		return "/admin/ubahPassword.html";
	}		

	
	public void simpanLogAdmin(int idAdmin, String action, int idUser, int idTransaksi) {
		TbLogAdmin tbLogAdmin = new TbLogAdmin();
		tbLogAdmin.setTbUsers(daoUsers.getOne(idAdmin)); 
		tbLogAdmin.setAction(action);
		tbLogAdmin.setToIdUser(idUser);
		tbLogAdmin.setToIdTransaksi(idTransaksi);
		tbLogAdmin.setTglLog(new Timestamp(System.currentTimeMillis()));
		daoLogAdmin.add(tbLogAdmin);
	}
	public void simpanLogAdmin(int idAdmin, String action, int idUser) {
		//		TbUsers tbUsers = daoUsers.findByUsername(auth.getName());
		//		String noRek = tbUsers.getTbRekening().getNoRek();
		TbLogAdmin tbLogAdmin = new TbLogAdmin();
		tbLogAdmin.setTbUsers(daoUsers.getOne(idAdmin)); 
		tbLogAdmin.setAction(action);
		tbLogAdmin.setToIdUser(idUser);
		tbLogAdmin.setTglLog(new Timestamp(System.currentTimeMillis()));
		daoLogAdmin.add(tbLogAdmin);
	}
	
	@GetMapping("/admin/listusers/terverifikasi/listtransaksi")
	public String adminListTransaksiUser2() {
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terverifikasi/listtransaksi")
	public String adminListTransaksiUser(Model model, int idUser) {
		TbUsers tbUsers = daoUsers.getOne(idUser);
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("listTransaksi", daoTransaksi.getAllByTbRekeningOrderByIdTransaksi(tbUsers.getTbRekening()));
		return "/admin/listTransaksiUser.html";
	}
	
	
	public String genTab(int idJnsTab) {
		Random r = new Random( System.currentTimeMillis() );
		String tmp = "";
		String noRekGen = "";
		int index = 0;
		do {
			if(idJnsTab < 10)
			{
				tmp = "00" ;
			}
			else if(idJnsTab < 100)
			{
				tmp = "0";
			}
			tmp += String.valueOf(idJnsTab);
			noRekGen = tmp + String.valueOf(r.nextInt(10000000));
			index++;
			if(index == 30000000) {
				noRekGen = "";
				break;
			}
		}while(daoRekening.findById(noRekGen));
		
		return noRekGen;
	}

	public String genNoKartu(int idJnsTab) {
		String patternMM = "MM";
		SimpleDateFormat formatMM = new SimpleDateFormat(patternMM);
		String bulan  = formatMM.format(new Date());
		String patternYY = "yy";
		SimpleDateFormat formatYY = new SimpleDateFormat(patternYY);
		String tahun = formatYY.format(new Date());
		Random r = new Random( System.currentTimeMillis() );
		int nilai = r.nextInt(100000);
		if(idJnsTab < 10)
		{
			return ("00" + String.valueOf(idJnsTab)+ bulan + tahun + String.valueOf(nilai));
		}
		else if(idJnsTab < 100)
		{
			return ("0" + String.valueOf(idJnsTab) + bulan + tahun + String.valueOf(nilai));
		}
		else
		{
			return (String.valueOf(idJnsTab) + bulan + tahun + String.valueOf(nilai));
		}
	}
	
	@GetMapping("/admin/listusers/baru/terima")
	public String adminTerimaUserBaru1() {
		return "redirect:/admin/listusers/baru";
	}
	
	@PostMapping("/admin/listusers/baru/terima")
	public String adminTerimaUserBaru(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		TbUserJnsTmp tbUserJnsTmp =  daoUserJnsTmp.getOneByTbUsers(daoUsers.getOne(idUser));
		if(genTab(tbUserJnsTmp.getTbJnsTab().getIdJnsTab()) == "") {
			return "redirect:/admin/listusers/baru";
		}
		TbRekening tbRekening = new TbRekening();
		tbRekening.setSaldo(0);
		tbRekening.setStatusRek(STATUSREK_ACTIVE);
		tbRekening.setTbJnsTab(tbUserJnsTmp.getTbJnsTab());
		tbRekening.setTbUsers(tbUserJnsTmp.getTbUsers());
		tbRekening.setNoRek(genTab(tbUserJnsTmp.getTbJnsTab().getIdJnsTab()));
		tbRekening.setNoKartu(genNoKartu(tbUserJnsTmp.getTbJnsTab().getIdJnsTab()));
		daoRekening.add(tbRekening);
		daoUserJnsTmp.delete(tbUserJnsTmp.getIdTmp());
		daoUsers.updateStatusUser(idUser, STATUSUSER_VERIFIED);
		daoUsers.updateKeterangan(idUser, null);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_VERIFY_NEW_USER, idUser);
		//AccountEmailWestBankPKWT accEmailAdmin = new AccountEmailWestBankPKWT();
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentVerifyNewUser(daoUsers.getOne(idUser).getUsername(), daoUsers.findByUsername(auth.getName()).getNama());
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				daoUsers.getOne(idUser).getEmail(),
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		sendEmailSMTP.sendEmail();
		return "redirect:/admin/listusers/baru";
	}
	
	@GetMapping("/admin/listusers/baru/tolak")
	public String adminTolakUserBaru1() {
		return "redirect:/admin/listusers/baru";
	}
	
	@PostMapping("/admin/listusers/baru/tolak")
	public String adminTolakUserBaru(Model model, int idUser, String keterangan) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_NOT_VERIFIED);
		daoUsers.updateKeterangan(idUser, keterangan);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_NOT_VERIFY_NEW_USER, idUser);
		//AccountEmailWestBankPKWT accEmailAdmin = new AccountEmailWestBankPKWT();
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentNotVerifyNewUser(daoUsers.getOne(idUser).getUsername(), daoUsers.findByUsername(auth.getName()).getNama(), keterangan);
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				daoUsers.getOne(idUser).getEmail(),
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		sendEmailSMTP.sendEmail();
		return "redirect:/admin/listusers/baru";
	}
	
	@GetMapping("/admin/listusers/terverifikasi/block")
	public String adminBlockUser1() {
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terverifikasi/block")
	public String adminBlockUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_BLOCKED);
		daoUsers.updateKeterangan(idUser, "AKun anda di block oleh admin.");
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_BLOCK_USER, idUser);
		model.addAttribute("msgbox", "User berhasil diblokir.");
		model.addAttribute("users", daoUsers.getUsersByStatusAndRoleOrderByIdUserAsc(STATUSUSER_VERIFIED,ROLE_NASABAH));
		return "/admin/listUserTerverifikasi.html";

	}
	
	@GetMapping("/admin/listusers/terblokir/unblock")
	public String adminUnblockUser1() {
		return "redirect:/admin/listusers/terblokir";
	}
	
	@PostMapping("/admin/listusers/terblokir/unblock")
	public String adminUnblockUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(daoUsers.getOne(idUser).getTbRekening() == null) {
			daoUsers.updateStatusUser(idUser, STATUSUSER_PENDING);
			daoUsers.updateKeterangan(idUser, "Akun anda sedang dalam proses verifikasi admin");
			simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_UNBLOCK_NEW_USER, idUser);
		}else {
			daoUsers.updateStatusUser(idUser, STATUSUSER_VERIFIED);
			daoUsers.updateKeterangan(idUser, null);
			simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_UNBLOCK_USER, idUser);

		}
		return "redirect:/admin/listusers/terblokir";
	}
	
	@GetMapping("/admin/listtransaksi/setortunai/terima")
	public String adminTransaksiSetorTerima1() {
		return "redirect:/admin/listtransaksi/setortunai";
	}
	

	@PostMapping("/admin/listtransaksi/setortunai/terima")
	public String adminTransaksiSetorTerima(Model model, int idTransaksi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String msgBox = "";
		// validasi no rekenin tujuan, bila tidak terdaftari maka langsung failed
		TbTransaksi tbTransaksi = daoTransaksi.getOne(idTransaksi);
		String noRekAsal = tbTransaksi.getTbRekening().getNoRek();
		String noRekTujuan = tbTransaksi.getNoRekTujuan();
		int nominal = tbTransaksi.getNominal();
		if(daoRekening.getOne(noRekTujuan).getNoRek().isEmpty()){
			daoTransaksi.updateStatusTransaksiAndTglTransaksi(idTransaksi, STATUSTRANSAKSI_FAILED, new Timestamp(System.currentTimeMillis()));
			ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
			contentEmail.getContentFailedSetorTunai(idTransaksi, tbTransaksi.getTglPengajuan(), tbTransaksi.getJnsTransaksi(),noRekAsal,  noRekTujuan, nominal, daoUsers.findByUsername(auth.getName()).getNama(), " karena no. rekening tujuan tidak sama / tidak terdaftar.");
			SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
					daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
					daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
					daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
					daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
					daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getEmail(), 
					"", // cc kosong
					contentEmail.getContentSubject(),
					contentEmail.getContentFull(), 
					contentEmail.getContentType());
			sendEmailSMTP.sendEmail();
			simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_AUTO_DECLINE_SETOR_TUNAI, tbTransaksi.getTbRekening().getTbUsers().getIdUser(), idTransaksi);
			msgBox = "Setor tunai gagal karena no. rekening tujuan tidak terdaftar.";
		}else {
			//update status transaksi
			daoTransaksi.updateStatusTransaksiAndTglTransaksi(idTransaksi, STATUSTRANSAKSI_SUCCESS,new Timestamp(System.currentTimeMillis()));
			double saldo = daoRekening.getOne(noRekAsal).getSaldo();
			double saldoAkhir = saldo + nominal;
			//update saldo rekening
			daoRekening.updateSaldo(noRekTujuan, saldoAkhir);
			//add mutasi in
			TbMutasi tbMutasi = new TbMutasi();
			tbMutasi.setJnsMutasi(JNSMUTASI_IN);
			tbMutasi.setSaldoAkhir(saldoAkhir);
			tbMutasi.setTbTransaksi(daoTransaksi.getOne(idTransaksi));
			daoMutasi.add(tbMutasi);
			ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
			contentEmail.getContentSuccessSetorTunai(idTransaksi, tbTransaksi.getTglPengajuan(), tbTransaksi.getJnsTransaksi(), noRekAsal, noRekTujuan, nominal, daoUsers.findByUsername(auth.getName()).getNama());
			SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
					daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
					daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
					daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
					daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
					daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getEmail(), 
					"", // cc kosong
					contentEmail.getContentSubject(),
					contentEmail.getContentFull(), 
					contentEmail.getContentType());
			sendEmailSMTP.sendEmail();
			simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_ACCEPT_SETOR_TUNAI, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
			msgBox = "Setor tunai berhasil disetujui.";
		}
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		model.addAttribute("msgBox", msgBox);
		return "/admin/listTransaksiSetorTunai.html";
	}
	

	//	============================================ TOLAK SETOR TUNAI =========================================


	@GetMapping("/admin/listtransaksi/setortunai/tolak")
	public String adminTransaksiSetorTolak1() {
		return "redirect:/admin/listtransaksi/setortunai";
	}

	@PostMapping("/admin/listtransaksi/setortunai/tolak")
	public String adminTransaksiSetorTolak(Model model, int idTransaksi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoTransaksi.updateStatusTransaksiAndTglTransaksi(idTransaksi, STATUSTRANSAKSI_FAILED,  new Timestamp(System.currentTimeMillis()));
		TbTransaksi tbTransaksi = daoTransaksi.getOne(idTransaksi);
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentFailedSetorTunai(idTransaksi, tbTransaksi.getTglPengajuan(), tbTransaksi.getJnsTransaksi(), tbTransaksi.getTbRekening().getNoRek(), tbTransaksi.getNoRekTujuan(), tbTransaksi.getNominal(), daoUsers.findByUsername(auth.getName()).getNama(), ".");
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(daoSetting.getValue("SMTP_SERVER"), //accEmailAdmin.getSmtpServer(),
				daoSetting.getValue("PORT"), //accEmailAdmin.getPort(),
				daoSetting.getValue("USERNAME"), //accEmailAdmin.getUsername(),
				daoSetting.getValue("PASSWORD"), //accEmailAdmin.getPassword(),
				daoSetting.getValue("EMAIL"), //accEmailAdmin.getEmail(),
				daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getEmail(), 
				"", // cc kosong
				contentEmail.getContentSubject(),
				contentEmail.getContentFull(), 
				contentEmail.getContentType());
		sendEmailSMTP.sendEmail();
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_DECLINE_SETOR_TUNAI, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		model.addAttribute("msgBox", "Setor tunai berhasil ditolak.");
		return "/admin/listTransaksiSetorTunai.html";
	}
	
	@GetMapping("/admin/listusers/terverifikasi/detailrekening")
	public String adminDetailRekening1() {
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terverifikasi/detailrekening")
	public String adminDetailRekening(Model model, String noRek) {
		TbRekening rekening = daoRekening.getOne(noRek);
		String formURL = "/admin/listusers/terverifikasi/detailrekening/aktif";
		String btnText = "Aktifkan";
		if(rekening.getStatusRek().equals(STATUSREK_ACTIVE)) {
			formURL = "/admin/listusers/terverifikasi/detailrekening/nonaktif";
			btnText = "Non-Aktifkan";
		}
		model.addAttribute("rekening", rekening);
		model.addAttribute("formURL", formURL);
		model.addAttribute("btnText", btnText);
		return "/admin/detailRekening.html";
	}
	
	@GetMapping("/admin/listusers/terverifikasi/detailrekening/nonaktif")
	public String adminNonAktifkanRekeningDariDetailRekening1() {
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terverifikasi/detailrekening/nonaktif")
	public String adminNonAktifkanRekeningDariDetailRekening(Model model, String noRek) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoRekening.updateStatusRek(noRek, STATUSREK_NON_ACTIVE);
		daoUsers.updateStatusUser(daoRekening.getOne(noRek).getTbUsers().getIdUser(), STATUSUSER_BLOCKED);
		daoUsers.updateKeterangan(daoRekening.getOne(noRek).getTbUsers().getIdUser(), "Akun user anda di block oleh admin.");
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_NON_ACTIVE_REKENING, daoRekening.getOne(noRek).getTbUsers().getIdUser());
		return "redirect:/admin/listrekening/nonaktif";
	}

	@GetMapping("/admin/listusers/terverifikasi/detailrekening/aktif")
	public String adminAktifkanRekeningDariDetailRekening1() {
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terverifikasi/detailrekening/aktif")
	public String adminAktifkanRekeningDariDetailRekening(Model model, String noRek) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoRekening.updateStatusRek(noRek, STATUSREK_ACTIVE);
		daoUsers.updateStatusUser(daoRekening.getOne(noRek).getTbUsers().getIdUser(), STATUSUSER_VERIFIED);
		daoUsers.updateKeterangan(daoRekening.getOne(noRek).getTbUsers().getIdUser(), null);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_ACTIVE_REKENING, daoRekening.getOne(noRek).getTbUsers().getIdUser());
		return "redirect:/admin/listrekening/nonaktif";
		//		model.addAttribute("rekening", daoRekening.getOne(noRek));
		//		return "detailRekening.html";
	}
	
	@GetMapping("/admin/listrekening/nonaktif/aktif")
	public String adminAktifkanRekeningDariListRekening1() {
		return "redirect:/admin/listrekening/nonaktif";
	}
	
	@PostMapping("/admin/listrekening/nonaktif/aktif")
	public String adminAktifkanRekeningDariListRekening(Model model, String noRek) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoRekening.updateStatusRek(noRek, STATUSREK_ACTIVE);
		daoUsers.updateStatusUser(daoRekening.getOne(noRek).getTbUsers().getIdUser(), STATUSUSER_VERIFIED);
		daoUsers.updateKeterangan(daoRekening.getOne(noRek).getTbUsers().getIdUser(), null);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_ACTIVE_REKENING, daoRekening.getOne(noRek).getTbUsers().getIdUser());
		return "redirect:/admin/listrekening/nonaktif";
	}


}