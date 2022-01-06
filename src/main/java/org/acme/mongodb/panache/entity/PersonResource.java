package org.acme.mongodb.panache.entity;
import org.bson.types.ObjectId;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import io.vertx.redis.client.Response;
import io.quarkus.redis.client.RedisClient;


//http://localhost:8080/entity/persons
//http://localhost:8080/entity/persons/validate/{name}

@Path("/entity/persons")
@Consumes("application/json")
@Produces("application/json")
public class PersonResource {

    @Inject
    RedisClient redisClient;
   
    @GET
    public List<Person> list() {
        return Person.listAll();
    }
    
    @GET
    @Path("/validate/{name}")
    public String search(@PathParam("name") String name) {

        Response response = redisClient.get(name);

        if(response != null)
        {
            return "FOUND IN REDIS CACHE ";
        }
        else 
        {
            Person p= Person.findByName(name);
            if(p!=null){

                return "FOUND IN MONGODB";
            }
            else
            {
                Person q = new Person();
                q.setName(name);
                q.persist();
                return q.toString();
            }
        }
    }

    @POST
    public Person create(Person person) {
         person.persist();
         return person;
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        Person person = Person.findById(new ObjectId(id));
        person.delete();
    }
}

