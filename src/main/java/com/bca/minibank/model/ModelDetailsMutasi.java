package com.bca.minibank.model;

import java.util.Date;

public class ModelDetailsMutasi {

	private Date tglTransaksi;
	private String note;
	private String jnsTransaksi;
	private int nominal;
	private double saldoAkhir;
	
	public ModelDetailsMutasi() {
		super();
	}

	public ModelDetailsMutasi(Date tglTransaksi, String note, String jnsTransaksi, int nominal, double saldoAkhir) {
		super();
		this.tglTransaksi = tglTransaksi;
		this.note = note;
		this.jnsTransaksi = jnsTransaksi;
		this.nominal = nominal;
		this.saldoAkhir = saldoAkhir;
	}

	public Date getTglTransaksi() {
		return tglTransaksi;
	}

	public void setTglTransaksi(Date tglTransaksi) {
		this.tglTransaksi = tglTransaksi;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getJnsTransaksi() {
		return jnsTransaksi;
	}

	public void setJnsTransaksi(String jnsTransaksi) {
		this.jnsTransaksi = jnsTransaksi;
	}

	public int getNominal() {
		return nominal;
	}

	public void setNominal(int nominal) {
		this.nominal = nominal;
	}

	public double getSaldoAkhir() {
		return saldoAkhir;
	}

	public void setSaldoAkhir(double saldoAkhir) {
		this.saldoAkhir = saldoAkhir;
	}
}
