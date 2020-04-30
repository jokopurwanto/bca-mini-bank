package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.dao.DaoTbLogAdmin;
import com.bca.minibank.dao.DaoTbMutasi;
import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbTransaksi;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.entity.TbLogAdmin;
import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.mail.AccountEmailWestBankPKWT;
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
	
	private static final String STATUSUSER_VERIFIED = "VERIFIED";
	private static final String STATUSUSER_NOT_VERIFIED = "NOT VERIFIED";
	private static final String STATUSUSER_PENDING = "PENDING";
	private static final String STATUSUSER_BLOCKED = "BLOCK";
	private static final String ROLE_NASABAH = "ROLE_NASABAH";
	private static final String JNSMUTASI_IN = "IN";
	private static final String ACTION_CHANGE_PASSWORD = "CHANGE PASSWORD";
	private static final String ACTION_VERIFY_NEW_USER = "VERIFY NEW USER";
	private static final String ACTION_NOT_VERIFY_NEW_USER = "NOT VERIFY NEW USER";
	private static final String ACTION_BLOCK_USER = "BLOCK USER";
	private static final String ACTION_UNBLOCK_NEW_USER = "UNBLOCK NEW USER";
	private static final String ACTION_UNBLOCK_USER = "UNBLOCK USER";
	private static final String ACTION_ACCEPT_SETOR_TUNAI = "ACCEPT SETORTUNAI";
	private static final String ACTION_DECLINE_SETOR_TUNAI = "DECLINE SETORTUNAI";
	private static final String ACTION_AUTO_DECLINE_SETOR_TUNAI = "AUTO DECLINE SETOR";
	
	private static final String JNSTRANSAKSI_SETOR_TUNAI = "SETOR TUNAI";
	private static final String STATUSTRANSAKSI_PENDING = "PENDING";
	private static final String STATUSTRANSAKSI_SUCCESS = "SUCCESS";
	private static final String STATUSTRANSAKSI_FAILED = "FAILED";
	
	@GetMapping("/adminVerifiedUsers")
	public String adminVerifiedUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(STATUSUSER_VERIFIED,ROLE_NASABAH));		
		return "adminVerifiedUsers.html";
	}
	
	@PostMapping("/adminVerifiedUsers")
	public String adminVerifiedUsersFilter(Model model, String searchUsername) {
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
		return "adminVerifiedUsers.html";
	}
	
	@GetMapping("/adminNewUsers")
	public String adminNewUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(STATUSUSER_PENDING,ROLE_NASABAH));		
		return "adminNewUsers.html";
	}
	
	@PostMapping("/adminNewUsers")
	public String adminNewUsersFilter(Model model, String searchUsername) {
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
		return "adminNewUsers.html";
	}

	@GetMapping("/adminBlockedUsers")
	public String adminBlockedUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(STATUSUSER_BLOCKED,ROLE_NASABAH));		
		return "adminBlockedUsers.html";
	}
	
	@PostMapping("/adminBlockedUsers")
	public String adminBlockedUsersFilter(Model model, String searchUsername) {
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
		return "adminBlockedUsers.html";
	}
	
	@PostMapping("/adminChangesPassword")
	public String adminUbahPassword(Model model, int idUser) {
		model.addAttribute("user", daoUsers.getOne(idUser));
		return "adminChangesPassword.html";
	}
	
	@PostMapping("/adminChangesPasswordEnd")
	public String adminUbahPasswordEnd(Model model, int idUser, String password, String confirmedPassword) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String msg = "";
		if(password.equals(confirmedPassword)) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);
			daoUsers.updatePassword(idUser, hashedPassword);
			saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(),ACTION_CHANGE_PASSWORD,idUser); //<-- daoUsers.findByUsername(auth.getName()) nanti diupdate mengambil dari session
			msg = "Password berhasil diubah.";
		}else {
			msg = "Password dan konfirmasi password tidak sama..";
		}
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("msg", msg);
		return "adminChangesPassword.html";
	}	
	
	public void saveLogAdmin(int idAdmin, String action, int idUser, int idTransaksi) {
		TbLogAdmin tbLogAdmin = new TbLogAdmin();
		tbLogAdmin.setTbUsers(daoUsers.getOne(idAdmin)); 
		tbLogAdmin.setAction(action);
		tbLogAdmin.setToIdUser(idUser);
		tbLogAdmin.setToIdTransaksi(idTransaksi);
		tbLogAdmin.setTglLog(new Timestamp(System.currentTimeMillis()));
		daoLogAdmin.add(tbLogAdmin);
	}
	public void saveLogAdmin(int idAdmin, String action, int idUser) {
//		TbUsers tbUsers = daoUsers.findByUsername(auth.getName());
//		String noRek = tbUsers.getTbRekening().getNoRek();
		TbLogAdmin tbLogAdmin = new TbLogAdmin();
		tbLogAdmin.setTbUsers(daoUsers.getOne(idAdmin)); 
		tbLogAdmin.setAction(action);
		tbLogAdmin.setToIdUser(idUser);
		tbLogAdmin.setTglLog(new Timestamp(System.currentTimeMillis()));
		daoLogAdmin.add(tbLogAdmin);
	}
	
	@PostMapping("/adminTransaksiUser")
	public String adminTransaksiUser(Model model, int idUser) {
		TbUsers tbUsers = daoUsers.getOne(idUser);
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("listTransaksi", daoTransaksi.getAllByTbRekening(tbUsers.getTbRekening()));
		return "adminTransaksiUser.html";
	}
	
	@PostMapping("/adminVerifiedNewUser")
	public String adminVerifiedNewUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_VERIFIED);
		saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_VERIFY_NEW_USER, idUser);
		AccountEmailWestBankPKWT accEmailAdmin = new AccountEmailWestBankPKWT();
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentVerifyNewUser(daoUsers.getOne(idUser).getUsername(), daoUsers.findByUsername(auth.getName()).getNama());
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(accEmailAdmin.getSmtpServer(),
														accEmailAdmin.getPort(),
														accEmailAdmin.getUsername(),
														accEmailAdmin.getPassword(),
														accEmailAdmin.getEmail(),
														daoUsers.getOne(idUser).getEmail(),
														"", // cc kosong
														contentEmail.getContentSubject(),
														contentEmail.getContentFull(), 
														contentEmail.getContentType());
		sendEmailSMTP.sendEmail();
		return "redirect:/adminNewUsers";
	}

	@PostMapping("/adminNotVerifiedNewUser")
	public String adminNotVerifiedNewUser(Model model, int idUser, String keterangan) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_NOT_VERIFIED);
		daoUsers.updateKeterangan(idUser, keterangan);
		saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_NOT_VERIFY_NEW_USER, idUser);
		AccountEmailWestBankPKWT accEmailAdmin = new AccountEmailWestBankPKWT();
		ContentEmailWestBankPKWT contentEmail = new ContentEmailWestBankPKWT();
		contentEmail.getContentNotVerifyNewUser(daoUsers.getOne(idUser).getUsername(), daoUsers.findByUsername(auth.getName()).getNama(), keterangan);
		SendEmailSMTP sendEmailSMTP = new SendEmailSMTP(accEmailAdmin.getSmtpServer(),
														accEmailAdmin.getPort(),
														accEmailAdmin.getUsername(),
														accEmailAdmin.getPassword(),
														accEmailAdmin.getEmail(),
														daoUsers.getOne(idUser).getEmail(),
														"", // cc kosong
														contentEmail.getContentSubject(),
														contentEmail.getContentFull(), 
														contentEmail.getContentType());
		sendEmailSMTP.sendEmail();
		return "redirect:/adminNewUsers";
	}
	
	@PostMapping("/adminBlockUser")
	public String adminBlockUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoUsers.updateStatusUser(idUser, STATUSUSER_BLOCKED);
		saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_BLOCK_USER, idUser);
		return "redirect:/adminVerifiedUsers";
	}
	
	@PostMapping("/adminUnblockUser")
	public String adminUnblockUser(Model model, int idUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(daoUsers.getOne(idUser).getTbRekening() == null) {
			daoUsers.updateStatusUser(idUser, STATUSUSER_PENDING);
			saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_UNBLOCK_NEW_USER, idUser);
		}else {
			daoUsers.updateStatusUser(idUser, STATUSUSER_VERIFIED);
			saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_UNBLOCK_USER, idUser);
			
		}
		return "redirect:/adminBlockedUsers";
	}
	
	@GetMapping("/adminTransaksiSetor")
	public String adminTransaksiSetor(Model model) {
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		return "adminTransaksiSetor.html";
	}
	
	@PostMapping("/adminTransaksiSetor")
	public String adminTransaksiSetor2(Model model, String searchNoRek) {
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
			return "redirect:/adminTransaksiSetor";
		}	
		model.addAttribute("listTransaksi", transaksi);	
		model.addAttribute("searchNoRek",searchNoRek);
		return "adminTransaksiSetor.html";
	}
	
	@PostMapping("/adminTransaksiSetorAccept")
	public String adminTransaksiSetorAccept(Model model, int idTransaksi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String msgBox = "";
		// validasi no rekenin tujuan, bila tidak terdaftar atau bukan no rekening sendiri maka langsung failed //karena setor tunai hanya bisa ke rekening sendiri
		TbTransaksi tbTransaksi = daoTransaksi.getOne(idTransaksi);
		String noRekTujuan = tbTransaksi.getNoRekTujuan();
		String noRekAsal = tbTransaksi.getTbRekening().getNoRek();
		double nominal = tbTransaksi.getNominal();
		if(daoRekening.getOne(noRekTujuan).getNoRek().isEmpty() || !(noRekAsal.equals(noRekTujuan))){
			daoTransaksi.updateStatusTransaksi(idTransaksi, STATUSTRANSAKSI_FAILED);
			saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_AUTO_DECLINE_SETOR_TUNAI, tbTransaksi.getTbRekening().getTbUsers().getIdUser(), idTransaksi);
			msgBox = "Setor tunai gagal karena no. rekening tujuan tidak sama / tidak terdaftar.";
		}else {
			//update status transaksi
			daoTransaksi.updateStatusTransaksi(idTransaksi, STATUSTRANSAKSI_SUCCESS);
			double saldo = daoRekening.getOne(noRekAsal).getSaldo();
			double saldoAkhir = saldo + nominal;
			//update saldo rekening
			daoRekening.updateSaldo(noRekTujuan, saldoAkhir);
			//add mutasi in
			TbMutasi tbMutasi = new TbMutasi();
			tbMutasi.setJnsMutasi(JNSMUTASI_IN);
			tbMutasi.setNominal(nominal);
			tbMutasi.setSaldoAkhir(saldoAkhir);
			tbMutasi.setTbTransaksi(daoTransaksi.getOne(idTransaksi));
			tbMutasi.setTglMutasi(new Date());
			daoMutasi.add(tbMutasi);
			saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_ACCEPT_SETOR_TUNAI, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
			msgBox = "Setor tunai berhasil disetujui.";
		}
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		model.addAttribute("msgBox", msgBox);
		return "adminTransaksiSetor.html";
	}
	
	@PostMapping("/adminTransaksiSetorDecline")
	public String adminTransaksiSetorDecline(Model model, int idTransaksi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		daoTransaksi.updateStatusTransaksi(idTransaksi, STATUSTRANSAKSI_FAILED);
		saveLogAdmin(daoUsers.findByUsername(auth.getName()).getIdUser(), ACTION_DECLINE_SETOR_TUNAI, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(JNSTRANSAKSI_SETOR_TUNAI, STATUSTRANSAKSI_PENDING));		
		model.addAttribute("msgBox", "Setor tunai berhasil ditolak.");
		return "adminTransaksiSetor.html";
	}
	
}