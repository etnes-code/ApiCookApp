package be.sentedagnely.API;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import be.sentedagnely.POJO.Recipe;
import be.sentedagnely.POJO.User;

@Path("recipe")
public class RecipeApi {
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") int id) {
		System.out.println("entr�e4");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params

		// 2.A connexion � la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("///////////////////////////////////////////");
			e.getMessage();
			
			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		System.out.println("entr�e 4 bis");
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		System.out.println("entr�e5");
		
		// 2.requete

		String sql = "SELECT * FROM Recipe WHERE id=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		Recipe recipe=null;
		try {
			System.out.println("entr�e5");
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			if (result.next()) {
				recipe = new Recipe(result.getInt("id"), result.getString("name"),result.getInt("difficulty"), result.getInt("totalDuration"),
						result.getString("urlPicture"));
			}else {
				return Response.status(Status.OK).entity(new Erreur(2000)).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1002)).build();
		}
		// 3. Retourner la r�ponse
				return Response.status(Status.OK).entity(recipe).build();
	
	}
	
	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("-1") @FormParam("difficulty") int difficulty,
			@DefaultValue("") @FormParam("totalDuration") int totalDuration,
			@DefaultValue("") @FormParam("urlPicture") String urlPicture) {
		System.out.println("entr�e1");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		
		// 2.A connexion � la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		// 2.B requete
		String sql = "INSERT INTO Recipe(name,difficulty,totalDuration,urlPicture) VALUES(?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			System.out.println("entr�e2");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setInt(2, difficulty);
			prepare.setInt(3, totalDuration);
			prepare.setString(4, urlPicture);
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 2C requete recup id
		sql = "SELECT id FROM Recipe WHERE name like ?";
		prepare = null;
		result = null;
		int id = 0;
		try {			
			System.out.println("entr�e3");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);

			result = prepare.executeQuery();
			if (result.next()) {
				id = result.getInt("id");
			} else {
				return Response.status(Status.OK).entity(new Erreur(2001)).build();
			}
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
		// 3.retourner la r�ponse
		return Response.status(Status.CREATED).header("Location", "/ApiCookApp/rest/recipe/" + id).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteUser(@PathParam("id") int id) {
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		//0.test param
		
		//1.connexion � la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		//2.requetes
		String sql = "DELETE FROM Recipe WHERE id=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			prepare.close();
			result.close();	
		}catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 3. Retourner la r�ponse
				return Response.status(Status.NO_CONTENT).build();	
	}
	
	

}