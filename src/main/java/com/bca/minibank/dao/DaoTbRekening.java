package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.repository.RepositoryTbRekening;

@Repository
@Transactional
public class DaoTbRekening {

	@Autowired
	private RepositoryTbRekening repositoryTbRekening;
	
	public TbRekening getOne(String noRek) {
		return this.repositoryTbRekening.getOne(noRek);
	}
	
	public List<TbRekening> getAll(){
		return  this.repositoryTbRekening.findAll();
	}
	
	public List<TbRekening> getAllByStatusRek(String statusRek){
		return  this.repositoryTbRekening.findByStatusRek(statusRek);
	}
	
	public Boolean findById(String noRek) {
		return this.repositoryTbRekening.findById(noRek).isPresent();
	}
	
	public void add(TbRekening tbRekening) {
		this.repositoryTbRekening.save(tbRekening);
	}
	
	public void delete(String noRek) {
		this.repositoryTbRekening.deleteById(noRek);
	}
	
	public void update(TbRekening tbRekening) {
		this.repositoryTbRekening.save(tbRekening);
	}
	
	public void updateSaldo(String noRek, double saldo) {
		TbRekening tbRekening = this.getOne(noRek);
		tbRekening.setSaldo(saldo);
	}	
	public void updateStatusRek(String noRek, String statusRek) {
		TbRekening tbRekening = this.getOne(noRek);
		tbRekening.setStatusRek(statusRek);
	}	
}
