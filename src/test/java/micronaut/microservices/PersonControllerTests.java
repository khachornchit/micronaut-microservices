package micronaut.microservices;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import micronaut.microservices.models.Gender;
import micronaut.microservices.models.Person;

@MicronautTest
public class PersonControllerTests {
 
    @Inject
    EmbeddedServer server;
     
    // tests implementation ...

    @Test
    public void testAdd() throws MalformedURLException {
        HttpClient client = HttpClient.create(new URL("http://" + server.getHost() + ":" + server.getPort()));
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setAge(33);
        person.setGender(Gender.MALE);
        person = client.toBlocking().retrieve(HttpRequest.POST("/persons", person), Person.class);
        Assertions.assertNotNull(person);
        Assertions.assertEquals(Integer.valueOf(1), person.getId());
    }
 
    @Test
    public void testAddNotValid() throws MalformedURLException {
        HttpClient client = HttpClient.create(new URL("http://" + server.getHost() + ":" + server.getPort()));
        final Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setAge(-1);
        person.setGender(Gender.MALE);
 
        Assertions.assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().retrieve(HttpRequest.POST("/persons", person), Person.class),
                "person.age: must be greater than or equal to 0");
    }
 
    @Test
    public void testFindById() throws MalformedURLException {
        HttpClient client = HttpClient.create(new URL("http://" + server.getHost() + ":" + server.getPort()));
        Person person = client.toBlocking().retrieve(HttpRequest.GET("/persons/1"), Person.class);
        Assertions.assertNotNull(person);
    }
 
    @Test
    public void testFindAll() throws MalformedURLException {
        HttpClient client = HttpClient.create(new URL("http://" + server.getHost() + ":" + server.getPort()));
        Person[] persons = client.toBlocking().retrieve(HttpRequest.GET("/persons"), Person[].class);
        Assertions.assertEquals(1, persons.length);
    }
}