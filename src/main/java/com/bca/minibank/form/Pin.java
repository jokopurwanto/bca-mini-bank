package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class Pin {
	
	
	@Pattern(regexp = "^[0-9]{6,6}$", message = "Pengisian pin harus 6 digit angka!")
	private String newPin;
	private String oldPin;
	private String confirmpin;
	
	public Pin() {
		super();
	}

	public Pin(
			@NotBlank(message = "Pin tidak boleh kosong") @Pattern(regexp = "^[0-9]{6,6}$", message = "Pengisian pin harus 6 digit angka!") String newPin,
			String oldPin, String confirmpin) {
		super();
		this.newPin = newPin;
		this.oldPin = oldPin;
		this.confirmpin = confirmpin;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	public String getOldPin() {
		return oldPin;
	}

	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}

	public String getConfirmpin() {
		return confirmpin;
	}

	public void setConfirmpin(String confirmpin) {
		this.confirmpin = confirmpin;
	}
	
}
