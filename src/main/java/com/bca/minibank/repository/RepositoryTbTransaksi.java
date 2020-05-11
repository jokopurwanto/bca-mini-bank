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
		
		@Query("SELECT a from TbTransaksi a where  a.statusTransaksi = :statusTransaksi AND a.noRekTujuan =:noRekTujuan AND a.jnsTransaksi = :jnsTransaksi ")
		List<TbTransaksi> findByNoRekTujuanANDJnsTransaksiANDStatusTransaksi(@Param("statusTransaksi")String status,@Param("noRekTujuan") String noRekTujuan, @Param("jnsTransaksi")String jnsTransaksi);
	 
		List<TbTransaksi>findTop7ByStatusTransaksiAndNoRekTujuanAndJnsTransaksiOrderByTglTransaksi(String statusTransaksi,String noRekTujuan,String jnsTransaksi);
	 	List<TbTransaksi> findByTbRekening(TbRekening tbRekening);
		List<TbTransaksi> findByJnsTransaksiAndStatusTransaksi(String jnsTransaksi, String statusTransaksi);
		List<TbTransaksi> findByJnsTransaksiAndStatusTransaksiAndTbRekening(String jnsTransaksi, String statusTransaksi, TbRekening tbRekening);

		List<TbTransaksi> findByTbRekeningOrderByIdTransaksi(TbRekening tbRekening);
		List<TbTransaksi> findByJnsTransaksiAndStatusTransaksiOrderByIdTransaksi(String jnsTransaksi, String statusTransaksi);
		List<TbTransaksi> findByJnsTransaksiAndStatusTransaksiAndTbRekeningOrderByIdTransaksi(String jnsTransaksi, String statusTransaksi, TbRekening tbRekening);

}
