package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbJnsTab;

@Repository
public interface RepositoryTbJnsTab extends JpaRepository<TbJnsTab, Integer>{

}
