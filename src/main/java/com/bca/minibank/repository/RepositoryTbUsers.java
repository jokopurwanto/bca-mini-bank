package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbUsers;

@Repository
public interface RepositoryTbUsers extends JpaRepository<TbUsers, Integer> {

	
	TbUsers findByUsername(String username);
}
