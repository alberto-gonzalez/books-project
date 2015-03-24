package edu.upc.eetac.dsa.agonzalez.books.api.model;

public class Book {

	private int id;
	private String titulo;
	private String lengua;
	private String edicion;
	private String fecha_edicion;
	private String fecha_impresion;
	private String editorial;
	private long LastModified;
	private String username;

	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getLastModified() {
		return LastModified;
	}
	public void setLastModified(long lastModified) {
		LastModified = lastModified;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getLengua() {
		return lengua;
	}
	public void setLengua(String lengua) {
		this.lengua = lengua;
	}
	public String getEdicion() {
		return edicion;
	}
	public void setEdicion(String edicion) {
		this.edicion = edicion;
	}
	public String getFecha_edicion() {
		return fecha_edicion;
	}
	public void setFecha_edicion(String fecha_edicion) {
		this.fecha_edicion = fecha_edicion;
	}
	public String getFecha_impresion() {
		return fecha_impresion;
	}
	public void setFecha_impresion(String fecha_impresion) {
		this.fecha_impresion = fecha_impresion;
	}
	public String getEditorial() {
		return editorial;
	}
	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

}
