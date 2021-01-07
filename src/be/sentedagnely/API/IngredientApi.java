package be.sentedagnely.API;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

@Path("ingredient")
public class IngredientApi {
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientById(@PathParam("id") int id) {
		System.out.println("entrée4");
		Connection connect = null;
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
		try {
			connect = DriverManager.getConnection(Const.chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		// 2.requete

		String sql = "SELECT * FROM Ingredient WHERE idIngredient=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		Ingredient ingredient = null;
		try {
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
	
	@Path("/createri")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addIngredientRecipe(@FormParam("idRecipe") int idRecipe,@FormParam("idIngredient") int idIngredient) {
		Connection connect = null;
		if (idRecipe == 0) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (idIngredient == 0) {
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
			connect = DriverManager.getConnection(Const.chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		String sql = "INSERT INTO Recipe_ingredient(idRecipe,idIngredient) VALUES(?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, idRecipe);
			prepare.setInt(2, idIngredient);	
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		return Response.status(Status.NO_CONTENT).build();	
	}

	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addIngredient(@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("") @FormParam("type") String type, @DefaultValue("") @FormParam("calories") String calories,
			@DefaultValue("") @FormParam("massUnit") String massUnit,
			@DefaultValue("") @FormParam("quantity") String quantity) {
		Connection connect = null;
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
		// 2.A connexion à la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		try {
			connect = DriverManager.getConnection(Const.chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		// 2.B requete
		String sql = "INSERT INTO Ingredient(name,type,calories,massUnit) VALUES(?,?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setString(2, type);
			prepare.setInt(3, Integer.parseInt(calories));
			prepare.setString(4, massUnit);
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 2C requete recup id
		sql = "SELECT idIngredient FROM Ingredient WHERE name like ? ";
		prepare = null;
		result = null;
		int id = 0;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
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

	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllIngredient() {
		Connection connect = null;
		// 2.A connexion à la db
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1000)).build();
		}
		try {
			connect = DriverManager.getConnection(Const.chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		String sql = "SELECT * FROM Ingredient ORDER BY name ASC";
		PreparedStatement prepare = null;
		ResultSet result = null;
		ArrayList<Ingredient> listIngredient = new ArrayList<Ingredient>();
		try {
			prepare = connect.prepareStatement(sql);
			result = prepare.executeQuery();		
			Ingredient ingredient;
			while (result.next()) {
				ingredient = null;
				ingredient = new Ingredient(result.getInt("idIngredient"), result.getString("name"),
						result.getString("type"), result.getInt("calories"), result.getString("massUnit"));
				listIngredient.add(ingredient);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
		return Response.status(Status.OK).entity(listIngredient).build();
	}
	@DELETE
	@Path("{id}")
	public Response deleteIngredient(@PathParam("id") int id) {
		Connection connect = null;
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
			connect = DriverManager.getConnection(Const.chaineConnexion, Const.username, Const.pwd);
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
