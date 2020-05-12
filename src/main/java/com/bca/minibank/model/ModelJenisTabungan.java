package com.bca.minibank.model;

public class ModelJenisTabungan {
	
	private String namaJnsTab;
	private String biayaAdmin;
	private String limitTransaksi;
	private String bunga;
	
	public ModelJenisTabungan() {
		
	}

	public String getNamaJnsTab() {
		return namaJnsTab;
	}

	public void setNamaJnsTab(String namaJnsTab) {
		this.namaJnsTab = namaJnsTab;
	}

	public String getBiayaAdmin() {
		return biayaAdmin;
	}

	public void setBiayaAdmin(String biayaAdmin) {
		this.biayaAdmin = biayaAdmin;
	}

	public String getLimitTransaksi() {
		return limitTransaksi;
	}

	public void setLimitTransaksi(String limitTransaksi) {
		this.limitTransaksi = limitTransaksi;
	}

	public String getBunga() {
		return bunga;
	}

	public void setBunga(String bunga) {
		this.bunga = bunga;
	}
	
	
	
}
