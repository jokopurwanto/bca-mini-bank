package com.bca.minibank.form;

import java.util.Date;

import javax.validation.constraints.Pattern;

public class FormTransferValidationPage {

	private String jnsTransaksi;
	private String tglTransaksi;	
	private String noRek;
	private String noRekTujuan;
	private String namaPenerima;
	private String keterangan;
	private String nominal;
	
	@Pattern(regexp="(^[0-9]{6})", message = "PIN harus berupa angka dan 6 digit")
	private String pin;
	
	public String getJnsTransaksi() {
		return jnsTransaksi;
	}
	public void setJnsTransaksi(String jnsTransaksi) {
		this.jnsTransaksi = jnsTransaksi;
	}
	public String getTglTransaksi() {
		return tglTransaksi;
	}
	public void setTglTransaksi(String tglTransaksi) {
		this.tglTransaksi = tglTransaksi;
	}
	public String getNoRek() {
		return noRek;
	}
	public void setNoRek(String noRek) {
		this.noRek = noRek;
	}
	public String getNoRekTujuan() {
		return noRekTujuan;
	}
	public void setNoRekTujuan(String noRekTujuan) {
		this.noRekTujuan = noRekTujuan;
	}
	public String getNamaPenerima() {
		return namaPenerima;
	}
	public void setNamaPenerima(String namaPenerima) {
		this.namaPenerima = namaPenerima;
	}
	public String getKeterangan() {
		return keterangan;
	}
	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}
	public String getNominal() {
		return nominal;
	}
	public void setNominal(String nominal) {
		this.nominal = nominal;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	
}
