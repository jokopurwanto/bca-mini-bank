package com.bca.minibank.configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class MBAccessDeniedHandler implements AccessDeniedHandler {
 
 
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException, ServletException {
         
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> roleTargetUrlMap = new HashMap<>();
	      roleTargetUrlMap.put("ADMIN", "/admin");
	      roleTargetUrlMap.put("NASABAH", "/beranda");
	      roleTargetUrlMap.put("AKUNBARU", "/verifikasi");
	      roleTargetUrlMap.put("NASABAHNOTACTIVE", "/beranda");
	      
	      //ngecek autoritas
	      final Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
	      for (final GrantedAuthority grantedAuthority : authorities) 
	      {
	          String authorityName = grantedAuthority.getAuthority();
	          if(roleTargetUrlMap.containsKey(authorityName)) 
	          {
	        	  response.sendRedirect(roleTargetUrlMap.get(authorityName));
	          }
	      }       
    }
}