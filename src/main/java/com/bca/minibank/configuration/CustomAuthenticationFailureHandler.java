package com.bca.minibank.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;



public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		// 	TbUsers user = userRepository.findByUsername(username);
//			MyUserPrincipal user = (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			if(user.getUsername() != null)
//			{//username gak ketemu
//				request.getSession().setAttribute("error", "Username tidak ditemukan");
//			}
//			else if(exception.getMessage() != null)	//password gak ketemu (counting)
//			{
//				request.getSession().setAttribute("error", "Password anda salah");
//			}
//			else if(user.isAccountNonLocked() == false)	//pending, blocked. not verified
//			{
//				request.getSession().setAttribute("error", user.getKeterangan());
//			}
//			else
//			{
				request.getSession().setAttribute("error", "Autentikasi gw masih error berarti!");
//			}
			response.sendRedirect("/login?error=true");
	}
}
