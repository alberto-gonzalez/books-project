package edu.upc.eetac.dsa.agonzalez.books.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.mysql.jdbc.Statement;

import edu.upc.eetac.dsa.agonzalez.books.api.model.Book;
import edu.upc.eetac.dsa.agonzalez.books.api.model.BooksCollection;
import edu.upc.eetac.dsa.agonzalez.books.api.model.Resena;
import edu.upc.eetac.dsa.agonzalez.books.api.model.ResenaCollection;


 
@Path("/books")
public class BooksResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	@Context
	private SecurityContext security;

	private String GET_STINGS_QUERY = "select *from libro";
	 
	@GET
	@Produces(MediaType.BOOKS_API_Books_COLLECTION)
	public BooksCollection getBooks() {
		BooksCollection books = new BooksCollection();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_STINGS_QUERY);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Book book = new Book();
				book.setId(rs.getInt("id"));
				book.setTitulo(rs.getString("titulo"));
				book.setLengua(rs.getString("lengua"));
				book.setEdicion(rs.getString("edicion"));
				book.setFecha_edicion(rs.getString("fecha_edicion"));
				book.setFecha_impresion(rs.getString("fecha_impresion"));
				book.setEditorial(rs.getString("editorial"));
				books.addBooks(book);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return books;
	}
	private Book getBookFromDatabase(String id) {
		Book book = new Book();
		 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOK_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(id));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				book.setId(rs.getInt("id"));
				book.setTitulo(rs.getString("titulo"));
				book.setLengua(rs.getString("lengua"));
				book.setEdicion(rs.getString("edicion"));
				book.setFecha_edicion(rs.getString("fecha_edicion"));
				book.setFecha_impresion(rs.getString("fecha_impresion"));
				book.setEditorial(rs.getString("editorial"));
			}else {
				throw new NotFoundException("There's no libro with id="
						+ id);
						}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;
	}
	
	private String GET_BOOK_BY_ID_QUERY = "select id,titulo,lengua,edicion,fecha_edicion, fecha_impresion,editorial from libro where id=?";
	 
		@GET
		@Path("/{id}")
		@Produces(MediaType.BOOKS_API_Books)
		public Response getSBooks(@PathParam("id") String id,
				@Context Request request) {
			// Create CacheControl
			CacheControl cc = new CacheControl();
		 
			Book sting = getBookFromDatabase(id);
		 
			// Calculate the ETag on last modified date of user resource
			EntityTag eTag = new EntityTag(Long.toString(sting.getLastModified()));
		 
			// Verify if it matched with etag available in http request
			Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);
		 
			// If ETag matches the rb will be non-null;
			// Use the rb to return the response without any further processing
			if (rb != null) {
				return rb.cacheControl(cc).tag(eTag).build();
			}
		 
			// If rb is null then either it is first time request; or resource is
			// modified
			// Get the updated representation and return with Etag attached to it
			rb = Response.ok(sting).cacheControl(cc).tag(eTag);
		 
			return rb.build();
		}
		/*Book book = new Book();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOK_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(id));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				book.setId(rs.getInt("id"));
				book.setTitulo(rs.getString("titulo"));
				book.setLengua(rs.getString("lengua"));
				book.setEdicion(rs.getString("edicion"));
				book.setFecha_edicion(rs.getString("fecha_edicion"));
				book.setFecha_impresion(rs.getString("fecha_impresion"));
				book.setEditorial(rs.getString("editorial"));
			}else {
				throw new NotFoundException("There's no libro with id="
						+ id);
						}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;*/

	private void validateBook(Book book) {
		if (book.getTitulo() == null)
			throw new BadRequestException("Titulo can't be null.");
		if (book.getLengua() == null)
			throw new BadRequestException("lengua can't be null.");
		if (book.getEdicion()== null)
			throw new BadRequestException("edicion can't be greater null.");
		
	}
	
	private String INSERT_BOOK_QUERY = "insert into libro (titulo, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (?, ?, ?, ?, ?, ?)";
	 
	@POST
	@Consumes(MediaType.BOOKS_API_Books)
	@Produces(MediaType.BOOKS_API_Books)
	public Book createBook(Book book) {
		validateBook(book);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_BOOK_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, book.getTitulo());
			stmt.setString(3, book.getLengua());
			stmt.setString(4, book.getEdicion());
			stmt.setString(5, book.getFecha_edicion());
			stmt.setString(6, book.getFecha_impresion());
			stmt.setString(7, book.getEditorial());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
	 
				book = getBookFromDatabase(Integer.toString(id));
			} else {
				// Something has failed...
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;
	}
	
	private void validateUser(String id) {
	    Book book = getBookFromDatabase(id);
	    String username = book.getUsername();
		if (!security.getUserPrincipal().getName()
				.equals(username))
			throw new ForbiddenException(
					"You are not allowed to modify this book.");
	}
	
	private String DELETE_STING_QUERY = "delete from libro where id=?";
	 
	@DELETE
	@Path("/{id}")
	public void deleteSting(@PathParam("id") String id) {
		validateUser(id);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_STING_QUERY);
			stmt.setInt(1, Integer.valueOf(id));
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no libro with id="
						+ id);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	private void validateUpdateBook(Book book) {
		if (book.getTitulo() != null && book.getTitulo().length() > 100)
			throw new BadRequestException(
					"Subject can't be greater than 100 characters.");
		if (book.getLengua() != null && book.getLengua().length() > 500)
			throw new BadRequestException(
					"Content can't be greater than 500 characters.");
	}
	private String UPDATE_STING_QUERY = "update libro set titulo=ifnull(?, titulo), lengua=ifnull(?, lengua) where id=?";
	 
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.BOOKS_API_Books)
	@Produces(MediaType.BOOKS_API_Books)
	public Book updateBook(@PathParam("id") String id, Book book) {
		validateUpdateBook(book);
		validateUser(id);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_STING_QUERY);
			stmt.setString(1, book.getTitulo());
			stmt.setString(2, book.getLengua());
			stmt.setInt(3, Integer.valueOf(id));
	 
			int rows = stmt.executeUpdate();
			if (rows == 1)
				book = getBookFromDatabase(id);
			else {
				;// Updating inexistent sting
			}
	 
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;
		}
		
		private String GET_resenas_QUERY = "select *from reviews";
		 
		@GET
		@Path("/reviews")
		@Produces(MediaType.BOOKS_API_Resenas_COLLECTION)
		public ResenaCollection getResena() {
			ResenaCollection resenas = new ResenaCollection();
		 
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
		 
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(GET_resenas_QUERY);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Resena resena = new Resena();
					resena.setResenaid(rs.getInt("resenaid"));
					resena.setLibroid(rs.getInt("libroid"));
					resena.setUsername(rs.getString("username"));
					resena.setName(rs.getString("name"));
					resena.setUltima_fecha_hora(rs.getTimestamp("ultima_fecha_hora").getTime());
					resena.setTexto(rs.getString("texto"));
					resenas.addResena(resena);
				}
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
		 
			return resenas;
		}

		private String GET_resena_BYID_QUERY = "select *from reviews where libroid=?";
		@GET
		@Path("/reviews/{libroid}")
		@Produces(MediaType.BOOKS_API_Resenas_COLLECTION)
		public ResenaCollection getResena(@PathParam("libroid") String libroid) {
			ResenaCollection resenas = new ResenaCollection();
		 
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
		 
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(GET_resena_BYID_QUERY);
				stmt.setInt(1, Integer.valueOf(libroid));
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Resena resena = new Resena();
					resena.setResenaid(rs.getInt("resenaid"));
					resena.setLibroid(rs.getInt("libroid"));
					resena.setUsername(rs.getString("username"));
					resena.setName(rs.getString("name"));
					resena.setUltima_fecha_hora(rs.getTimestamp("ultima_fecha_hora").getTime());
					resena.setTexto(rs.getString("texto"));
					resenas.addResena(resena);
					
				}
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
		 
			return resenas;
		}
		private void validateResena(Resena resena) {
			if (resena.getUsername() == null)
				throw new BadRequestException("Username can't be null.");
			if (resena.getName() == null)
				throw new BadRequestException("name can't be null.");
			if (resena.getTexto().length() > 500)
				throw new BadRequestException("text can't be greater than 500 characters.");
			
		}
		private String GET_resena_ID_QUERY = "SELECT * FROM reviews where resenaid=?";
		public Resena getResenya( String resenaid) {
			
			Resena resena = new Resena();
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
		 
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(GET_resena_ID_QUERY);
				stmt.setInt(1, Integer.valueOf(resenaid));
				ResultSet rs = stmt.executeQuery();
				
				if (rs.next()) {
					resena.setResenaid(rs.getInt("resenaid"));
					resena.setLibroid(rs.getInt("libroid"));
					resena.setUsername(rs.getString("username"));
					resena.setName(rs.getString("name"));
					resena.setUltima_fecha_hora(rs.getTimestamp("ultima_fecha_hora").getTime());
					resena.setTexto(rs.getString("texto"));
					
				}else{
					throw new NotFoundException("There's no review with id="
							+ resenaid);
				}
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
		 
			return resena;
		}
		private String INSERT_resena_QUERY = "insert into reviews (libroid, username, name, texto) values (?, ?, ?, ?)";
		 
		@POST
		@Path("/reviews/{id}")
		@Consumes(MediaType.BOOKS_API_Resenas)
		@Produces(MediaType.BOOKS_API_Resenas)
		public Resena createReview(Resena resena) {
			validateResena(resena);
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
		 
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(INSERT_resena_QUERY,
						Statement.RETURN_GENERATED_KEYS);
		 
				stmt.setInt(1, resena.getLibroid());
				stmt.setString(2, resena.getUsername());
				stmt.setString(3, resena.getName());
				stmt.setString(4, resena.getTexto());
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int resenaid = rs.getInt(1);
					resena = getResenya(Integer.toString(resenaid));
					
				} else {
					// Something has failed...
				}
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
		 
			return resena;
		}
		private String DELETE_resena_QUERY = "delete from reviews where resenaid=?";
		@DELETE
		@Path("/reviews/{resenaid}")
		public void deleteResena(@PathParam("resenaid") String resenaid) {
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
		 
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(DELETE_resena_QUERY);
				stmt.setInt(1, Integer.valueOf(resenaid));
		 
				int rows = stmt.executeUpdate();
				if (rows == 0)
					throw new NotFoundException("There's no resena with resenaid="
							+ resenaid);
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
}
