package com.bca.minibank.model;

import com.bca.minibank.form.FormTransferPage;

public class ModelTransferPage {
	
	private String noRek;
	private String noRekTujuan;
	private int nominal;
	private String keterangan;
	
	public ModelTransferPage(FormTransferPage formTransferPage) {
		this.noRek = formTransferPage.getNoRek();
		this.keterangan = formTransferPage.getKeterangan();
		this.nominal = formTransferPage.getNominal();
		this.noRekTujuan = formTransferPage.getNoRekTujuan();
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
	public int getNominal() {
		return nominal;
	}
	public void setNominal(int nominal) {
		this.nominal = nominal;
	}
	public String getKeterangan() {
		return keterangan;
	}
	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}
	
	

}
