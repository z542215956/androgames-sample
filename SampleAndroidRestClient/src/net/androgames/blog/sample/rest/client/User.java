package net.androgames.blog.sample.rest.client;

public class User {

	private String id;
	
	private String nom;
	private String prenom;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	public String toString() {
		return this.prenom + " " + this.nom;
	}
	
	public boolean equals(Object o) {
		return o != null 
				&& (o instanceof User) 
				&& this.id != null 
				&& this.id.equals(((User) o).getId());
	}
	
}
