package edu.upc.eetac.dsa.agonzalez.books.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
import edu.upc.eetac.dsa.agonzalez.books.api.model.BooksError;
 
@Provider
public class WebApplicationExceptionsMapper implements
		ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException exception) {
		BooksError error = new BooksError(
				exception.getResponse().getStatus(), exception.getMessage());
		return Response.status(error.getStatus()).entity(error)
				.type(MediaType.BOOKS_API_ERROR).build();
	}
 
}
