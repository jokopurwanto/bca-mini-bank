package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class FormUbahPassword {

	@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!")
	@Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!")
	private String password;
	
	private String confirmedPassword;

	private int idUser;
	
	public FormUbahPassword() {
		
	}

	public FormUbahPassword(int idUser, String confirmedPassword,
			@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!") @Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!") String password) {
		super();
		this.idUser = idUser;
		this.confirmedPassword = confirmedPassword;
		this.password = password;
	}
	
	

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
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