package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Password {
	
	@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!")
	@Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!")
	private String newpassword;
	private String oldpassword;
	
	public Password() {
		
	}

	public Password(String oldpassword,
			@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!") @Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!") String newpassword) {
		super();
		this.oldpassword = oldpassword;
		this.newpassword = newpassword;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

}
