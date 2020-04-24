package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbRekening;

@Repository
public interface RepositoryTbRekening extends JpaRepository<TbRekening, String> {

}
