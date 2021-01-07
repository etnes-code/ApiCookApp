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

import be.sentedagnely.POJO.Ingredient;
import be.sentedagnely.POJO.Recipe;
import be.sentedagnely.POJO.Step;
import be.sentedagnely.POJO.User;

@Path("user")
public class UserApi {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") int id) {
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

		String sql = "SELECT * FROM Users WHERE IdUser=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		User user = null;
		try {
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
		Recipe recipe;
		Step step;
		Ingredient ingredient;
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
		try {
			connect = DriverManager.getConnection(chaineConnexion, Const.username, Const.pwd);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1001)).build();
		}
		// 2.requete

		String sql = "SELECT * FROM Users WHERE email like ?";
		String sqlRecipe = "Select * from Recipe WHERE IdUser=?";
		String sqlIngredient = "SELECT * FROM Recipe_ingredient R INNER JOIN Ingredient I ON I.idIngredient = R.idIngredient where idRecipe=?";
		String sqlStep = "Select * FROM Step WHERE idRecipe=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		ResultSet resultRecipe = null;
		ResultSet resultIngredient = null;
		ResultSet resultStep = null;
		User user = null;
		try {
			// requete user
			prepare = connect.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
			// requete recipe
			prepare = connect.prepareStatement(sqlRecipe);
			prepare.setInt(1, user.getId());
			result = prepare.executeQuery();

			while (result.next()) {
				recipe = null;
				recipe = new Recipe(result.getInt("idRecipe"), result.getString("name"), result.getString("category"),
						result.getInt("difficulty"), result.getInt("totalDuration"), result.getString("urlPicture"));
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
				// requete step
				prepare = connect.prepareStatement(sqlStep);
				prepare.setInt(1, recipe.getId());
				resultStep = prepare.executeQuery();
				while (resultStep.next()) {
					step = null;
					step = new Step(resultStep.getInt("idStep"), resultStep.getInt("orderStep"),
							resultStep.getString("text"), resultStep.getInt("duration"));
					recipe.addListStep(step);
				}
				user.addList(recipe);
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
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}

		// 2.B requete
		String sql = "INSERT INTO Users(name,firstName,email,password,address) VALUES(?,?,?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		// 2C requete recup id
		sql = "SELECT IdUser FROM users WHERE email like ?";
		prepare = null;
		result = null;
		int id = 0;
		try {
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

	@POST
	@Path("/update")
	public Response updateUser(@DefaultValue("") @FormParam("idUser") String idUser,
			@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("") @FormParam("firstname") String firstname,
			@DefaultValue("") @FormParam("email") String email,
			@DefaultValue("") @FormParam("password") String password,
			@DefaultValue("") @FormParam("address") String address) {
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
		//appel de la procédure 
		try {
			callableStmt = connect.prepareCall("{call updateUser(?,?,?,?,?,?)}");
			callableStmt.setInt(1, Integer.parseInt(idUser));
			callableStmt.setString(2, name);
			callableStmt.setString(3, firstname);
			callableStmt.setString(4, email);
			callableStmt.setString(5, password);
			callableStmt.setString(6, address);
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
