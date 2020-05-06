package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.entity.TbLogAdmin;
import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
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

	private static final String STATUSREK_ACTIVE = "ACTIVE";
	private static final String STATUSREK_NON_ACTIVE = "NON ACTIVE";
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
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(STATUSUSER_VERIFIED,ROLE_NASABAH));		
		return "/admin/listUserTerverifikasi.html";
	}
	
	@PostMapping("/admin/listusers/terverifikasi")
	public String adminListuserTerverifikasi2(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(STATUSUSER_VERIFIED, ROLE_NASABAH, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(STATUSUSER_VERIFIED,ROLE_NASABAH, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(STATUSUSER_VERIFIED,ROLE_NASABAH);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(STATUSUSER_VERIFIED,ROLE_NASABAH);	
		}		
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "/admin/listUserTerverifikasi.html";
	}
	
	@GetMapping("/admin/listusers/baru")
	public String adminListUserBaru(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(STATUSUSER_PENDING,ROLE_NASABAH));		
		return "/admin/listUserBaru.html";
	}
	
	@PostMapping("/admin/listusers/baru")
	public String adminListUserBaru2(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(STATUSUSER_PENDING,ROLE_NASABAH, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(STATUSUSER_PENDING,ROLE_NASABAH, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(STATUSUSER_PENDING,ROLE_NASABAH);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(STATUSUSER_PENDING,ROLE_NASABAH);	
		}	
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "/admin/listUserBaru.html";
	}

	@GetMapping("/admin/listusers/terblokir")
	public String adminListUserTerblokir(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(STATUSUSER_BLOCKED,ROLE_NASABAH));		
		return "/admin/listUserTerblokir.html";
	}
	
	@PostMapping("/admin/listusers/terblokir")
	public String adminListUserTerblokir2(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(STATUSUSER_BLOCKED,ROLE_NASABAH, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(STATUSUSER_BLOCKED,ROLE_NASABAH, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(STATUSUSER_BLOCKED,ROLE_NASABAH);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(STATUSUSER_BLOCKED,ROLE_NASABAH);	
		}	
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "/admin/listUserTerblokir.html";
	}
	
	@GetMapping("/admin/listtransaksi/setortunai")
	public String adminListTransaksiSetor(Model model) {
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		return "/admin/listTransaksiSetorTunai.html";
	}
	
	@PostMapping("/admin/listtransaksi/setortunai")
	public String adminListTransaksiSetor2(Model model, String searchNoRek) {
		List<TbTransaksi> transaksi = new ArrayList<TbTransaksi>();
		if(!(searchNoRek == "")) {
			Boolean foundNoRek = daoRekening.findById(searchNoRek);
			if(foundNoRek) {
				TbRekening tbRekening = daoRekening.getOne(searchNoRek);
				transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiAndTbRekening(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING, tbRekening);
				if(transaksi.isEmpty()) {
					model.addAttribute("msg", "Tidak ada Transaksi dengan No Rekening : "+ searchNoRek);
					transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING);
				}
			}else {
				model.addAttribute("msg", "No Rekening : "+ searchNoRek +" tidak terdaftar");
				transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING);
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
		model.addAttribute("listRekening", daoRekening.getAllByStatusRek( STATUSREK_NON_ACTIVE));		
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
					rekening = daoRekening.getAllByStatusRek(STATUSREK_NON_ACTIVE);
				}else {
					rekening.clear();
					rekening.add(daoRekening.getOne(searchNoRek));
				}
			}else {
				model.addAttribute("msg", "No Rekening : "+ searchNoRek +" tidak terdaftar");
				rekening = daoRekening.getAllByStatusRek(STATUSREK_NON_ACTIVE);
			}
		} else {
			return "redirect:/admin/listrekening/nonaktif";
		}	
		model.addAttribute("listRekening", rekening);	
		model.addAttribute("searchNoRek",searchNoRek);
		return "/admin/listRekeningNonAktif.html";
	}
	
	@PostMapping("/admin/listuser/terverifikasi/ubahpassword")
	public String adminUbahPassword(Model model, int idUser, FormAdminUbahPassword formAdminUbahPassword) {
		model.addAttribute("user", daoUsers.getOne(idUser));
		return "/admin/ubahPassword.html";
	}

	@PostMapping("/admin/listuser/terverifikasi/ubahpassword/sukses")
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
	
	@PostMapping("/admin/listuser/terverifikasi/listtransaksi")
	public String adminListTransaksiUser(Model model, int idUser) {
		TbUsers tbUsers = daoUsers.getOne(idUser);
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("listTransaksi", daoTransaksi.getAllByTbRekening(tbUsers.getTbRekening()));
		return "/admin/listTransaksiUser.html";
	}
	
	@PostMapping("/admin/listusers/baru/terima")
	public String adminTerimaUserBaru(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_VERIFIED);
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
	
	@PostMapping("/admin/listusers/terverifikasi/block")
	public String adminBlockUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_BLOCKED);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_BLOCK_USER, idUser);
		return "redirect:/admin/listusers/terverifikasi";
	}
	
	@PostMapping("/admin/listusers/terblokir/unblock")
	public String adminUnblockUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(daoUsers.getOne(idUser).getTbRekening() == null) {
			daoUsers.updateStatusUser(idUser, STATUSUSER_PENDING);
			simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_UNBLOCK_NEW_USER, idUser);
		}else {
			daoUsers.updateStatusUser(idUser, STATUSUSER_VERIFIED);
			simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_UNBLOCK_USER, idUser);
			
		}
		return "redirect:/admin/listusers/terblokir";
	}
	
	@PostMapping("/admin/listtransaksi/setortunai/terima")
	public String adminTransaksiSetorTerima(Model model, int idTransaksi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String msgBox = "";
		// validasi no rekenin tujuan, bila tidak terdaftar atau bukan no rekening sendiri maka langsung failed //karena setor tunai hanya bisa ke rekening sendiri
		TbTransaksi tbTransaksi = daoTransaksi.getOne(idTransaksi);
		String noRekTujuan = tbTransaksi.getNoRekTujuan();
		String noRekAsal = tbTransaksi.getTbRekening().getNoRek();
		double nominal = tbTransaksi.getNominal();
		if(daoRekening.getOne(noRekTujuan).getNoRek().isEmpty() || !(noRekAsal.equals(noRekTujuan))){
			daoTransaksi.updateStatusTransaksiAndTglTransaksi(idTransaksi, STATUSTRANSAKSI_FAILED, new Timestamp(System.currentTimeMillis()));
			ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
			contentEmail.getContentFailedSetorTunai(idTransaksi, tbTransaksi.getTglPengajuan(), tbTransaksi.getJnsTransaksi(), noRekAsal, noRekTujuan, nominal, daoUsers.findByUsername(auth.getName()).getNama(), " karena no. rekening tujuan tidak sama / tidak terdaftar.");
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
			msgBox = "Setor tunai gagal karena no. rekening tujuan tidak sama / tidak terdaftar.";
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
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		model.addAttribute("msgBox", msgBox);
		return "/admin/listTransaksiSetorTunai.html";
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
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		model.addAttribute("msgBox", "Setor tunai berhasil ditolak.");
		return "/admin/listTransaksiSetorTunai.html";
	}
	
	@PostMapping("/admin/listuser/terverifikasi/detailrekening")
	public String adminDetailRekening(Model model, String noRek) {
		TbRekening rekening = daoRekening.getOne(noRek);
		String formURL = "/admin/listuser/terverifikasi/detailrekening/aktif";
		String btnText = "Aktifkan";
		if(rekening.getStatusRek().equals(STATUSREK_ACTIVE)) {
			formURL = "/admin/listuser/terverifikasi/detailrekening/nonaktif";
			btnText = "Non-Aktifkan";
		}
		model.addAttribute("rekening", rekening);
		model.addAttribute("formURL", formURL);
		model.addAttribute("btnText", btnText);
		return "/admin/detailRekening.html";
	}
	
	@PostMapping("/admin/listuser/terverifikasi/detailrekening/nonaktif")
	public String adminNonAktifkanRekeningDariDetailRekening(Model model, String noRek) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoRekening.updateStatusRek(noRek, STATUSREK_NON_ACTIVE);
		daoUsers.updateStatusUser(daoRekening.getOne(noRek).getTbUsers().getIdUser(), STATUSUSER_BLOCKED);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_NON_ACTIVE_REKENING, daoRekening.getOne(noRek).getTbUsers().getIdUser());
		return "redirect:/admin/listrekening/nonaktif";
	}
	
	@PostMapping("/admin/listuser/terverifikasi/detailrekening/aktif")
	public String adminAktifkanRekeningDariDetailRekening(Model model, String noRek) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoRekening.updateStatusRek(noRek, STATUSREK_ACTIVE);
		daoUsers.updateStatusUser(daoRekening.getOne(noRek).getTbUsers().getIdUser(), STATUSUSER_VERIFIED);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_ACTIVE_REKENING, daoRekening.getOne(noRek).getTbUsers().getIdUser());
		return "redirect:/admin/listrekening/nonaktif";
//		model.addAttribute("rekening", daoRekening.getOne(noRek));
//		return "detailRekening.html";
	}
	
	@PostMapping("/admin/listrekening/nonaktif/aktif")
	public String adminAktifkanRekeningDariListRekening(Model model, String noRek) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoRekening.updateStatusRek(noRek, STATUSREK_ACTIVE);
		simpanLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_ACTIVE_REKENING, daoRekening.getOne(noRek).getTbUsers().getIdUser());
		return "redirect:/admin/listrekening/nonaktif";
	}
	

}