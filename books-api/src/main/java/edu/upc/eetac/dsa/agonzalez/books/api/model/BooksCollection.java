package edu.upc.eetac.dsa.agonzalez.books.api.model;

import java.util.ArrayList;
import java.util.List;


import edu.upc.eetac.dsa.agonzalez.books.api.model.Book;

public class BooksCollection {
	
	private List<Book> books;
	
	public BooksCollection() {
		super();
		books = new ArrayList<>();
	}
 
	public List<Book> getBooks() {
		return books;
	}
	
	public void getBooks(List<Book> books){
		this.books = books;
	}
	public void setBooks(List<Book> books){
		this.books = books;
	}
 
	public void addBooks(Book book) {
		books.add(book);
	}

}
