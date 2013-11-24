package com.example.testmobiletaskclient;

import com.example.testmobiletaskclient.EMF;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "taskendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath = "testmobiletaskclient"))
public class TaskEndpoint {

	private static final String API_KEY = "AIzaSyDxwpS47rfYvvbM4iefYG_tVyMQeSWMMOE";

	private static final DeviceInfoEndpoint endpoint = new DeviceInfoEndpoint();
	
	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listTask")
	public CollectionResponse<Task> listTask(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Task> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Task as Task");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Task>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Task obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Task> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getTask")
	public Task getTask(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		Task task = null;
		try {
			task = mgr.find(Task.class, id);
		} finally {
			mgr.close();
		}
		return task;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param task the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertTask")
	public Task insertTask(Task task) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsTask(task)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(task);
		} finally {
			mgr.close();
		}
		broadcastUpdate();
		return task;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param task the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateTask")
	public Task updateTask(Task task) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsTask(task)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(task);
		} finally {
			mgr.close();
		}
		return task;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeTask")
	public void removeTask(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		try {
			Task task = mgr.find(Task.class, id);
			mgr.remove(task);
		} finally {
			mgr.close();
		}
	}
	
	private void broadcastUpdate() {
		
		Sender sender = new Sender(API_KEY);
		
		String message = "Latest tasks added";
		
		for(Task t : listTask("", 5).getItems())
			message += t.getId() + "\n   name: " + t.getName()
					+ "\n   date: " + t.getDate()
					+ "\n   status: " + t.getStatus()
					+ "\n   required: " + t.getRequired() + "\n\n";
		
		CollectionResponse<DeviceInfo> response = endpoint.listDeviceInfo(null, 10);
		    for (DeviceInfo deviceInfo : response.getItems()) {

		    	Message msg = new Message.Builder().addData("message", message).build();
		
		    	try{
		    		Result result = sender.send(msg, deviceInfo.getDeviceRegistrationID(), 5);
		    	}
		    	catch(IOException ex)
		    	{}
		    }
	}

	private boolean containsTask(Task task) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Task item = mgr.find(Task.class, task.getId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}
}
