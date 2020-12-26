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

import be.sentedagnely.POJO.Review;

@Path("utensil")
public class UtensilApi {
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecipeById(@PathParam("id") int id) {
		System.out.println("entr�e4");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (id == 0) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
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

		String sql = "SELECT * FROM Review WHERE idReview=?";
		PreparedStatement prepare = null;
		ResultSet result = null;
		Review review = null;
		try {
			System.out.println("entr�e5");
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			result = prepare.executeQuery();
			if (result.next()) {
				review = new Review(result.getInt("idReview"), result.getInt("note"), result.getString("remark"));
			} else {
				return Response.status(Status.OK).entity(new Erreur(2000)).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(1002)).build();
		}
		// 3. Retourner la r�ponse
		return Response.status(Status.OK).entity(review).build();
	}

	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addRecipe(@DefaultValue("") @FormParam("name") String name,
			@DefaultValue("") @FormParam("type") String type,
			@DefaultValue("") @FormParam("idStep") String idStep) {
		System.out.println("entr�e1");
		Connection connect = null;
		String chaineConnexion = "jdbc:oracle:thin:@//193.190.64.10:1522/XEPDB1";
		// 1. test des params
		if (name == null || name.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (type == null || type.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
		if (idStep == null || idStep.equals("")) {
			return Response.status(Status.OK).entity(new Erreur(201)).build();
		}
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
		String sql = "INSERT INTO Utensil(name, type, idStep) VALUES(?,?,?)";
		PreparedStatement prepare = null;
		ResultSet result = null;
		try {
			System.out.println("entr�e2");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setInt(3, Integer.parseInt(type));
			prepare.setInt(3, Integer.parseInt(idStep));
			result = prepare.executeQuery();
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10021)).build();
		}
		// 2C requete recup id
		sql = "SELECT idUtensil FROM Utensil WHERE name like ? AND IdStep=?";
		prepare = null;
		result = null;
		int id = 0;
		try {
			System.out.println("entr�e3");
			prepare = connect.prepareStatement(sql);
			prepare.setString(1, name);
			prepare.setInt(2, Integer.parseInt(idStep));
			result = prepare.executeQuery();
			prepare.close();
			result.close();
			if (result.next()) {
				id = result.getInt("idReview");
			} else {
				return Response.status(Status.OK).entity(new Erreur(2001)).build();
			}
			prepare.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
		//2.C ajouter a la table utensil_step
		sql = "INSERT INTO Step_Utensil(idUtensil,idStep) VALUES(?,?)";
		prepare = null;
		result = null;
		try {
			prepare = connect.prepareStatement(sql);
			prepare.setInt(1, id);
			prepare.setInt(2, Integer.parseInt(idStep));
			result = prepare.executeQuery();		
		}catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new Erreur(10022)).build();
		}
		// 3.retourner la r�ponse
		return Response.status(Status.CREATED).header("Location", "/ApiCookApp/rest/utensil/" + id).build();
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
		// 1.connexion � la db
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
		String sql = "DELETE FROM Utensil WHERE idUtensil=?";
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
		// 3. Retourner la r�ponse
		return Response.status(Status.NO_CONTENT).build();
	}
}