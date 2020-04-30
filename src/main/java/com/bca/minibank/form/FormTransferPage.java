package com.bca.minibank.form;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.springframework.lang.NonNull;

import com.bca.minibank.model.ModelTransferPage;

public class FormTransferPage {

	private String noRek;
	@Pattern(regexp="(^[0-9]{6})", message = "Inputan harus berupa angka dan max 10 angka")
	private String noRekTujuan;
	private int nominal;
	private String keterangan;
	
	public FormTransferPage(ModelTransferPage modelTransferPage) {
		if(modelTransferPage != null) {
			this.noRek = modelTransferPage.getNoRek();
			this.noRekTujuan = modelTransferPage.getNoRekTujuan();
			this.nominal = modelTransferPage.getNominal();
			this.keterangan = modelTransferPage.getKeterangan();
		}
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
