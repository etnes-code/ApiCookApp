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

import be.sentedagnely.POJO.Ingredient;
import be.sentedagnely.POJO.Quantity;

@Path("ingredient")
public class IngredientApi {
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecipeById(@PathParam("id") int id) {
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

		String sql = "SELECT * FROM Ingredient WHERE idIngredient=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		Ingredient ingredient = null;
		try {
			System.out.println("entrée5");
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			if (result.next()) {
				ingredient = new Ingredient(result.getInt("idIngredient"), result.getString("name"),
						result.getString("type"), result.getInt("calories"), result.getString("massUnit"));
			} else {
				return Response.status(Status.OK).entity(new Erreur(2000)).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1002)).build();
		}
		// 3. Retourner la réponse
		return Response.status(Status.OK).entity(ingredient).build();

	}

	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addRecipe(@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("") @FormParam("type") String type, @DefaultValue("") @FormParam("calories") String calories,
			@DefaultValue("") @FormParam("massUnit") String massUnit,
			@DefaultValue("") @FormParam("idQuantity") String idQuantity) {
		System.out.println("entrée1");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (name == null || name.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (type == null || type.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (calories == null || calories.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (massUnit == null || massUnit.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (idQuantity == null || idQuantity.equals("")) {
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
		// 2.B requete
		String sql = "INSERT INTO Ingredient(name,type,calories,massUnit,idQuantity) VALUES(?,?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			System.out.println("entrée2");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name );
			prepare.setString(2, type);
			prepare.setInt(3, Integer.parseInt(calories));
			prepare.setString(4, massUnit);		
			prepare.setInt(5, Integer.parseInt(idQuantity));
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 2C requete recup id
		sql = "SELECT idIngredient FROM Ingredient WHERE name like ? AND idQuantity=?";
		prepare = null;
		result = null;
		int id = 0;
		try {
			System.out.println("entrée3");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setInt(2, Integer.parseInt(idQuantity));
			result = prepare.executeQuery();
			if (result.next()) {
				id = result.getInt("idIngredient");
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
		return Response.status(Status.CREATED).header("Location", "/ApiCookApp/rest/ingredient/" + id).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteRecipe(@PathParam("id") int id) {
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
		String sql = "DELETE FROM Ingredient WHERE idIngredient=?";
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
