package com.jdbo.hm.services;

import java.util.List;

import com.jdbo.hm.entity.Producto;

public interface ProductoServices {
	
	List<Producto> obtenerTodos();
	
	Producto obtenerporId(Long id);
	
	Producto crearProducto(Producto cliente);
	
	Producto actualizarProducto(Long id , Producto cliente);
	
	long contarProducto();
}
