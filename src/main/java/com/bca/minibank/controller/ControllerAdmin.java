package com.bca.minibank.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.dao.DaoTbRekening;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUsers;


@Controller
public class ControllerAdmin {
	@Autowired
	private DaoTbUsers daoUsers;
	@Autowired
	private DaoTbRekening daoRekening;
	
	private String statusUserActive = "ACTIVE";
	private String statusUserNonActive = "NON ACTIVE";
	private String statusUserBlocked = "BLOCK";
	private String roleNasabah = "ROLE_NASABAH";
	
	@GetMapping("/adminActiveUsers")
	public String adminActiveUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(statusUserActive,roleNasabah));		
		return "adminActiveUsers.html";
	}
	
	@PostMapping("/adminActiveUsers")
	public String adminActiveUsersFilter(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserActive, roleNasabah, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserActive,roleNasabah, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(statusUserActive,roleNasabah);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(statusUserActive,roleNasabah);	
		}		
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "adminNewUsers.html";
	}
	
	@GetMapping("/adminNewUsers")
	public String adminNewUsers(Model model) {
		model.addAttribute("users", daoUsers.getUsersByStatusAndRole(statusUserNonActive,roleNasabah));		
		return "adminNewUsers.html";
	}
	
	@PostMapping("/adminNewUsers")
	public String adminNewUsersFilter(Model model, String searchUsername) {
		List<TbUsers> users = new ArrayList<TbUsers>();
		if(!(searchUsername == "")) {
			if(!(daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserNonActive,roleNasabah, searchUsername).isEmpty())) {
				users = daoUsers.getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(statusUserNonActive,roleNasabah, searchUsername);
			}else {
				model.addAttribute("msg", "USERNAME : "+ searchUsername +" tidak terdaftar");
				users = daoUsers.getUsersByStatusAndRole(statusUserNonActive,roleNasabah);
			}
				
		} else {
			users = daoUsers.getUsersByStatusAndRole(statusUserNonActive,roleNasabah);	
		}	
		model.addAttribute("users", users);	
		model.addAttribute("searchUsername",searchUsername);
		return "adminActiveUsers.html";
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
}
