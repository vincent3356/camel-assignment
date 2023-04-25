package com.assignment.models;

import java.util.ArrayList;
import java.util.HashMap;

public class AbsractAndAuthorInformation {
	
	private String abrstract;
	private HashMap<String, ArrayList<String>> authors=new HashMap<String, ArrayList<String>>();
	
	public AbsractAndAuthorInformation(String abrstract,HashMap<String, ArrayList<String>> authors) {
		super();
		this.abrstract = abrstract;
		this.authors = authors;
	}
	
	public AbsractAndAuthorInformation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAbrstract() {
		return abrstract;
	}
	public void setAbrstract(String abrstract) {
		this.abrstract = abrstract;
	}

	public HashMap<String, ArrayList<String>> getAuthors() {
		return authors;
	}

	public void setAuthors(HashMap<String, ArrayList<String>> authors) {
		this.authors = authors;
	}



	

}
