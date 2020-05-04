package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbTransaksi;

@Repository
public interface RepostitoryTbTransaksi extends JpaRepository<TbTransaksi, Integer> {
		
	TbTransaksi findByIdTransaksi(int id);
}
