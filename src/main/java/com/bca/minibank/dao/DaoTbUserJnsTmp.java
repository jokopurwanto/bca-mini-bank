package com.bca.minibank.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbUserJnsTmp;

@Service
@Transactional
public class DaoTbUserJnsTmp {
		
	@Autowired
	private RepositoryTbUserJnsTmp RepositoryTbUserJnsTmp;
	

	public TbUserJnsTmp getOne(int idTmp) {
		return RepositoryTbUserJnsTmp.getOne(idTmp);
	}
	
	public List<TbUserJnsTmp> getAll(){
		return  RepositoryTbUserJnsTmp.findAll();
	}
	
	public void add(TbUserJnsTmp TbUserJnsTmp) {
		RepositoryTbUserJnsTmp.save(TbUserJnsTmp);
	}
	
	public void delete(int idTmp) {
		RepositoryTbUserJnsTmp.deleteById(idTmp);
	}
	
	public void update(int idTmp, TbUserJnsTmp newTbUserJnsTmp) {
		TbUserJnsTmp TbUserJnsTmpTemp = RepositoryTbUserJnsTmp.getOne(idTmp);
		TbUserJnsTmpTemp.setTbJnsTab(newTbUserJnsTmp.getTbJnsTab());
		TbUserJnsTmpTemp.setTbUsers(newTbUserJnsTmp.getTbUsers());
	}
	
	public TbUserJnsTmp getOneByTbUsers(TbUsers tbUsers) {
		return RepositoryTbUserJnsTmp.findByTbUsers(tbUsers);
	}
}
