package com.bca.minibank.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_SETTING")
public class TbSetting {

	private String key;
	private String value;
	public TbSetting() {
		super();
	}
	public TbSetting(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Id
	@Column(name = "KEY", unique = true, nullable = false, length = 255)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "VALUE", nullable = false, length = 255)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
