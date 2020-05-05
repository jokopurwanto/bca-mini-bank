package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbUsers;

@Repository
public interface RepositoryTbUsers extends JpaRepository<TbUsers, Integer> {

	public TbUsers findByUsername(String username);
	public TbUsers findByEmail(String email);
	public TbUsers findByNoKtp(String noKtp);
	public TbUsers findByNoHp(String noHp);
}
