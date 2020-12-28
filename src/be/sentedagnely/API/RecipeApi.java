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
import be.sentedagnely.POJO.Recipe;
import be.sentedagnely.POJO.User;

@Path("recipe")
public class RecipeApi {

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

		String sql = "SELECT * FROM Recipe WHERE idRecipe=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		Recipe recipe = null;
		try {
			System.out.println("entrée5");
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			if (result.next()) {
				recipe = new Recipe(result.getInt("idRecipe"), result.getString("name"), result.getString("category"),
						result.getInt("difficulty"), result.getInt("totalDuration"), result.getString("urlPicture"));
			} else {
				return Response.status(Status.OK).entity(new Erreur(2000)).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1002)).build();
		}
		// 3. Retourner la réponse
		return Response.status(Status.OK).entity(recipe).build();
	}

	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addRecipe(@DefaultValue("") @FormParam("nameRecipe") String name,
			@DefaultValue("") @FormParam("category") String category,
			@DefaultValue("") @FormParam("difficulty") String difficulty,
			@DefaultValue("") @FormParam("totalDuration") String totalDuration,
			@DefaultValue("") @FormParam("urlPicture") String urlPicture,
			@DefaultValue("") @FormParam("idUser") String idUser) {
		System.out.println("entrée1");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (name == null || name.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (difficulty == null || difficulty.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (totalDuration == null || totalDuration.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (totalDuration == null || totalDuration.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (idUser == null || idUser.equals("")) {
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
		String sql = "INSERT INTO Recipe(name,category,difficulty,totalDuration,urlPicture,IdUser) VALUES(?,?,?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			System.out.println("entrée2");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setString(2, category);
			prepare.setInt(3, Integer.parseInt(difficulty));
			prepare.setInt(4, Integer.parseInt(totalDuration));
			prepare.setString(5, urlPicture);
			prepare.setInt(6, Integer.parseInt(idUser));
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		
		// 2C requete recup id
		sql = "SELECT idRecipe FROM Recipe WHERE name like ? AND IdUser=?";
		prepare = null;
		result = null;
		int id = 0;
		try {
			System.out.println("entrée3");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setInt(2, Integer.parseInt(idUser));
			result = prepare.executeQuery();
			if (result.next()) {
				id = result.getInt("idRecipe");
			} else {
				return Response.status(Status.OK).entity(new Erreur(2001)).build();
			}
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
<<<<<<< HEAD
		//2D ajout a la table recipe_ingredient
		/*
		 * 	Récupérer l'iD de recipe, ensuite dans user.update() juste après l'ajout de recette : 
		 * 		créer l'entrée RECIPE_INGREDIENT (Faire une fonction dans IngredientApi pour créer)
		 * 
		 */
=======
		/*2D ajout a la table recipe_ingredient
>>>>>>> branch 'master' of https://github.com/etnes-code/ApiCookApp
		sql = "INSERT INTO RECIPE_INGREDIENT(idIngredient,idRecipe) VALUES(?,?)";
		prepare = null;
		result = null;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, Integer.parseInt(idIngredient));
			prepare.setInt(2, id);
			result = prepare.executeQuery();	
			prepare.close();
			result.close();
		}catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}*/
				 
		// 3.retourner la réponse
		return Response.status(Status.CREATED).header("Location", "/ApiCookApp/rest/recipe/" + id).build();
	}
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllRecipe() {
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
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
		//SELECT * FROM Recipe_Ingredient R JOIN Ingredient I ON (R.idRecipe = ? AND R.idIngredient = I.IdIngredient)
		String sql = "SELECT * FROM Recipe";
		PreparedStatement prepare = null;
		ResultSet result = null;
		ArrayList<Recipe> listrecipe = new ArrayList<Recipe>();
		try {
			prepare = connect.prepareStatement(sql);
			result = prepare.executeQuery();		
			Recipe recipe;
			while (result.next()) {
				recipe = null;
				recipe = new Recipe(result.getInt("idRecipe"),result.getString("name"),result.getString("Category"),result.getInt("difficulty"),result.getInt("totalDuration"),result.getString("urlPicture"));
				listrecipe.add(recipe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
		return Response.status(Status.OK).entity(listrecipe).build();
		
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
		String sql = "DELETE FROM Recipe WHERE idRecipe=?";
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
