package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbUsers;

@Repository
public interface RepositoryTbUsers extends JpaRepository<TbUsers, Integer> {
	
    @Query("SELECT a FROM TbUsers a WHERE a.username= ?1")
    TbUsers findByUsername(String username);

}
