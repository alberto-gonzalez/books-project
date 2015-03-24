package edu.upc.eetac.dsa.agonzalez.books.api;

public interface MediaType {
	public final static String BOOKS_API_USER = "application/vnd.books.api.user+json";
	public final static String BOOKS_API_USER_COLLECTION = "application/vnd.books.api.user.collection+json";
	public final static String BOOKS_API_Books = "application/vnd.books.api.book+json";
	public final static String BOOKS_API_Books_COLLECTION = "application/vnd.books.api.book.collection+json";
	public final static String BOOKS_API_Autores = "application/vnd.books.api.autor+json";
	public final static String BOOKS_API_Autores_COLLECTION = "application/vnd.books.api.autor.collection+json";
	public final static String BOOKS_API_Resenas = "application/vnd.books.api.resenas+json";
	public final static String BOOKS_API_Resenas_COLLECTION = "application/vnd.books.api.resenas.collection+json";
	public final static String BOOKS_API_ERROR = "application/vnd.dsa.book.error+json";

}
