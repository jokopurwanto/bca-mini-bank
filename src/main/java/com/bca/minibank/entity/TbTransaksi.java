package com.bca.minibank.entity;
// Generated Apr 24, 2020 11:11:46 AM by Hibernate Tools 5.1.10.Final

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * TbTransaksi generated by hbm2java
 */
@Entity
@Table(name = "TB_TRANSAKSI")
public class TbTransaksi implements java.io.Serializable {

	private int nominal;
	private int idTransaksi;
	private TbRekening tbRekening;
	private String jnsTransaksi;
	private String noRekTujuan;
	private Timestamp tglTransaksi;
	private String statusTransaksi;
	private String note;
	private Set<TbMutasi> tbMutasis = new HashSet<TbMutasi>(0);
	private Date tglPengajuan;

	public TbTransaksi() {
		
	}

	public TbTransaksi(int idTransaksi, TbRekening tbRekening, String jnsTransaksi, String noRekTujuan,
			Timestamp tglTransaksi, String statusTransaksi, String note, Set<TbMutasi> tbMutasis) {
		this.idTransaksi = idTransaksi;
		this.tbRekening = tbRekening;
		this.jnsTransaksi = jnsTransaksi;
		this.noRekTujuan = noRekTujuan;
		this.tglTransaksi = tglTransaksi;
		this.statusTransaksi = statusTransaksi;
		this.note = note;
		this.tbMutasis = tbMutasis;
		this.tglPengajuan = tglPengajuan;
	}

	@Column(name="NOMINAL",nullable=false, precision = 126, scale = 0)
	public int getNominal() {
		return nominal;
	}

	public void setNominal(int nominal) {
		this.nominal = nominal;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tb_transaksi_seq")
	@Column(name = "ID_TRANSAKSI", unique = true, nullable = false, precision = 22, scale = 0)
	public int getIdTransaksi() {
		return this.idTransaksi;
	}
	

	public void setIdTransaksi(int idTransaksi) {
		this.idTransaksi = idTransaksi;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NO_REK", nullable = false)
	public TbRekening getTbRekening() {
		return this.tbRekening;
	}

	public void setTbRekening(TbRekening tbRekening) {
		this.tbRekening = tbRekening;
	}

	@Column(name = "JNS_TRANSAKSI", nullable = false, length = 11)
	public String getJnsTransaksi() {
		return this.jnsTransaksi;
	}

	public void setJnsTransaksi(String jnsTransaksi) {
		this.jnsTransaksi = jnsTransaksi;
	}

	@Column(name = "NO_REK_TUJUAN", nullable = false, length = 10)
	public String getNoRekTujuan() {
		return this.noRekTujuan;
	}

	public void setNoRekTujuan(String noRekTujuan) {
		this.noRekTujuan = noRekTujuan;
	}


	@Column(name = "TGL_TRANSAKSI", nullable = true, length = 11)
	public Timestamp getTglTransaksi() {
		return this.tglTransaksi;
	}

	public void setTglTransaksi(Timestamp tglTransaksi) {
		this.tglTransaksi = tglTransaksi;
	}

	@Column(name = "STATUS_TRANSAKSI", nullable = false, length = 11)
	public String getStatusTransaksi() {
		return this.statusTransaksi;
	}

	public void setStatusTransaksi(String statusTransaksi) {
		this.statusTransaksi = statusTransaksi;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tbTransaksi")
	public Set<TbMutasi> getTbMutasis() {
		return this.tbMutasis;
	}

	public void setTbMutasis(Set<TbMutasi> tbMutasis) {
		this.tbMutasis = tbMutasis;
	}
	
	@Column(name = "NOTE", nullable = true)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TGL_PENGAJUAN", nullable = true, length = 11)
	public Date getTglPengajuan() {
		return this.tglPengajuan;
	}

	public void setTglPengajuan(Date tglPengajuan) {
		this.tglPengajuan = tglPengajuan;
	}

}
