/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.jgroups;

import dk.itu.smds.e2013.jgroups.common.TaskProvider;
import dk.itu.smds.e2013.serialization.common.Envelope;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 * 
 * @author Rao
 */
public class TaskReceiver2 extends ReceiverAdapter {

	final List<String> state = new LinkedList<>();
	protected TaskProvider provider;
	private JChannel channel;
	private String prefix = "[receiver]: ";
	public String hostProcessAddresss = "";
	public TaskManagerServer manager;

	// public Address address;

	// request, timestamp
	// Hashtable<String,Integer> map = new Hashtable<String,Integer>();

	public TaskReceiver2(TaskProvider provider, JChannel channel,
			TaskManagerServer server) {

		this.provider = provider;
		this.channel = channel;
		manager = server;
	}

	@Override
	public void receive(Message msg) {

		// the message sender's address.
		Address address = msg.getSrc();

		// We know that, we will always get Xml.
		String envelopeXml = msg.getObject().toString();
		String messageSourceId = msg.getSrc().toString();
		Envelope deserializedEnvelope;

		try {
			System.out.println("ENVELOPE received");

			deserializedEnvelope = TaskSerializer
					.DeserializeEnvelope(envelopeXml);

			System.out.println("ENVELOPE deserialized");

			// System.out.println("Input envelope received: " + inputData);
			// We may get
		} catch (JAXBException ex) {
			System.out.println(prefix
					+ "Failed to deserialize envelope Xml. Error message" + ex);

			return;
		}

		String sourceInfo = String.format("[ command: %s, source: %s ]:",
				deserializedEnvelope.command, messageSourceId);

		// if there's no lock object in the envelope
		if (deserializedEnvelope.lock.equals("commit")) {

			// concentrate on the commands
			if (deserializedEnvelope.command.equals("execute")) {

				
				System.out.println("EXECUTING ...........................................");
				// Only handle the command if it is from other sources.
				if (!messageSourceId.equals(hostProcessAddresss)) {

					Task task = GetTaskWithId(deserializedEnvelope.taskId);

					if (task != null) {

						task.status = "executed";

						task.required = "false";

						try {
							provider.PersistTaskManager();

						} catch (JAXBException ex) {
							System.out
									.println(prefix
											+ "Failed to persist envelope Xml. Error message"
											+ ex);
						} catch (IOException ex) {
							System.out
									.println(prefix
											+ "Failed to persist envelope Xml. Error message"
											+ ex);
						}

						System.out.println(hostProcessAddresss + sourceInfo
								+ " Task :" + deserializedEnvelope.taskId
								+ " executed successfully! ");
					} else {

						System.out.println(hostProcessAddresss + sourceInfo
								+ " Error: Task :"
								+ deserializedEnvelope.taskId + " NOT found! ");

					}
				}
				// _____________________________________________________________________________________________________________
			} else if (deserializedEnvelope.command.equals("request")) {

				System.out.println("EXECUTING ...........................................");
				
				// Only handle the command if it is from other sources.
				if (!messageSourceId.equals(hostProcessAddresss)) {

					Task task = GetTaskWithId(deserializedEnvelope.taskId);

					if (task != null) {

						task.required = "true";

						try {
							provider.PersistTaskManager();

						} catch (JAXBException ex) {
							System.out
									.println(prefix
											+ "Failed to persist envelope Xml. Error message"
											+ ex);
						} catch (IOException ex) {
							System.out
									.println(prefix
											+ "Failed to persist envelope Xml. Error message"
											+ ex);
						}

						System.out.println(hostProcessAddresss + sourceInfo
								+ " Task :" + deserializedEnvelope.taskId
								+ " marked as required successfully! ");
					} else {

						System.out.println(hostProcessAddresss + sourceInfo
								+ " Error: Task :"
								+ deserializedEnvelope.taskId + " NOT found! ");

					}

				}

			}

			// But if there's a lock object in the envelope
		} else {
			
			// Only handle the command if it is from other sources.			
			if (!messageSourceId.equals(hostProcessAddresss)) {

				if (deserializedEnvelope.lock.equals("requestlock")) {

					System.out
							.println("requestlock.................................................");
					System.out.println("TaskManager contains taskid: "
							+ manager.grantTable
									.containsKey(deserializedEnvelope.taskId));

					Envelope env = new Envelope();

					if (manager.grantTable
							.containsKey(deserializedEnvelope.taskId)) {
						// send deny

						env.initiator = hostProcessAddresss;
						env.lock = "denyLock";
						env.taskId = deserializedEnvelope.taskId;
						env.command = deserializedEnvelope.command;

						 sendLockMessages(address, channel, env);

//						WriteEnvelopeToChannel(env, channel);

					} else {
						// send grant

						env.initiator = hostProcessAddresss;
						env.lock = "grantLock";
						env.taskId = deserializedEnvelope.taskId;
						env.command = deserializedEnvelope.command;

						sendLockMessages(address, channel, env);
						
//						WriteEnvelopeToChannel(env, channel);
					}

				} else if (deserializedEnvelope.lock.equals("denyLock")) {

					System.out
							.println("denyLock......................................................");
					manager.grantTable.remove(deserializedEnvelope.taskId);
					
					
					

				} else if (deserializedEnvelope.lock.equals("grantLock")) {
					System.out
							.println("grantLock.....................................................");
					// count down number of grants
					manager.grantTable
							.put(deserializedEnvelope.taskId,
									manager.grantTable
											.get(deserializedEnvelope.taskId) - 1);

					// if all grantLocks have been received
					if (manager.grantTable.get(deserializedEnvelope.taskId) == 0) {
						// access is granted. remove the lock and task
						manager.grantTable.remove(deserializedEnvelope.taskId);

						// send
						Envelope env = new Envelope();

						env.initiator = hostProcessAddresss;
						env.lock = "commit";
						env.command = deserializedEnvelope.command;
						env.taskId = deserializedEnvelope.taskId;

						// send to group
//						WriteEnvelopeToChannel(env, channel);

						sendLockMessages(address, channel, env);
						
					}
				}
			}
		}
	}

	@Override
	public void getState(OutputStream output) throws Exception {

	}

	@Override
	public void setState(InputStream input) throws Exception {

	}

	@Override
	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}

	private void SendAddTaskCommand(Task task) {

		Envelope envelope = new Envelope();

		envelope.command = "add";

		envelope.data.add(task);

		WriteEnvelopeToChannel(envelope, channel);

	}

	private void sendLockMessages(Address add, JChannel channel, Envelope env) {

		try {
			// Here we make the thread to sleep for random amount of time < 1
			// sec,
			// so as tossdsdds simulate some latency in network.
			Thread.sleep(5000);

			String xml = TaskSerializer.SerializeEnvelope(env);

			Message mess = new Message(add, null, xml);

			channel.send( mess );

		} catch (JAXBException | InterruptedException ie) {
			// System.out.println(messagePrefix +
			// "Failed to write task object to the channel. Error message:" +
			// ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) {

		try {
			String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);

			Message msg = new Message(null, null, envelopeXml);

			channel.send(msg);

		} catch (Exception ex) {
			System.out
					.println("Failed to write task object to the channel. Error message:"
							+ ex.getMessage());
		}

	}

	private Task GetTaskWithId(String taskId) {

		for (int index = 0; index < provider.TaskManagerInstance.tasks.size(); index++) {
			if (provider.TaskManagerInstance.tasks.get(index).id.equals(taskId)) {
				return provider.TaskManagerInstance.tasks.get(index);
			}

		}
		return null;
		//

	}

}
