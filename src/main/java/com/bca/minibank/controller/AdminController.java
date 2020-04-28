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
	private String statusUserPending = "PENDING";
	private String statusUserBlocked = "BLOCK";
	private String roleNasabah = "ROLE_NASABAH";
//	private String jnsMutasiIn = "IN";
	private String jnsMutasiOut = "OUT";
	private String actionLogAdminChangePassword = "UBAH PASSWORD";
	
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
			TbLogAdmin tbLogAdmin = new TbLogAdmin();
			//tbLogAdmin.setIdLog(0); //<--harusnya autoincrement
			tbLogAdmin.setTbUsers(daoUsers.getOne(1)); //<--sementara, ntar diupdate jadi ambil idUser dari session
			tbLogAdmin.setAction(actionLogAdminChangePassword);
			//tbLogAdmin.setToIdTransaksi(0); // karena change password ini jadi null (bukan menunjuk ke transaksi)
			tbLogAdmin.setToIdUser(idUser);
			tbLogAdmin.setTglLog(new Timestamp(System.currentTimeMillis()));
			daoLogAdmin.add(tbLogAdmin);
			msg = "Password berhasil diubah." + daoUsers.getOne(1).getUsername();
		}else {
			msg = "Password dan konfirmasi password tidak sama..";
		}
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("msg", msg);
		return "adminChangesPassword.html";
	}
	
	@PostMapping("/adminTransaksiUser")
	public String adminTransaksiUser(Model model, int idUser) {
		TbUsers tbUsers = daoUsers.getOne(idUser);
		model.addAttribute("user", daoUsers.getOne(idUser));
		model.addAttribute("listTransaksi", daoTransaksi.getAllByTbRekening(tbUsers.getTbRekening()));
		return "adminTransaksiUser.html";
	}
}