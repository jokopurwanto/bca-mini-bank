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
	
	public void add(TbRekening tbRekening) {
		this.repositoryTbRekening.save(tbRekening);
	}
	
	public void delete(String noRek) {
		this.repositoryTbRekening.deleteById(noRek);
	}
	
	public void update(String NoRek, TbRekening newTbRekening) {
		TbRekening TbRekeningTemp = this.repositoryTbRekening.getOne(NoRek);
		TbRekeningTemp.setTbJnsTab(newTbRekening.getTbJnsTab());
		TbRekeningTemp.setPin(newTbRekening.getPin());
		TbRekeningTemp.setSaldo(newTbRekening.getSaldo());
	}
	public TbRekening noRek(String noRek) {
		return this.repositoryTbRekening.findByNoRek(noRek);
		
	}
	
}
