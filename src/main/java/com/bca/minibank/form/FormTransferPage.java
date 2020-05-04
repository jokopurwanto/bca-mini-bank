package com.bca.minibank.form;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.lang.NonNull;

import com.bca.minibank.model.ModelTransferPage;

public class FormTransferPage {

	@Pattern(regexp="(^[0-9]{6})", message = "No Rek harus berupa angka dan min 10 digit")
	private String noRek;
	
	@Pattern(regexp="(^[0-9]{6})", message = "No Rek tujuan tidak boleh kosong & berupa angka, min 10 digit")
	private String noRekTujuan;
	
	@Pattern(regexp="([0-9]+)", message = "nominal tidak boleh kosong & berupa angka")
	@Min(value=10000, message="Minimum transfer 10000")
	private String nominal;
	
	@NotEmpty(message = "Keterangan tidak boleh kosong")
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
	public String getNominal() {
		return nominal;
	}
	public void setNominal(String nominal) {
		this.nominal = nominal;
	}
	public String getKeterangan() {
		return keterangan;
	}
	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}
	
	
	
}
