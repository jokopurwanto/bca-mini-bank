package com.bca.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbSetting;

@Repository
public interface RepositoryTbSetting extends JpaRepository<TbSetting, String> {

}