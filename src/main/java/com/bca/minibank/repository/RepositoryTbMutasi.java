package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbMutasi;

@Repository
public interface RepositoryTbMutasi extends JpaRepository<TbMutasi, Integer>{
	
}
