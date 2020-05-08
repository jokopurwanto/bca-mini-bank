package com.bca.minibank.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bca.minibank.entity.TbRekening;
import com.bca.minibank.entity.TbTransaksi;
import com.bca.minibank.entity.TbUserJnsTmp;
import com.bca.minibank.entity.TbUsers;

@Repository
public interface RepositoryTbUserJnsTmp extends JpaRepository<TbUserJnsTmp, Integer>  {
	public TbUserJnsTmp findByTbUsers(TbUsers tbUsers);
}