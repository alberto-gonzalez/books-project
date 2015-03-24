package edu.upc.eetac.dsa.agonzalez.books.api.model;

import java.util.ArrayList;
import java.util.List;
import edu.upc.eetac.dsa.agonzalez.books.api.model.Resena;

public class ResenaCollection {


	private List<Resena> resenas;
	
	public ResenaCollection() {
		super();
		resenas = new ArrayList<>();
	}
 
	public List<Resena> getResena() {
		return resenas;
	}
	
	public void getResena(List<Resena> resenas){
		this.resenas = resenas;
	}
	public void setResena(List<Resena> resenas){
		this.resenas = resenas;
	}
 
	public void addResena(Resena resena) {
		resenas.add(resena);
	}

}
