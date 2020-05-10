package com.bca.minibank.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbTransaksi;

@Repository
public interface RepositoryTbMutasi extends JpaRepository<TbMutasi, Integer>{
	
    @Query("SELECT b FROM TbTransaksi a, TbMutasi b WHERE (a.noRekTujuan= ?1 AND TRUNC(a.tglTransaksi) BETWEEN TO_DATE(?2) AND TO_DATE(?3)) AND (a.statusTransaksi='SUCCESS') AND (b.tbTransaksi = a.idTransaksi AND 'UANG MASUK' = b.jnsMutasi) order by a.tglTransaksi desc")
    List<TbMutasi> findByFilterTransaksiIn(String noRek, String startDate, String endDate);
    
    @Query("SELECT b FROM TbTransaksi a, TbMutasi b WHERE (a.tbRekening.noRek= ?1 AND TRUNC(a.tglTransaksi) BETWEEN TO_DATE(?2) AND TO_DATE(?3)) AND (b.tbTransaksi = a.idTransaksi AND 'UANG KELUAR' = b.jnsMutasi) order by a.tglTransaksi desc")
    List<TbMutasi> findByFilterTransaksiOut(String noRek, String startDate, String endDate);
	
}
