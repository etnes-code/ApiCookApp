package be.sentedagnely.API;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("step")
public class StepApi {
	@GET
	@Path("test")
	public String test() {
		return "classe step";
	}

}
