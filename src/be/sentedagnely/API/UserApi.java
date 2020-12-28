package be.sentedagnely.API;

import java.sql.CallableStatement;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import be.sentedagnely.POJO.User;

@Path("user")
public class UserApi {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") int id) {
		System.out.println("entrée4");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (id == 0) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		// 2.A connexion à la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("///////////////////////////////////////////");
			e.getMessage();

			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		System.out.println("entrée 4 bis");
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		System.out.println("entrée5");

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
				System.out.println("nom trouvé dans la db " + result.getString("name"));
				user = new User(result.getInt("IdUser"), result.getString("name"), result.getString("firstName"),
						result.getString("email"), result.getString("password"), result.getString("address"));
			} else {
				return Response.status(Status.OK).entity(new Erreur(2000)).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1002)).build();
		}
		// 3. Retourner la réponse
		return Response.status(Status.OK).entity(user).build();

	}
	
	@Path("/find/{email}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") String email) {
		Connection connect = null;
		System.out.println(email);
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (email == null || email.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		// 2.A connexion à la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("///////////////////////////////////////////");
			e.getMessage();

			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		System.out.println("entrée 4 bis");
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		// 2.requete

				String sql = "SELECT * FROM Users WHERE email like ?";
				PreparedStatement prepare = null;
				ResultSet result = null;
				User user = null;
				try {
					System.out.println("entrée5");
					prepare = connect.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					prepare.setString(1, email);
					result = prepare.executeQuery();
					System.out.println(result.getRow());
					if (result.first()) {
						System.out.println("nom trouvé dans la db " + result.getString("name"));
						user = new User(result.getInt("IdUser"), result.getString("name"), result.getString("firstName"),
								result.getString("email"), result.getString("password"), result.getString("address"));
					} else {
						return Response.status(Status.OK).entity(new Erreur(2000)).build();
					}

				} catch (SQLException e) {
					e.printStackTrace();
					return Response.status(Status.OK).entity(new Erreur(1002)).build();
				}
				// 3. Retourner la réponse
				return Response.status(Status.OK).entity(user).build();
		
	}

	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("") @FormParam("firstname") String firstname,
			@DefaultValue("") @FormParam("email") String email,
			@DefaultValue("") @FormParam("password") String password,
			@DefaultValue("") @FormParam("address") String address) {
		System.out.println("entrée1");
		Connection connect = null;
		CallableStatement callableStmt = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (name == null || name.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (firstname == null || firstname.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (email == null || email.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (password == null || password.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (address == null || address.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
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
		
		try {
		callableStmt = connect.prepareCall("{call createUser(?,?,?,?,?)}");
		callableStmt.setString(1, name);
        callableStmt.setString(2, firstname);
        callableStmt.setString(3, email);
        callableStmt.setString(4, password);
        callableStmt.setString(5, address);
        callableStmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
	  
		// 2.B requete
		String sql = "INSERT INTO Users(name,firstName,email,password,address) VALUES(?,?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		/*
		try {
			System.out.println(
					"valeurs des champs : " + name + " " + firstname + " " + email + " " + password + " " + address);
			System.out.println("affiche");
			System.out.println("entrée2");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setString(2, firstname);
			prepare.setString(3, email);
			prepare.setString(4, password);
			prepare.setString(5, address);
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}*/
		// 2C requete recup id
		sql = "SELECT IdUser FROM users WHERE email like ?";
		prepare = null;
		result = null;
		int id = 0;
		try {
			System.out.println("entrée3");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, email);

			result = prepare.executeQuery();
			if (result.next()) {
				id = result.getInt("IdUser");
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
		// 0.test param
		if (id == 0) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		// 1.connexion à la db
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
		// 2.requetes
		String sql = "DELETE FROM Users WHERE IdUser=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 3. Retourner la réponse
		return Response.status(Status.NO_CONTENT).build();
	}

}
