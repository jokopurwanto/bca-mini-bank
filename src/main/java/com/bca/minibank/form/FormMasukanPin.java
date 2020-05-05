package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class FormMasukanPin {
	@NotBlank(message = "Pin harus diisi")
	@Pattern(regexp = "^[0-9]{6,6}$", message = "Input PIN tidak valid!")
	private String pin;

	public FormMasukanPin() {
	
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
