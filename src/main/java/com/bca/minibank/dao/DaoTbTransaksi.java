package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.repository.RepostitoryTbTransaksi;

@Service
@Transactional
public class DaoTbTransaksi {
	
	@Autowired
	private RepostitoryTbTransaksi repostitoryTbTransaksi;
	
	public TbTransaksi getOne(int idTransaksi) {
		return this.repostitoryTbTransaksi.getOne(idTransaksi);
	}
	
	public List<TbTransaksi> getAll(){
		return  this.repostitoryTbTransaksi.findAll();
	}
	
	public List<TbTransaksi> getAllByTbRekening(TbRekening tbRekening){
		return  this.repostitoryTbTransaksi.findByTbRekening(tbRekening);
	}
	
	public void add(TbTransaksi tbTransaksi) {
		this.repostitoryTbTransaksi.save(tbTransaksi);
	}
	
	public void delete(int idTransaksi) {
		this.repostitoryTbTransaksi.deleteById(idTransaksi);
	}
	
	public void update(TbTransaksi tbTransaksi) {
		this.repostitoryTbTransaksi.save(tbTransaksi);
	}
}
