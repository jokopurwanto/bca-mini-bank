package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.repository.RepositoryTbMutasi;

@Service
@Transactional
public class DaoTbMutasi {

	@Autowired
	private RepositoryTbMutasi repositoryTbMutasi;
	
	public TbMutasi getOne(int idMutasi) {
		return this.repositoryTbMutasi.getOne(idMutasi);
	}
	
	public List<TbMutasi> getAll(){
		return  this.repositoryTbMutasi.findAll();
	}
	
	public void add(TbMutasi TbMutasi) {
		this.repositoryTbMutasi.save(TbMutasi);
	}
	
	public void delete(int idMutasi) {
		this.repositoryTbMutasi.deleteById(idMutasi);
	}
	
	public void update(TbMutasi TbMutasi) {
		this.repositoryTbMutasi.save(TbMutasi);
	}
}
