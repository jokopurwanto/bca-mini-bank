package com.bca.minibank.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.dao.DaoTbRekening;

@Controller
public class ControllerLogin {

	@Autowired
	private DaoTbUsers DaoTbUsers;
	
	@Autowired
	private DaoTbRekening DaoTbRekening;
	
	@GetMapping("/login")
	public String getLogin() {
		return "login.html";
	}
	
	@PostMapping("/login")
	public String postLogin(HttpServletRequest request, HttpSession session, Model model, String username, String password) {
		//Mencari kecocokan username dan password dalam database
		int flag = 0;
		List<TbUsers> Users = DaoTbUsers.getAll();
		for(TbUsers TU : Users)
		{	
			if(username.equals(TU.getUsername()) && password.equals(TU.getPassword())) 
			{
				request.getSession().setAttribute("idUser", TU.getIdUser());
				request.getSession().setAttribute("namaUser", TU.getNama());
				request.getSession().setAttribute("role", TU.getRole());
				request.getSession().setAttribute("statusUser", TU.getStatusUser());
				request.getSession().setAttribute("keterangan", TU.getKeterangan());
				flag = 2;
				break;
			}
			else if(username.equals(TU.getUsername()))
			{
				request.getSession().setAttribute("idUser", TU.getIdUser());
				request.getSession().setAttribute("statusUser", TU.getStatusUser());
				request.getSession().setAttribute("keterangan", TU.getKeterangan());
				flag = 1;
			}
		}
		
		//Memilih route bedasarkan data input
		//Berhasil Login
		if(flag == 2)
		{
			//memilih route bedasarkan role
			//Role Admin
			if(session.getAttribute("role").equals("ADMIN"))
			{
				return "redirect:/admin";
			}
			//Role Nasabah
			else if(session.getAttribute("role").equals("NASABAH") && session.getAttribute("statusUser").equals("AKTIF"))
			{
				int idUser = (int)session.getAttribute("idUser");
				List<TbRekening> Rekenings = DaoTbRekening.getAll();
				for(TbRekening TR : Rekenings)
				{
					if(idUser == TR.getTbUsers().getIdUser())
					{
						request.getSession().setAttribute("PIN", TR.getPin());
						break;
					}
					else
					{
						request.getSession().setAttribute("PIN", null);
					}
				}
				//Akun Lama
				if(session.getAttribute("PIN") != null)
				{
					return "redirect:/home";
				}
				//Akun Baru
				else
				{
					return "redirect:/konfirmasi";
				}
			}
			else //Kalau statusnya pending atau not approve
			{
				model.addAttribute("keterangan", session.getAttribute("keterangan"));
				return "login.html";		
			}
		}
		//Username benar, Password Salah
		if(flag == 1)
		{
			model.addAttribute("keterangan", "Your password is incorrect");
			return "login.html";
		}
		//Username Tidak Ada
		else
		{
			model.addAttribute("keterangan", "User does not exists");
			return "login.html";
		}
	}
	
	
}
