package com.bca.minibank.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class FormRegisterUser {
	@NotBlank(message = "Username harus diisi!")
	@Pattern(regexp = "^(?![0-9_.-])[A-Za-z0-9_.-]{6,12}$", message = "Username tidak invalid, silahkan cek ketentuan pengisian username!")
	private String username;
	
	@Size(min = 8, max = 32, message = "Rentang pengisian password harus 8 sampai 32 digit!")
	@Pattern(regexp = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{0,32})", message = "Harus mengandung angka, huruf kapital dan huruf kecil!")
	@NotBlank(message = "Password harus diisi!")
	private String password;
	
	private String confirmPassword;
	
	@NotBlank(message = "Nama harus diisi!")
	@Pattern(regexp = "(^[a-zA-Z.' ]+$)", message = "Nama hanya mengandung huruf kapital, huruf kecil, dan simbol .'")
	@Size(max = 30, message = "Maksimal 30 Karakter!")
	private String nama;
	
	@NotBlank(message = "Alamat harus diisi!")
	@Size(max = 255, message = "Maksimal 255 karakter!")
	private String alamat;
	
	@NotBlank(message = "No. HP harus diisi")
	@Size(min = 10, max = 13, message = "Rentang pengisian No. HP harus 10 sampai 13 digit!")
	@Pattern(regexp = "^[0-9]+$", message = "Inputan harus Angka!")
	private String noHp;
	
	@NotBlank(message = "No. KTP harus diisi")
	@Size(min = 16, max = 16, message = "Pengisian No. KTP harus 16 digit!")
	@Pattern(regexp = "^[0-9]+$", message = "Inputan Harus Angka!")
	private String noKtp;
	
	@NotBlank(message = "Email harus diisi")
	@Size(max = 30, message = "Maksimal 30 Karakter!")
	@Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Pengisian email tidak valid!")
	private String email;
	

	private int idJnsTab;
	
	public FormRegisterUser() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getNama() {
		return nama;
	}

	public void setNama(String nama) {
		this.nama = nama;
	}

	public String getAlamat() {
		return alamat;
	}

	public void setAlamat(String alamat) {
		this.alamat = alamat;
	}

	public String getNoHp() {
		return noHp;
	}

	public void setNoHp(String noHp) {
		this.noHp = noHp;
	}

	public String getNoKtp() {
		return noKtp;
	}

	public void setNoKtp(String noKtp) {
		this.noKtp = noKtp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIdJnsTab() {
		return idJnsTab;
	}

	public void setIdJnsTab(int idJnsTab) {
		this.idJnsTab = idJnsTab;
	}
}
