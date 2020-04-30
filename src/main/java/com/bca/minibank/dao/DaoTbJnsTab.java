package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbJnsTab;
import com.bca.minibank.repository.RepositoryTbJnsTab;

@Service
@Transactional
public class DaoTbJnsTab {

	@Autowired
	private RepositoryTbJnsTab repositoryTbJnsTab;
	
	public TbJnsTab getOne(int idJnsTab) {
		return this.repositoryTbJnsTab.getOne(idJnsTab);
	}
	
	public List<TbJnsTab> getAll(){
		return  this.repositoryTbJnsTab.findAll();
	}
	
	public void add(TbJnsTab TbJnsTab) {
		this.repositoryTbJnsTab.save(TbJnsTab);
	}
	
	public void delete(int idJnsTab) {
		this.repositoryTbJnsTab.deleteById(idJnsTab);
	}
	
	public void update(TbJnsTab TbJnsTab) {
		this.repositoryTbJnsTab.save(TbJnsTab);
	}
}
