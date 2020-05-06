package com.bca.minibank.form;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class FormAdminUbahPassword {

	@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!")
	@Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!")
	private String password;
	
	private String confirmedPassword;

	
	public FormAdminUbahPassword() {
		
	}


	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}