package com.bca.minibank.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TB_USER_JNS_TMP")
public class TbUserJnsTmp implements java.io.Serializable{
	
	@Id
	@Column(name = "ID_TMP")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ID_TMP_SEQ")
	private int idTmp;
	
	@JoinColumn(name = "ID_USER")
	@OneToOne(fetch = FetchType.LAZY)
	private TbUsers tbUsers;
	
	@JoinColumn(name = "ID_JNS_TAB")
	@ManyToOne(fetch = FetchType.LAZY)
	private TbJnsTab tbJnsTab;

	public TbUserJnsTmp() {
		super();
	}

	public TbUserJnsTmp(int idTmp, TbUsers tbUsers, TbJnsTab tbJnsTab) {
		super();
		this.idTmp = idTmp;
		this.tbUsers = tbUsers;
		this.tbJnsTab = tbJnsTab;
	}

	public int getIdTmp() {
		return idTmp;
	}

	public void setIdTmp(int idTmp) {
		this.idTmp = idTmp;
	}

	public TbUsers getTbUsers() {
		return tbUsers;
	}

	public void setTbUsers(TbUsers tbUsers) {
		this.tbUsers = tbUsers;
	}

	public TbJnsTab getTbJnsTab() {
		return tbJnsTab;
	}

	public void setTbJnsTab(TbJnsTab tbJnsTab) {
		this.tbJnsTab = tbJnsTab;
	}

}
