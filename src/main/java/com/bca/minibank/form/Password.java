package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Password {
	
	@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!")
	@Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!")
	private String newpassword;
	private String oldpassword;
	private String confirmpass;
	
	public Password() {
		
	}

	public Password(
			@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!") @Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!") String newpassword,
			String oldpassword, String confirmpass) {
		super();
		this.newpassword = newpassword;
		this.oldpassword = oldpassword;
		this.confirmpass = confirmpass;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getConfirmpass() {
		return confirmpass;
	}

	public void setConfirmpass(String confirmpass) {
		this.confirmpass = confirmpass;
	}

}
