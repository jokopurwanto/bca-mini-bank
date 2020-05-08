package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbUsers;

@Service
@Transactional
public class DaoTbUsers {
	
	@Autowired
	private RepositoryTbUsers repositoryTbUsers;
	
	
	public TbUsers getOne(int idUser) {
		return this.repositoryTbUsers.getOne(idUser);
	}
	
	public List<TbUsers> getAll(){
		return  this.repositoryTbUsers.findAll();
	}
	
	public void add(TbUsers tbUsers) {
		this.repositoryTbUsers.save(tbUsers);
	}
	
	public void delete(int idUser) {
		this.repositoryTbUsers.deleteById(idUser);
	}
	
	public void update(TbUsers tbUsers) {
		this.repositoryTbUsers.save(tbUsers);
	}
	
	public TbUsers findByUsername(String username) {
		return  this.repositoryTbUsers.findByUsername(username);
	}

}
