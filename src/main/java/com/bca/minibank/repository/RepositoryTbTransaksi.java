package com.bca.minibank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;

@Repository
public interface RepositoryTbTransaksi extends JpaRepository<TbTransaksi, Integer> {

	@Query("Select a from TbTransaksi a WHERE a.noRekTujuan=?1")
	 List<TbTransaksi> findByNoRekTujuan(String noRekTujuan);
	
	 @Query("SELECT a from TbTransaksi a where a.noRekTujuan =:noRekTujuan AND a.jnsTransaksi = :jenis")
	   List<TbTransaksi> findByNoRekTujuanANDJnsTransaksi(@Param("noRekTujuan") String noRekTujuan, @Param("jenis")String jenis);
	 
	 	List<TbTransaksi> findByTbRekeningOrderByIdTransaksi(TbRekening tbRekening);
		List<TbTransaksi> findByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(String jnsTransaksi, String statusTransaksi);
		List<TbTransaksi> findByJnsTransaksiAndStatusTransaksiAndTbRekeningOrderByIdTransaksi(String jnsTransaksi, String statusTransaksi, TbRekening tbRekening);
}
