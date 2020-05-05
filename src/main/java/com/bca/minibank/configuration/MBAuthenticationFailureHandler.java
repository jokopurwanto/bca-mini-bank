package com.bca.minibank.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.dao.DaoTbUsers;


public class MBAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	DaoTbUsers DaoTbUsers;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException 
	{
		 	TbUsers user = DaoTbUsers.findTbUsersByUsername((String)request.getParameter("username"));
		 	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();  
			if(user == null)
			{//username gak ketemu
				request.getSession().setAttribute("error", "Username tidak ditemukan");
			}
			else if(!encoder.matches((String)request.getParameter("password"), user.getPassword()))	//password gak ketemu 
			{
				HttpSession session = request.getSession();
				if(session.getAttribute("usernameBefore") == null)
				{
					request.getSession().setAttribute("loginattempt", 1);
				}
				else if(session.getAttribute("usernameBefore").equals(request.getParameter("username"))) //counting salah password
				{
					int loginattempt = (Integer)session.getAttribute("loginattempt");
					loginattempt++;
					request.getSession().setAttribute("loginattempt", loginattempt);
					if(loginattempt > 3)
					{
						user.setStatusUser("BLOCK");
						user.setKeterangan("Akun anda terblokir dikarenakan salah password 3x berturut-turut");
						DaoTbUsers.update(user.getIdUser(), user);
					}
				}
				else
				{
					request.getSession().setAttribute("loginattempt", 1);
				}
				request.getSession().setAttribute("error", "Password anda salah sebanyak: " + session.getAttribute("loginattempt") + "x");
				request.getSession().setAttribute("usernameBefore", user.getUsername());
			}
			else //Pending, Not Verified, Block
			{
				request.getSession().setAttribute("error", user.getKeterangan());
			}
			response.sendRedirect("/login?error=true");
	}
}
