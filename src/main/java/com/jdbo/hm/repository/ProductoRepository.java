package com.jdbo.hm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jdbo.hm.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {

}
