package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbLogAdmin;

@Repository
public interface RepositoryTbLogAdmin extends JpaRepository<TbLogAdmin, Integer> {

}
