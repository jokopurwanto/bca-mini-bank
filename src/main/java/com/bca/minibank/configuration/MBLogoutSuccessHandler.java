package com.bca.minibank.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.bca.minibank.dao.DaoTbUsers;
import com.bca.minibank.entity.TbUsers;

public class MBLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler 
{
 
	@Autowired
	DaoTbUsers DaoTbUsers;
	
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException 
    {
    	TbUsers user = DaoTbUsers.findTbUsersByUsername(authentication.getName());
    	String error = user.getKeterangan();
    	if(error != null)
    	{
    		request.getSession().setAttribute("error", error);
    		response.sendRedirect("/login?error=true");
    	}
    	else
    	{
	    	request.getSession().setAttribute("message", "Anda telah melakukan log out!");
	    	response.sendRedirect("/login?message=true");
    	}
    }
}