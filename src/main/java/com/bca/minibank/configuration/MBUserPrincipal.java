package com.bca.minibank.configuration;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bca.minibank.entity.TbUsers;

public class MBUserPrincipal implements UserDetails{
    private TbUsers TbUsers;
    
    //Bawaan dari
    public MBUserPrincipal(TbUsers user) 
    {
        this.TbUsers = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
	{
    	if(TbUsers.getRole().equals("ADMIN"))
    	{
    		return Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
    	}
    	else if(TbUsers.getRole().equals("NASABAH") && TbUsers.getTbRekening().getPin() != null )
    	{
    		return Collections.singleton(new SimpleGrantedAuthority("NASABAH"));
    	}
    	else
    	{
    		return Collections.singleton(new SimpleGrantedAuthority("AKUNBARU"));
    	}
	}
	
    @Override
	public String getPassword()
	{
		return TbUsers.getPassword();
	}
	
    @Override
	public String getUsername()
	{
		return TbUsers.getUsername();
	}
		
	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked()
	{
		if(TbUsers.getStatusUser().equals("VERIFIED"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}
	
	@Override
	public boolean isEnabled()
	{
		return true;
	}
    
	//Tambahan Informasi Users
	public int getIdUser()
	{
		return TbUsers.getIdUser();
	}
	
	public String getNama()
	{
		return TbUsers.getNama();
	}
	
	public String getKeterangan()
	{
		return TbUsers.getKeterangan();
	}
	
	public String getAlamat()
	{
		return TbUsers.getAlamat();
	}
	
	public String getNoHp()
	{
		return TbUsers.getNoHp();
	}
	
	public String getNoKtp()
	{
		return TbUsers.getNoKtp();
	}
	
	public String getEmail()
	{
		return TbUsers.getEmail();
	}
	
	public String getStatusUser()
	{
		return TbUsers.getStatusUser();
	}
	
	public String getPin()
	{
		return TbUsers.getTbRekening().getPin();
	}
	
	public String getNoRek()
	{
		return TbUsers.getTbRekening().getNoRek();
	}
	
	public String getRole()
	{
		return TbUsers.getRole();
	}
}