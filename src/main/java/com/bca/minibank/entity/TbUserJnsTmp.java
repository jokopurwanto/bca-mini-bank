//package com.bca.minibank.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "TB_USER_JNS_TMP")
//public class TbUserJnsTmp implements java.io.Serializable{
//	
//	@Id
//	@JoinColumn(name = "ID_USER")
//	@Column(name = "ID_USER", nullable = false)
//	@OneToOne(fetch = FetchType.LAZY)
//	private int idUser;
//	
//	@JoinColumn(name = "ID_JNS_TAB")
//	@Column(name = "ID_JNS_TAB", nullable = false)
//	@ManyToOne(fetch = FetchType.LAZY)
//	private int idJnsTab;
//
//	public TbUserJnsTmp() {
//	
//	}
//
//	public TbUserJnsTmp(int idUser, int idJnsTab) {
//		super();
//		this.idUser = idUser;
//		this.idJnsTab = idJnsTab;
//	}
//
//	public int getIdUser() {
//		return idUser;
//	}
//
//	public void setIdUser(int idUser) {
//		this.idUser = idUser;
//	}
//
//	public int getIdJnsTab() {
//		return idJnsTab;
//	}
//
//	public void setIdJnsTab(int idJnsTab) {
//		this.idJnsTab = idJnsTab;
//	}
//	
//}
