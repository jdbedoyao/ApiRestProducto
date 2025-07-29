package com.jdbo.hm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jdbo.hm.entity.Producto;
import com.jdbo.hm.repository.ProductoRepository;
import com.jdbo.hm.services.ProductoServices;

@Service
public class ProductoServicesImpl implements ProductoServices {
    @Autowired
	private ProductoRepository pr;
	@Override
	public List<Producto> obtenerTodos() {
           
		return pr.findAll();
	}

	@Override
	public Producto obtenerporId(Long id) {
		return pr.findById(id).orElse(null);
	}

	@Override
	public Producto crearProducto(Producto cliente) {
		return pr.save(cliente);
	}

	@Override
	public Producto actualizarProducto(Long id, Producto producto) {
		Producto productoDB= pr.findById(id).orElse(null);
		if(productoDB!=null) {
			productoDB.setId(id);
			productoDB.setNombre(producto.getNombre());
			productoDB.setPrecio(producto.getPrecio());
			productoDB.setDescripcion(producto.getDescripcion());
			return pr.save(productoDB);			
		}
		return null;
	}


	@Override
	public long contarProducto() {
		return pr.count();
	}

}
