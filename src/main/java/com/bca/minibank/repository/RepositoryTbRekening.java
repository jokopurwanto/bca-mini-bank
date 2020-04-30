package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbRekening;

@Repository
public interface RepositoryTbRekening extends JpaRepository<TbRekening, String> {
	
    @Query("SELECT a FROM TbRekening a WHERE a.noRek= ?1")
    TbRekening findByNoRek(String noRek);
}
