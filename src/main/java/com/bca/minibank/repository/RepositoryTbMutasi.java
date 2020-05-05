package com.bca.minibank.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbMutasi;

@Repository
public interface RepositoryTbMutasi extends JpaRepository<TbMutasi, Integer>{
	
    @Query("SELECT a FROM TbMutasi a WHERE a.noRek= ?1 order by a.tglMutasi desc")
    List<TbMutasi> findBySemua(String noRek);
    
//    @Query("SELECT a FROM TbMutasi a WHERE TRUNC(a.tglMutasi) = TO_DATE(?1) order by a.tglMutasi desc")
//    List<TbMutasi> findByPeriodeDay(String startDate1);
    
    @Query("SELECT a FROM TbMutasi a WHERE TRUNC(a.tglMutasi) = TO_DATE(?1) order by a.tglMutasi desc")
    List<TbMutasi> findByPeriodeDay(String startDate);
    
//    @Query("SELECT a FROM TbMutasi a WHERE a.noRek= ?1 AND TRUNC(a.tglMutasi) BETWEEN TO_DATE(?2) AND TO_DATE(?3) order by a.tglMutasi desc")
//    List<TbMutasi> findByPeriode(String noRek, String startDate, String endtDate);
    
//    @Query("SELECT a FROM TbMutasi a WHERE a.noRek= ?1 AND a.jnsMutasi= ?2 AND TRUNC(a.tglMutasi) BETWEEN TO_DATE(?3) AND TO_DATE(?4) order by a.tglMutasi desc")
//    List<TbMutasi> findByPeriode(String noRek, String jnsMutasi, String startDate, String endtDate);
    
//    filter by semua transaksi
    @Query("SELECT a FROM TbMutasi a WHERE a.noRek= ?1 AND TRUNC(a.tglMutasi) BETWEEN TO_DATE(?2) AND TO_DATE(?3) order by a.tglMutasi desc")
    List<TbMutasi> findByAllTransaksi(String noRek, String startDate, String endtDate);
    
//  filter by uang masuk dan uang keluar  
    @Query("SELECT a FROM TbMutasi a WHERE a.noRek= ?1 AND a.jnsMutasi= ?2 AND TRUNC(a.tglMutasi) BETWEEN TO_DATE(?3) AND TO_DATE(?4) order by a.tglMutasi desc")
    List<TbMutasi> findByFilterTransaksi(String noRek, String jnsMutasi, String startDate, String endtDate);
	
}
