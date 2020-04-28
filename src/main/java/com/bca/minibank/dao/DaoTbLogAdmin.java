package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbLogAdmin;
import com.bca.minibank.repository.RepositoryTbLogAdmin;

@Service
@Transactional
public class DaoTbLogAdmin {

	@Autowired
	private RepositoryTbLogAdmin repositoryTbLogAdmin;
	
	public TbLogAdmin getOne(int idLog) {
		return this.repositoryTbLogAdmin.getOne(idLog);
	}
	
	public List<TbLogAdmin> getAll(){
		return  this.repositoryTbLogAdmin.findAll();
	}

	public void add(TbLogAdmin tbLogAdmin) {
		this.repositoryTbLogAdmin.save(tbLogAdmin);
	}
	
	public void delete(int idLog) {
		this.repositoryTbLogAdmin.deleteById(idLog);
	}
	
	public void update(TbLogAdmin tbLogAdmin) {
		this.repositoryTbLogAdmin.save(tbLogAdmin);
	}
}
