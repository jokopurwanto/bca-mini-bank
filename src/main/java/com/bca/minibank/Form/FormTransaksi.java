package com.bca.minibank.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.bca.minibank.Model.ModelTransaksi;

public class FormTransaksi {
	private String noRek;
	
	@NotNull(message="Harap isi nominal setor")
	@Min(value=50000, message="Minimal Setor adalah 50000")
	private Integer nominal;
	
	public FormTransaksi() {
		
	}

	public FormTransaksi(String noRek) {
		super();
		this.noRek = noRek;
		this.nominal = nominal;
	}

	public FormTransaksi(ModelTransaksi modelTransaksi) {
		this.noRek = modelTransaksi.getNoRek();
		this.nominal = modelTransaksi.getNominal();
	}
	
	public String getNoRek() {
		return noRek;
	}
	public void setNoRek(String noRek) {
		this.noRek = noRek;
	}
	public Integer getNominal() {
		return nominal;
	}
	public void setNominal(Integer nominal) {
		this.nominal = nominal;
	
	}
}
