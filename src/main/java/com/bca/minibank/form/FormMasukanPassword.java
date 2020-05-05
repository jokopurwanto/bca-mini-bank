package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class FormMasukanPassword {
	
	@NotBlank(message = "Password harus diisi")
	@Pattern(regexp = "^[0-9]{6,6}$", message = "Input Password tidak valid!")
	private String password;
	
	public FormMasukanPassword() {
		
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
