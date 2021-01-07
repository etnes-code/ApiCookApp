package be.sentedagnely.API;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import be.sentedagnely.POJO.Step;
import be.sentedagnely.POJO.User;

@Path("recipe")
public class RecipeApi {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecipeById(@PathParam("id") int id) {
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
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}

		// 2.requete

		String sql = "SELECT * FROM Recipe WHERE idRecipe=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		Recipe recipe = null;
		try {
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
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setInt(2, Integer.parseInt(idUser));
			result = prepare.executeQuery();
			if (result.next()) {
				id = result.getInt("idRecipe");
				System.out.println("id de la recette crée " + id);
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
		return Response.status(Status.CREATED).header("Location", "/ApiCookApp/rest/recipe/" + id).build();
	}

	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllRecipe() {
		Connection connect = null;
		Recipe recipe;
		Step step;
		Ingredient ingredient;
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
		String sql = "SELECT * FROM Recipe ORDER BY name asc";
		String sqlIngredient = "SELECT * FROM Recipe_ingredient R INNER JOIN Ingredient I ON I.idIngredient = R.idIngredient where idRecipe=?";
		String sqlStep = "Select * FROM Step WHERE idRecipe=? ORDER BY orderStep ASC";
		PreparedStatement prepare = null;
		ResultSet result = null;
		ResultSet resultIngredient = null;
		ResultSet resultStep = null;
		Set<Recipe> listrecipe = new HashSet<Recipe>();
		try {
			// requete recipe
			prepare = connect.prepareStatement(sql);
			result = prepare.executeQuery();

			while (result.next()) {
				recipe = null;
				recipe = new Recipe(result.getInt("idRecipe"), result.getString("name"), result.getString("category"),
						result.getInt("difficulty"), result.getInt("totalDuration"), result.getString("urlPicture"));
				System.out.println("API RECIPE- getALL : CATEGORY" + recipe.getCategory());
				// requete ingredient
				prepare = connect.prepareStatement(sqlIngredient);
				prepare.setInt(1, recipe.getId());
				resultIngredient = prepare.executeQuery();

				while (resultIngredient.next()) {
					ingredient = null;
					ingredient = new Ingredient(resultIngredient.getInt("idIngredient"),
							resultIngredient.getString("name"), resultIngredient.getString("type"),
							resultIngredient.getInt("calories"), resultIngredient.getString("massUnit"));
					recipe.addListIngredient(ingredient);
				}
				prepare = connect.prepareStatement(sqlStep);
				prepare.setInt(1, recipe.getId());
				resultStep = prepare.executeQuery();
				while (resultStep.next()) {
					step = null;
					step = new Step(resultStep.getInt("idStep"), resultStep.getInt("orderStep"),
							resultStep.getString("text"), resultStep.getInt("duration"));
					recipe.addListStep(step);
				}
				listrecipe.add(recipe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}

		return Response.status(Status.OK).entity(listrecipe).build();

	}

	@POST
	@Path("/update")
	public Response updateRecipe(@DefaultValue("") @FormParam("nameRecipe") String name,
			@DefaultValue("") @FormParam("category") String category,
			@DefaultValue("") @FormParam("difficulty") String difficulty,
			@DefaultValue("") @FormParam("idRecipe") String idRecipe) {
		Connection connect = null;
		CallableStatement callableStmt = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (name == null || name.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (category == null || category.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (difficulty == null || difficulty.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (idRecipe == null || idRecipe.equals("")) {
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
		//appel de la procédure 
		try {
			callableStmt = connect.prepareCall("{call updateRecipe(?,?,?,?)}");
			callableStmt.setInt(1, Integer.parseInt(idRecipe));
			callableStmt.setString(2, name);
			callableStmt.setString(3, category);
			callableStmt.setInt(4, Integer.parseInt(difficulty));
			callableStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 3. Retourner la réponse
		return Response.status(Status.NO_CONTENT).build();	
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
