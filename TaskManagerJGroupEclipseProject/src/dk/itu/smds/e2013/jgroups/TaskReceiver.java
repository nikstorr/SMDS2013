/*
 * To change this template, choose Tools | Templates
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
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 *
 * @author rao
 */
// register receivers. Either implement Jgroup.Receiver or extend ReceiverAdapter (which has defaullt implementations) and only
// override what we need.
// 

public class TaskReceiver extends ReceiverAdapter {

    // Instance variable. This is the state. A list of Tasks?
	// a new instance joining the group can get the whole history of communication.
	final List<String> state = new LinkedList<>();
    
    protected TaskProvider provider;
    private JChannel channelTasks;
    private String prefix = "[receiver]: ";

    public TaskReceiver(TaskProvider provider, JChannel channel) {
        this.provider = provider;
        channelTasks = channel;
    }

    // state transfer. Gets the cluster state from the coordinator.
    // getState() and setState() are used to transfer a history of the communication in the group
    // prior to a given instance joining. NOT A PART OF THIS ASSIGNMENT.
    @Override
    public void getState(OutputStream output) throws Exception {
//        synchronized (provider.TaskManagerInstance) {
//            // write the whole task manager object to the stream.
//            Util.objectToStream(provider.TaskManagerInstance, new DataOutputStream(output));
//        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
//        TaskManager taskmanager = (TaskManager) Util.objectFromStream(new DataInputStream(input));
//        synchronized (provider.TaskManagerInstance) {
//
//            provider.TaskManagerInstance.tasks.clear();
//
//            provider.TaskManagerInstance.users.clear();
//
//            provider.TaskManagerInstance.users.addAll(taskmanager.users);
//
//            provider.TaskManagerInstance.tasks.addAll(taskmanager.tasks);
//
//        }
//        System.out.println("received state (" + taskmanager.tasks.size() + " Tasks in chat history):");
//
//        for (Task task : taskmanager.tasks) {
//            System.out.println(task);
//        }
    }

    // callback, called when a new instance joins the cluster, or an istance leaves the group (e.g. crashes)
    // 
    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    // here we receive messages from the group. 
    @Override
    public void receive(Message msg) {
        // We know that, we will always get Xml.
        String envelopeXml = msg.getObject().toString();

        Envelope DeserializeEnvelope;

        try {
            DeserializeEnvelope = TaskSerializer.DeserializeEnvelope(envelopeXml);

            //System.out.println("Input envelope received: " + inputData);

            // We may get
        } catch (JAXBException ex) {
            System.out.println(prefix + "Failed to deserialize envelope Xml. Error message" + ex);

            return;
        }

        if (DeserializeEnvelope.command.equals("add")) {
            Task taskWithId = GetTaskWithId(DeserializeEnvelope.data.get(0).id);

            if (taskWithId == null) {

                provider.TaskManagerInstance.tasks.add(DeserializeEnvelope.data.get(0));

                try {
                    provider.PersistTaskManager();
                } catch (JAXBException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                } catch (IOException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                }

                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " added! " + "total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());
            } else {
                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " already exists and hence NOT added! " + "\n\tTotal number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());
            }
        } else if (DeserializeEnvelope.command.equals("delete")) {
            Task taskWithId = GetTaskWithId(DeserializeEnvelope.data.get(0).id);

            if (taskWithId != null) {

                provider.TaskManagerInstance.tasks.remove(taskWithId);

                try {
                    provider.PersistTaskManager();
                } catch (JAXBException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                } catch (IOException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                }

                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " deleted!" + "total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());


            } else {
                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " can not be found and hence NOT deleted!" + " Total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());

            }

        } else if (DeserializeEnvelope.command.equals("replicate")) {

            // First get the array of all tasks.
            Object[] tasks = provider.TaskManagerInstance.tasks.toArray();

            // Remove the all tasks from the task manager.
            provider.TaskManagerInstance.tasks.removeAll(provider.TaskManagerInstance.tasks);

            try {
                provider.PersistTaskManager();
            } catch (JAXBException ex) {
                System.out.println(prefix + "Failed to serialize task manager. Error message:" + ex.getMessage());
            } catch (IOException ex) {
                System.out.println(prefix + "Failed to save task manager. Error message:" + ex.getMessage());
            }

            for (int index = 0; index < tasks.length; index++) {
                SendAddTaskCommand((Task) tasks[index]);
            }

        } else if (DeserializeEnvelope.command.equals("trace")) {

            //System.out.println(prefix +" Failed to save task manager. Error message:");

            for (int index = 0; index < provider.TaskManagerInstance.tasks.size(); index++) {
            }

        }else if(DeserializeEnvelope.command.equals("execute")){
        	String taskWithId = DeserializeEnvelope.id;// .data.get(0).id);

            if (taskWithId != null) {
            	Task t = GetTaskWithId(taskWithId);
            	t.status = "executed";
            	t.required = false;

                try {
                    provider.PersistTaskManager();
                } catch (JAXBException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                } catch (IOException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                }

                System.out.println(prefix + "Task with Id:" + taskWithId + " executed!" + "total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());

            } else {
                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " can not be found and hence NOT executed!" + " Total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());
            }
        	
        }else if(DeserializeEnvelope.command.equals("request")){
        	Task taskWithId = GetTaskWithId(DeserializeEnvelope.data.get(0).id);

            if (taskWithId != null) {
            	
            	taskWithId.required = true;

            	try {
                    provider.PersistTaskManager();
                } catch (JAXBException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                } catch (IOException ex) {
                    System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                }

                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " requested!" + "total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());

            } else {
                System.out.println(prefix + "Task with Id:" + DeserializeEnvelope.data.get(0).id + " can not be found and hence NOT requested!" + " Total number of tasks: "
                        + provider.TaskManagerInstance.tasks.size());
            }
        	
        }else if(DeserializeEnvelope.command.equals("get")){
    
        	int count = 0;
        	
        	for (Task t : provider.TaskManagerInstance.tasks ){
        		if(t.role.equals(DeserializeEnvelope.role)){
        			System.out.println(prefix + "Task with id:" + t.id + " belongs to " + DeserializeEnvelope.role);
        			count++;
        		}
        	}
        	System.out.println("Total number of tasks " + count);
        }
    }

    private void SendAddTaskCommand(Task task) {
        Envelope envelope = new Envelope();
        envelope.command = "add";
        envelope.data.add(task);
        WriteEnvelopeToChannel(envelope, channelTasks);
    }

    private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) {

        try {
            String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);

            Message msg = new Message(null, null, envelopeXml);

            channel.send(msg);

        } catch (Exception ex) {
            System.out.println("Failed to write task object to the channel. Error message:" + ex.getMessage());
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
