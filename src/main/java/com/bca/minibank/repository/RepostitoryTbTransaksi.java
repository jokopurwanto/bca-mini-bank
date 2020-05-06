package com.bca.minibank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bca.minibank.entity.TbMutasi;
import com.bca.minibank.entity.TbTransaksi;

@Repository
public interface RepostitoryTbTransaksi extends JpaRepository<TbTransaksi, Integer> {
		
    @Query("SELECT a,b FROM TbTransaksi a, TbMutasi b WHERE (a.noRekTujuan= ?1 AND TRUNC(a.tglTransaksi) BETWEEN TO_DATE(?2) AND TO_DATE(?3)) AND (b.tbTransaksi = a.idTransaksi AND 'UANG MASUK' = b.jnsMutasi) order by a.tglTransaksi desc")
    List<TbTransaksi> findByFilterTransaksiIn(String noRek, String startDate, String endtDate);
    
    @Query("SELECT a FROM TbTransaksi a, TbMutasi b WHERE (a.tbRekening= ?1 AND TRUNC(a.tglTransaksi) BETWEEN TO_DATE(?2) AND TO_DATE(?3)) AND (b.tbTransaksi = a.idTransaksi AND 'UANG KELUAR' = b.jnsMutasi) order by a.tglTransaksi desc")
    List<TbTransaksi> findByFilterTransaksiOut(String noRek, String startDate, String endtDate);

}
