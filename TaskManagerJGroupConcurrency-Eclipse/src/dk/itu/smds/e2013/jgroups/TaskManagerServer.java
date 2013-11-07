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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import javax.xml.bind.JAXBException;
import org.jgroups.JChannel;
import org.jgroups.Message;

import java.sql.Timestamp;
import java.util.Date;


/**
 *
 * @author Rao
 */
public class TaskManagerServer {

    private TaskProvider provider;	// serializer
    private JChannel channel;  // group
    BufferedReader in;
    private String hostProcessAddresss; 
    private String instanceName = "Task manager# ";
    private String messagePrefix = ""; 
    
    
    //____________________________________________________________________________
    
    // lock
    Object lockObject = new Object();

    // GRANTLOCK. (taskid, numOfGrants)
    Hashtable<String, Integer> grantTable = new Hashtable<String, Integer>();       
    
    public void start() throws Exception {
    	
        instanceName = instanceName + generateInstanceIndex();

        messagePrefix = instanceName + ": ";

        in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the path to task-manager-xml!");
        System.out.println(">");

        try {
            String taskXmlPath = in.readLine();

            // First load the tasks from the task manager Xml.
            provider = new TaskProvider(taskXmlPath);

        } catch (Exception ex) {
            System.out.println("Failed to load Task Manager Xml! Error message:" + ex.getMessage());
            return;
        }

        // Create channels for each opertaion.
        channel = new JChannel();
        channel.setName(instanceName);

        TaskReceiver2 receiver2 = new TaskReceiver2(provider, channel, this);

        channel.setReceiver(receiver2);
        channel.connect("Add Tasks Channel");
        channel.getState(null, 10000);
        hostProcessAddresss = channel.getAddress().toString();

        receiver2.hostProcessAddresss = hostProcessAddresss;

        System.out.println("Channel address:" + channel.getAddress().toString());

        eventLoop();

        channel.close();
    }
    
    
    int grantlocks;
    
    private void eventLoop() {

        while (true) {
            try {

                System.out.println("Usage: 'execute' | 'request' | 'trace' | 'exit' ");
                System.out.print("> ");
                System.out.flush();

                String command = in.readLine().toLowerCase();

                Envelope envelope = new Envelope();
                String taskId;

                switch (command.toLowerCase().trim()) {

                    case "execute":
                    	
                        System.out.println("type id of task you would like to execute!");
                        System.out.print("> ");

                        // get a task id
                        taskId = in.readLine();                     
                                            
                        // First execute the task at local server and then 
                        // send the envelope to channel.
                        Task exeTask = getTaskWithId(taskId);

                        if (exeTask == null) {
                            System.out.println(messagePrefix + "*** Error ***: The task with Id: " + taskId
                                    + " not found in the task manager! Therefore task can't be executed!");
                            break;
                        }

                        
                        //______________________________________
                        // number of grantlocks to receive before ececuting a task
                        grantlocks = channel.getView().size()-1;                                        
                        
                        // 'queued' the task
                        if(!grantTable.containsKey(taskId)){
                        	grantTable.put(taskId, grantlocks);
                        }    
                        
                        System.out.println("Number of grants required: " + grantlocks );
                                                
                        
                        // Ask permission
                        envelope.initiator = hostProcessAddresss;
                        envelope.lock      = "requestlock"; // why a token? Why not just a string?
                        envelope.taskId    = taskId;
                        envelope.command   = "execute";
                        
                        WriteEnvelopeToChannel(envelope, channel);

//                        exeTask.status = "executed";
//                        exeTask.required = "false";
//                        
//                        provider.PersistTaskManager();

                        
                        
                        
                        
//                        System.out.println(messagePrefix + "The task with Id: " + taskId + " executed successfully! ");

                        // send envelope to the channel for informing the changes to the other
//                        
                        //envelope.initiator = hostProcessAddresss;
//                        envelope.command = command;
//                        envelope.taskId = taskId;
//
//                        WriteEnvelopeToChannel(envelope, channel);

                        break;

                    case "request":

                        System.out.println("type id of task you would like to mark as required!");

                        System.out.print("> ");

                        //Write Task To Channel
                        taskId = in.readLine();

                        // First execute the task at local server and then 
                        // send the envelope to channel.
                        Task task = getTaskWithId(taskId);

                        if (task == null) {

                            System.out.println(messagePrefix + "*** Error ***: The task with Id: " + taskId
                                    + " not found in the task manager! Therefore task can't be marked as required!");

                            break;
                        }
                        
                        //______________________________________
                        // number of grantlocks to receive before ececuting a task
                        grantlocks = channel.getView().size()-1;                                        
                        
                        // 'queued' the task
                        grantTable.put(taskId, grantlocks);

                        
                        // Ask permission
                        envelope.initiator = hostProcessAddresss;
                        envelope.lock      = "requestlock"; // why a token? Why not just a string?
                        envelope.taskId    = taskId;
                        envelope.command   = "request";
                        
                        WriteEnvelopeToChannel(envelope, channel);

                        
//                        synchronized(lockObject){
//                        	task.required = "true";
//                        }
//                        
//                        provider.PersistTaskManager();

//                        System.out.println(messagePrefix + "The task with Id: " + taskId + " marked as required successfully! ");

                        // send envelope to the channel for informing the changes to the other
//                        envelope.initiator = hostProcessAddresss;
//
//                        envelope.command = command;
//
//                        envelope.taskId = taskId;
//
//                        WriteEnvelopeToChannel(envelope, channel);

                        break;


                    case "trace":

                        System.out.println(messagePrefix + " TaskIds : " + getTaskIds() + "");

                        break;
                    
                    
                    

                    case "exit":
                        return;

                    default:
                        System.out.println("Error: Unknown command: " + command);

                }

            } catch (Exception e) {
                System.out.println("Exit from EventLoop! Error message:" + e.getMessage());

            }
        }

    }

    public static void main(String[] args) throws Exception {

        // Start Task manager.
        new TaskManagerServer().start();
    }

    

    
    private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) throws Exception {

        try {

            // Here we make the thread to sleep for random amount of time < 1 sec,
            // so as to simulate some latency in network.
            Thread.sleep(generateRandomDelay());

            String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);

            Message msg = new Message(null, null, envelopeXml);

            channel.send(msg);

        } catch (JAXBException ex) {
            System.out.println(messagePrefix + "Failed to write task object to the channel. Error message:" + ex.getMessage());
        }

    }

    private static int generateRandomDelay() {

        Random randomGenerator = new Random();

        return randomGenerator.nextInt(50000);

    }

    private static int generateInstanceIndex() {

        Random randomGenerator = new Random();

        return randomGenerator.nextInt(50000);

    }

    private Task getTaskWithId(String taskId) {

        for (int index = 0; index < provider.TaskManagerInstance.tasks.size(); index++) {
            if (provider.TaskManagerInstance.tasks.get(index).id.equals(taskId)) {
                return provider.TaskManagerInstance.tasks.get(index);
            }
        }
        return null;
    }

    private String getTaskIds() {

        String taskIds = "";

        Iterator<Task> iterator = provider.TaskManagerInstance.tasks.iterator();

        while (iterator.hasNext()) {

            taskIds += iterator.next().id + ",";

        }

        return taskIds;
    }

}
