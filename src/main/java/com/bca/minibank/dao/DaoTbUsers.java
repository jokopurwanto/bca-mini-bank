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

	public List<TbUsers> getUsersByStatusAndRole(String statusUser, String role){ 
		return this.repositoryTbUsers.findByStatusUserAndRole(statusUser, role);
	}
	
	public List<TbUsers> getUsersByStatusAndRoleAndUsernameContainingIgnoreCase(String statusUser, String role, String username){ 
		return this.repositoryTbUsers.findByStatusUserAndRoleAndUsernameContainingIgnoreCase(statusUser, role, username);
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
	
	public void updatePassword(int idUser, String newPassword) {
		TbUsers user = this.repositoryTbUsers.getOne(idUser);
		user.setPassword(newPassword);
	}

	public void updateStatusUser(int idUser, String statusUser) {
		TbUsers user = this.repositoryTbUsers.getOne(idUser);
		user.setStatusUser(statusUser);
	}
	public void updateKeterangan(int idUser, String keterangan) {
		TbUsers user = this.repositoryTbUsers.getOne(idUser);
		user.setKeterangan(keterangan);
	}
	
	public TbUsers findByUsername(String username) {
		return  this.repositoryTbUsers.findByUsername(username);
	}
}
