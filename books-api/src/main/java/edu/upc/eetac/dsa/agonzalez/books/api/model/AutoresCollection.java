package edu.upc.eetac.dsa.agonzalez.books.api.model;

import java.util.ArrayList;
import java.util.List;


import edu.upc.eetac.dsa.agonzalez.books.api.model.Autor;

public class AutoresCollection {
	
	private List<Autor> autores;
	
	public AutoresCollection() {
		super();
		autores = new ArrayList<>();
	}
	
	public List<Autor> setAutor() {
		return autores;
	}
	public List<Autor> getAutor() {
		return autores;
	}
	
	public void getAutor(List<Autor> autores){
		this.autores = autores;
	}
	public void setAutor(List<Autor> autores){
		this.autores = autores;
	}
 
	public void addAutor(Autor autor) {
		autores.add(autor);
	}

}

