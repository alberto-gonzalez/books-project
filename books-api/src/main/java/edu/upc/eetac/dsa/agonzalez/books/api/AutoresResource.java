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

import edu.upc.eetac.dsa.agonzalez.books.api.model.Autor;
import edu.upc.eetac.dsa.agonzalez.books.api.model.AutoresCollection;
import edu.upc.eetac.dsa.agonzalez.books.api.model.Book;


@Path("/autores")
public class AutoresResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	private String GET_Autores_QUERY = "select * from autor";
	 
	@GET
	@Produces(MediaType.BOOKS_API_Autores_COLLECTION)
	public AutoresCollection getAutor() {
		AutoresCollection autores = new AutoresCollection();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_Autores_QUERY);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Autor autor = new Autor();
				autor.setAid(rs.getInt("aid"));
				autor.setNombre(rs.getString("nombre"));
				
				autores.addAutor(autor);
			
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
	 
		return autores;
	}
	
	private String GET_Autor_BY_ID_QUERY = "select aid,nombre from autor where aid=?";
	 
	@GET
	@Path("/{aid}")
	@Produces(MediaType.BOOKS_API_Autores)
	public Autor getAutor(@PathParam("aid") String aid) {
		Autor autor = new Autor();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_Autor_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(aid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				autor.setAid(rs.getInt("aid"));
				autor.setNombre(rs.getString("nombre"));	
			}else {
				throw new NotFoundException("There's no autor with aid="
						+ aid);
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
	 
		return autor;
	}
	private void validateAutor(Autor autor) {
		if (autor.getNombre() == null)
			throw new BadRequestException("nombre can't be null.");
		
	}
	private String INSERT_autor_QUERY = "insert into autor (nombre) values (?)";
	 
	@POST
	@Consumes(MediaType.BOOKS_API_Autores)
	@Produces(MediaType.BOOKS_API_Autores)
	public Autor createAutor(Autor autor) {
		validateAutor(autor);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_autor_QUERY,
					Statement.RETURN_GENERATED_KEYS);
	 
			stmt.setString(1, autor.getNombre());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
	 
				autor = getAutor(Integer.toString(id));
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
	 
		return autor;
	}
	private String DELETE_autor_QUERY = "delete from autor where aid=?";
	 
	@DELETE
	@Path("/{aid}")
	public void deleteSting(@PathParam("aid") String aid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_autor_QUERY);
			stmt.setInt(1, Integer.valueOf(aid));
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no autor with aid="
						+ aid);
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
	private void validateUpdateAutor(Autor autor) {
		if (autor.getNombre() != null && autor.getNombre().length() > 100)
			throw new BadRequestException(
					"Subject can't be greater than 100 characters.");
	}
	private String UPDATE_autor_QUERY = "update autor set nombre=ifnull(?, nombre) where aid=?";
	 
	@PUT
	@Path("/{aid}")
	@Consumes(MediaType.BOOKS_API_Autores)
	@Produces(MediaType.BOOKS_API_Autores)
	public Autor updateAutor(@PathParam("id") String aid, Autor autor) {
		validateUpdateAutor(autor);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_autor_QUERY);
			stmt.setString(1, autor.getNombre());

			stmt.setInt(2, Integer.valueOf(aid));
	 
			int rows = stmt.executeUpdate();
			if (rows == 1)
				autor = getAutor(aid);
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
	 
		return autor;
		}
}