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

import be.sentedagnely.POJO.User;

@Path("/user")
public class UserApi {

	
	public UserApi() {
		System.out.println("called");
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") int id) {
		System.out.println("entrée4");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params

		// 2.A connexion à la db
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
		// 2.requete

		String sql = "SELECT * FROM Users WHERE IdUser=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		User user = null;
		try {
			System.out.println("entrée5");
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			if (result.next()) {
				user = new User(result.getInt("IdUser"), result.getString("name"), result.getString("firstName"),
						result.getString("email"), result.getString("password"), result.getString("address"));
			}else {
				return Response.status(Status.OK).entity(new Erreur(2000)).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1002)).build();
		}
		// 3. Retourner la réponse
				return Response.status(Status.OK).entity(user).build();
	}
	
	@Path("create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("") @FormParam("firstname") String firstname,
			@DefaultValue("") @FormParam("email") String email,
			@DefaultValue("") @FormParam("password") String password,
			@DefaultValue("") @FormParam("address") String address) {
		System.out.println("entrée1");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		

		// 2.A connexion à la db
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
		String sql = "INSERT INTO Users(name,firstName,email,password,address,adminLevel) VALUES(?,?,?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			System.out.println("entrée2");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setString(2, firstname);
			prepare.setString(3, email);
			prepare.setString(4, password);
			prepare.setString(5, address);
			prepare.setInt(6, 0);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 2C requete recup id
		sql = "SELECT id FROM Users WHERE name=? AND firstName=?";
		prepare = null;
		result = null;
		int id = 0;
		try {
			System.out.println("entrée3");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setString(2, firstname);
			result = prepare.executeQuery();
			if (result.next()) {
				id = result.getInt(1);
			} else {
				return Response.status(Status.OK).entity(new Erreur(2001)).build();
			}
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
		// 3.retourner la réponse
		return Response.status(Status.CREATED).header("Location", "/ApiCookApp/rest/user/" + id).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteUser(@PathParam("id") int id) {
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		//0.test param
		
		//1.connexion à la db
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
		String sql = "DELETE FROM Users WHERE id=?";
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
		// 3. Retourner la réponse
				return Response.status(Status.NO_CONTENT).build();	
	}
	
}
