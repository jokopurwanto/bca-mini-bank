package com.bca.minibank.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbUsers;

@Repository
public interface RepositoryTbUsers extends JpaRepository<TbUsers, Integer> {

	List<TbUsers> findByStatusUserAndRole(String statusUser, String role);
	List<TbUsers> findByStatusUserAndRoleAndUsernameContainingIgnoreCase(String statusUser, String role, String username);
	
	
}
