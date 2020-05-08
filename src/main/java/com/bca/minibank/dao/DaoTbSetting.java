package com.bca.minibank.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.repository.RepositoryTbSetting;

@Service
@Transactional
public class DaoTbSetting {

	@Autowired
	private RepositoryTbSetting repositoryTbSetting;
	
	public String getValue(String key) {
		return this.repositoryTbSetting.getOne(key).getValue();
	}
}