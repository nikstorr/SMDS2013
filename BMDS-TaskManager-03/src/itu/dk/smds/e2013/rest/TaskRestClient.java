package itu.dk.smds.e2013.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TaskRestClient
{
	public static void main(String[] args)
	{
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		
		String taskXml = service.path("rest").path("tasks").accept(MediaType.TEXT_XML).get(String.class);
		  
		System.out.println(taskXml);
		
		
	}
	
	private static URI getBaseURI()
	{
		return UriBuilder.fromUri("http://localhost:8080/BDMS-TaskManager-03").build();
	}
} 
