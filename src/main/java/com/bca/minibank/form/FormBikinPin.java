package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class FormBikinPin {
	@NotBlank(message = "Pin harus diisi")
	@Pattern(regexp = "^[0-9]{6,6}$", message = "Pengisian pin harus 6 digit angka!")
	private String pin;
	
	private String confirmPin;

	public FormBikinPin(
			@NotBlank(message = "Pin harus diisi") @Pattern(regexp = "^[0-9]{6,6}$", message = "Pengisian pin harus 6 digit angka!") String pin,
			String confirmPin) {
		super();
		this.pin = pin;
		this.confirmPin = confirmPin;
	}

	public FormBikinPin() {
		super();
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getConfirmPin() {
		return confirmPin;
	}

	public void setConfirmPin(String confirmPin) {
		this.confirmPin = confirmPin;
	}
	
	
}
