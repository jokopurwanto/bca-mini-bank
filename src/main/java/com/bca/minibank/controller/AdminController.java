package com.bca.minibank.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
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

	private String statusUserVerified = "VERIFIED";
	private String statusUserNotVerified = "NOT VERIFIED";
	private String statusUserPending = "PENDING";
	private String statusUserBlocked = "BLOCK";
	private String roleNasabah = "ROLE_NASABAH";
//	private String jnsMutasiIn = "IN";
	private String jnsMutasiOut = "OUT";
	private String actionLogAdminChangePassword = "CHANGE PASSWORD";
	private String actionLogAdminVerifyNewUser = "VERIFY NEW USER";
	private String actionLogAdminNotVerifyNewUser = "NOT VERIFY NEW USER";
	private String actionLogAdminBlockUser = "BLOCK USER";
	private String actionLogAdminUnblockNewUser = "UNBLOCK NEW USER";
	private String actionLogAdminUnblockUser = "UNBLOCK USER";
	private String actionLogAdminTransaksiSetorAccept = "ACCEPT SETORTUNAI";
	private String actionLogAdminTransaksiSetorDecline = "DECLINE SETORTUNAI";
	private String actionLogAdminTransaksiSetorDeclineAuto = "AUTO DECLINE SETOR";
	
	private String jnsTransaksiSetorTunai = "SETOR TUNAI";
	private String statusTransaksiPending = "PENDING";
	private String statusTransaksiSuccess = "SUCCESS";
	private String statusTransaksiFailed = "FAILED";
	
	private int idAdminSession = 1; //sementara nnti diganti ambil dari session
	
	@GetMapping("/adminVerifiedUsers")
	public String adminVerifiedUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(statusUserVerified,roleNasabah));		
		return "adminVerifiedUsers.html";
	}
	
	@PostMapping("/adminVerifiedUsers")
	public String adminVerifiedUsersFilter(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserVerified, roleNasabah, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserVerified,roleNasabah, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(statusUserVerified,roleNasabah);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(statusUserVerified,roleNasabah);	
		}		
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "adminVerifiedUsers.html";
	}
	
	@GetMapping("/adminNewUsers")
	public String adminNewUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(statusUserPending,roleNasabah));		
		return "adminNewUsers.html";
	}
	
	@PostMapping("/adminNewUsers")
	public String adminNewUsersFilter(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserPending,roleNasabah, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserPending,roleNasabah, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(statusUserPending,roleNasabah);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(statusUserPending,roleNasabah);	
		}	
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "adminNewUsers.html";
	}

	@GetMapping("/adminBlockedUsers")
	public String adminBlockedUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(statusUserBlocked,roleNasabah));		
		return "adminBlockedUsers.html";
	}
	
	@PostMapping("/adminBlockedUsers")
	public String adminBlockedUsersFilter(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserBlocked,roleNasabah, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserBlocked,roleNasabah, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(statusUserBlocked,roleNasabah);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(statusUserBlocked,roleNasabah);	
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
		String msg = "";
		if(password.equals(confirmedPassword)) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);
			daoUsers.updatePassword(idUser, hashedPassword);
			saveLogAdmin(idAdminSession,actionLogAdminChangePassword,idUser); //<-- idAdminSession nanti diupdate mengambil dari session
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
		daoUsers.updateStatusUser(idUser, statusUserVerified);
		saveLogAdmin(idAdminSession, actionLogAdminVerifyNewUser, idUser);
		return "redirect:/adminNewUsers";
	}

	@PostMapping("/adminNotVerifiedNewUser")
	public String adminNotVerifiedNewUser(Model model, int idUser, String keterangan) {
		daoUsers.updateStatusUser(idUser, statusUserNotVerified);
		daoUsers.updateKeterangan(idUser, keterangan);
		saveLogAdmin(idAdminSession, actionLogAdminNotVerifyNewUser, idUser);
		return "redirect:/adminNewUsers";
	}
	
	@PostMapping("/adminBlockUser")
	public String adminBlockUser(Model model, int idUser) {
		daoUsers.updateStatusUser(idUser, statusUserBlocked);
		saveLogAdmin(idAdminSession, actionLogAdminBlockUser, idUser);
		return "redirect:/adminVerifiedUsers";
	}
	
	@PostMapping("/adminUnblockUser")
	public String adminUnblockUser(Model model, int idUser) {
		if(daoUsers.getOne(idUser).getTbRekening() == null) {
			daoUsers.updateStatusUser(idUser, statusUserPending);
			saveLogAdmin(idAdminSession, actionLogAdminUnblockNewUser, idUser);
		}else {
			daoUsers.updateStatusUser(idUser, statusUserVerified);
			saveLogAdmin(idAdminSession, actionLogAdminUnblockUser, idUser);
			
		}
		return "redirect:/adminBlockedUsers";
	}
	
	@GetMapping("/adminTransaksiSetor")
	public String adminTransaksiSetor(Model model) {
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(jnsTransaksiSetorTunai, statusTransaksiPending));		
		return "adminTransaksiSetor.html";
	}
	
	@PostMapping("/adminTransaksiSetor")
	public String adminTransaksiSetor2(Model model, String searchNoRek) {
		List<TbTransaksi> transaksi = new ArrayList<TbTransaksi>();
		if(!(searchNoRek == "")) {
			Boolean foundNoRek = daoRekening.findById(searchNoRek);
			if(foundNoRek) {
				TbRekening tbRekening = daoRekening.getOne(searchNoRek);
				transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksiAndTbRekening(jnsTransaksiSetorTunai, statusTransaksiPending, tbRekening);
				if(transaksi.isEmpty()) {
					model.addAttribute("msg", "Tidak ada Transaksi dengan No Rekening : "+ searchNoRek);
					transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(jnsTransaksiSetorTunai, statusTransaksiPending);
				}
			}else {
				model.addAttribute("msg", "No Rekening : "+ searchNoRek +" tidak terdaftar");
				transaksi = daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(jnsTransaksiSetorTunai, statusTransaksiPending);
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
		String msgBox = "";
		// validasi no rekenin tujuan, bila tidak terdaftar atau bukan no rekening sendiri maka langsung failed //karena setor tunai hanya bisa ke rekening sendiri
		String noRekTujuan = daoTransaksi.getOne(idTransaksi).getNoRekTujuan();
		String noRekAsal = daoTransaksi.getOne(idTransaksi).getTbRekening().getNoRek();
		if(daoRekening.getOne(noRekTujuan).getNoRek().isEmpty() || !(noRekAsal.equals(noRekTujuan))){
			daoTransaksi.updateStatusTransaksi(idTransaksi, statusTransaksiFailed);
			saveLogAdmin(idAdminSession, actionLogAdminTransaksiSetorDeclineAuto, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
			msgBox = "Setor tunai gagal karena no. rekening tujuan tidak sama / tidak terdaftar.";
		}else {
			daoTransaksi.updateStatusTransaksi(idTransaksi, statusTransaksiSuccess);
			saveLogAdmin(idAdminSession, actionLogAdminTransaksiSetorAccept, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
			msgBox = "Setor tunai berhasil disetujui.";
		}
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(jnsTransaksiSetorTunai, statusTransaksiPending));		
		model.addAttribute("msgBox", msgBox);
		return "adminTransaksiSetor.html";
	}
	
	@PostMapping("/adminTransaksiSetorDecline")
	public String adminTransaksiSetorDecline(Model model, int idTransaksi) {
		daoTransaksi.updateStatusTransaksi(idTransaksi, statusTransaksiFailed);
		saveLogAdmin(idAdminSession, actionLogAdminTransaksiSetorDecline, daoTransaksi.getOne(idTransaksi).getTbRekening().getTbUsers().getIdUser(), idTransaksi);
		model.addAttribute("listTransaksi", daoTransaksi.getAllByJnsTransaksiAndStatusTransaksi(jnsTransaksiSetorTunai, statusTransaksiPending));		
		model.addAttribute("msgBox", "Setor tunai berhasil ditolak.");
		return "adminTransaksiSetor.html";
	}
	
}