package testing;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

	@Inject
	FruitService fruitService;

	@GET
	public List<Fruit> list() {
		return fruitService.list();
	}

	@POST
	public List<Fruit> add(Fruit fruit) {
		fruitService.add(fruit);
		return list();
	}
}
