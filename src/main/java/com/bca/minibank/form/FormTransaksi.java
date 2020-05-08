package com.bca.minibank.form;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.bca.minibank.Model.ModelTransaksi;
import com.bca.minibank.entity.TbTransaksi;

/**
 * TbTransaksi generated by hbm2java
 */

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
