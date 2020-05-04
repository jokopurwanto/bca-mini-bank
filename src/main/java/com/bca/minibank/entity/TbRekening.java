package com.bca.minibank.entity;
// Generated Apr 24, 2020 11:11:46 AM by Hibernate Tools 5.1.10.Final

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * TbRekening generated by hbm2java
 */
@Entity
@Table(name = "TB_REKENING")
public class TbRekening implements java.io.Serializable {

	private String noRek;
	
	@OneToOne
	@JoinColumn(name="ID_USER")
	private TbUsers tbUsers;
	private TbJnsTab tbJnsTab;
	private String noKartu;
	private String pin;
	private double saldo;
	private String statusRek;
	private int transaksiHarian;
	private Set<TbTransaksi> tbTransaksis = new HashSet<TbTransaksi>(0);

	public TbRekening() {
	}

	
	public TbRekening(String noRek, TbUsers tbUsers, TbJnsTab tbJnsTab, String noKartu, double saldo,
			String statusRek) {
		this.noRek = noRek;
		this.tbUsers = tbUsers;
		this.tbJnsTab = tbJnsTab;
		this.noKartu = noKartu;
		this.saldo = saldo;
		this.statusRek = statusRek;
	}

	public TbRekening(String noRek, TbUsers tbUsers, TbJnsTab tbJnsTab, String noKartu, String pin, double saldo,
			String statusRek, Set<TbTransaksi> tbTransaksis) {
		this.noRek = noRek;
		this.tbUsers = tbUsers;
		this.tbJnsTab = tbJnsTab;
		this.noKartu = noKartu;
		this.pin = pin;
		this.saldo = saldo;
		this.statusRek = statusRek;
		this.tbTransaksis = tbTransaksis;
	}

	@Id
	@Column(name = "NO_REK", unique = true, nullable = false, length = 10)
	public String getNoRek() {
		return this.noRek;
	}

	public void setNoRek(String noRek) {
		this.noRek = noRek;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USER", nullable = false)
	public TbUsers getTbUsers() {
		return this.tbUsers;
	}

	public void setTbUsers(TbUsers tbUsers) {
		this.tbUsers = tbUsers;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_JNS_TAB", nullable = false)
	public TbJnsTab getTbJnsTab() {
		return this.tbJnsTab;
	}

	public void setTbJnsTab(TbJnsTab tbJnsTab) {
		this.tbJnsTab = tbJnsTab;
	}

	@Column(name = "NO_KARTU", nullable = false, length = 12)
	public String getNoKartu() {
		return this.noKartu;
	}

	public void setNoKartu(String noKartu) {
		this.noKartu = noKartu;
	}

	@Column(name = "PIN", length = 6)
	public String getPin() {
		return this.pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Column(name = "SALDO", nullable = false, precision = 126, scale = 0)
	public double getSaldo() {
		return this.saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	@Column(name = "STATUS_REK", nullable = false, length = 10)
	public String getStatusRek() {
		return this.statusRek;
	}

	public void setStatusRek(String statusRek) {
		this.statusRek = statusRek;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tbRekening")
	public Set<TbTransaksi> getTbTransaksis() {
		return this.tbTransaksis;
	}

	public void setTbTransaksis(Set<TbTransaksi> tbTransaksis) {
		this.tbTransaksis = tbTransaksis;
	}

	@Column(name = "TRANSAKSI_HARIAN", nullable = false, length = 11)
	public int getTransaksiHarian() {
		return transaksiHarian;
	}


	public void setTransaksiHarian(int transaksiHarian) {
		this.transaksiHarian = transaksiHarian;
	}
	
	

}
