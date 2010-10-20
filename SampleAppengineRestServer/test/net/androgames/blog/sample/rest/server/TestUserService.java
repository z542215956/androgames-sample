package net.androgames.blog.sample.rest.server;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TestUserService {

	private static final String URL = "http://localhost:8888/user";
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// creation d'un client Jersey
		Client c = Client.create();
		WebResource r;
		User user;
		
		// test d'insertion d'un utilisateur
		r = c.resource(URL);
		user = new User();
		user.setNom("Vianey");
		user.setPrenom("");
		user = r.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.put(User.class, user);
		
		System.out.println("User enregistré avec l'id : " + user.getId());
		
		// test de mise a jour de l'utilisateur
		r = c.resource(URL + "/" + user.getId());
		user = new User();
		user.setNom("Vianey");
		user.setPrenom("Antoine");
		user = r.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(User.class, user);
		
		System.out.println("User mise a jour : " + user.getPrenom() + " " + user.getNom());
		
		// test de recuperation de l'utilisateur
		r = c.resource(URL + "/" + user.getId());
		user = r.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(User.class);
		
		System.out.println("User récupéré : " + user.getPrenom() + " " + user.getNom());
		
		// test de suppression des utilisateurs
		r = c.resource(URL + "/" + user.getId());
		r.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.delete();
		
	}

}
