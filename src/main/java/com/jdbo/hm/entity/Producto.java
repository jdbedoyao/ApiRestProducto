package com.jdbo.hm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="producto")
public class Producto {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String nombre;
 private int precio;
 private String descripcion;
 
 public Producto() {
	super();
 }

 
 
 public Producto(Long id, String nombre, int precio, String descripcion) {
	super();
	this.id = id;
	this.nombre = nombre;
	this.precio = precio;
	this.descripcion = descripcion;
}



 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public String getNombre() {
	return nombre;
 }

 public void setNombre(String nombre) {
	this.nombre = nombre;
 }

 public int getPrecio() {
	return precio;
 }

 public void setPrecio(int precio) {
	this.precio = precio;
 }

 public String getDescripcion() {
	return descripcion;
 }

 public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
 }

 
 
}
