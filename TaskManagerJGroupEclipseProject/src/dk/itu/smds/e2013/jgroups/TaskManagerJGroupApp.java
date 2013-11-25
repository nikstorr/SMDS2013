/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.jgroups;

import dk.itu.smds.e2013.jgroups.common.TaskProvider;
import dk.itu.smds.e2013.serialization.common.Envelope;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskSerializer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.bind.JAXBException;
import org.jgroups.JChannel;
import org.jgroups.Message;

/**
 *
 * @author rao
 */

public class TaskManagerJGroupApp {

    //private String taskXmlPath = "/Users/rao/Dropbox/PhDWork/teaching/NetBeans-Projects/lab-exercises/week-06/TaskManagerJGroupApp/src/resources/task-manager-xml.xml";
    String user_name = System.getProperty("user.name", "n/a");
    private TaskProvider provider;
    String localIp = "127.0.0.1";
    int addPort = 51924;
    int deletePort = 51925;
    int updatePart = 51926;
    
    private JChannel channelTasks;
    BufferedReader in; 
    
    public static void main(String[] args) throws Exception {
        // Start Task manager.
        new TaskManagerJGroupApp().start();
    }
    
    public void start() throws Exception {

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
        // channelTasks = ChannelHelper.getNewChannel(localIp, addPort);
        channelTasks = new JChannel();
        
        // Receiver (taskprovider, channel) 
        channelTasks.setReceiver(new TaskReceiver(provider, channelTasks));
        
        // a Group. If this is the first connect, the group will be created.
        channelTasks.connect("Add Tasks Channel");
        
        // state transfer getState(target instance (null means get the state from the coordinator/the first instance, timeout ))
        // receiverAdapter defines a callback getState() which is called on the coordinator to fetch the cluster state.
        
        channelTasks.getState(null, 10000);
        
        eventLoop();
    	// when exiting the eventLoop we exit the group channel
        channelTasks.close();
    }

    // JGroups have an EventLoop which reads messages from the input and 'multicast' it to all other instances in the group
    private void eventLoop() {
        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    	while (true) {
            try {

                System.out.println("Usage: type one of the commands: 'add', 'delete', 'update', 'replicate', 'execute', 'request', 'get' ! otherwise type 'exit' to quit!  ");
                System.out.print("> ");
                System.out.flush();
                
                String command = in.readLine().toLowerCase();
                
                // create an skeleton message
                Envelope envelope = new Envelope();

                switch (command.toLowerCase().trim()) {
                
                	case "execute":
                        System.out.println("type or paste task Xml you want to execute (in single line)!");
                        System.out.print("> ");
                		
                        String id = in.readLine();

                        //Task executeTask = TaskSerializer.DeserializeTask(id);
                        envelope.command = command;
                        envelope.id = id;
                        //envelope.data.add(executeTask);

                        // here we send the message to the group. (message, Channel)
                        WriteEnvelopeToChannel(envelope, channelTasks);
                		               		
                		break;
                		
                	case "request":
                		System.out.println("type or paste task Xml you want to request (in single line)!");
                        System.out.print("> ");
                		
                        String requestXml = in.readLine();

                        Task requestTask = TaskSerializer.DeserializeTask(requestXml);
                        envelope.command = command;
                        envelope.data.add(requestTask);

                        // here we send the message to the group. (message, Channel)
                        WriteEnvelopeToChannel(envelope, channelTasks);
                		
                		break;
                		
                	case "get":
                		System.out.println("type or paste the role you want to get (in single line)!");
                        System.out.print("> ");
                		                        
                        String role = in.readLine();

                        envelope.command = command;
                        envelope.role    = role;
                        
                        // here we send the message to the group. (message, Channel)
                        WriteEnvelopeToChannel(envelope, channelTasks);
                		
                		break;
                		
                    case "add":
                        System.out.println("type or paste task Xml you want to add (in single line)!");
                        System.out.print("> ");
                        
                        // task, as XML string
                        String taskXml = in.readLine();

                        Task addTask = TaskSerializer.DeserializeTask(taskXml);
                        envelope.command = command;
                        envelope.data.add(addTask);

                        // here we send the message to the group. (message, Channel)
                        WriteEnvelopeToChannel(envelope, channelTasks);

                        //WriteTaskToChannel(taskXml, channelAddTaks);

                        break;

                    case "update":
                        System.out.println("type or paste task Xml ");
                        System.out.print("> ");

                        String updateTaskXml = in.readLine();
                        
                        //  deserialize into a task object
                        Task updateTask = TaskSerializer.DeserializeTask(updateTaskXml);
                        // type the command
                        envelope.command = command;
                        // add the task
                        envelope.data.add(updateTask);
                        // multicast to the group
                        WriteEnvelopeToChannel(envelope, channelTasks);
                        
                        break;

                    case "delete":

                        System.out.println("type or paste task Xml you want to delete (in single line)!");
                        System.out.print("> ");

                        //Write Task To Channel
                        String taskXml2 = in.readLine();

                        Task deleteTask = TaskSerializer.DeserializeTask(taskXml2);

                        envelope.command = command;
                        envelope.data.add(deleteTask);

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;

                    case "trace":

                        envelope.command = command;

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;


                    case "replicate":

                        envelope.command = command;
                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;

                    case "exit":
                        return;

                    default:
                        //System.out.println("Usage: type one of the commands: add, delete, update !  ");
                        continue;
                }

            } catch (Exception e) {
                System.out.println("Exit from EventLoop! Error message:" + e.getMessage());
            }
        }
    }

    private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) throws Exception {

        try {
            String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);
            
            // JGroup Message:    Message( receivers address (null means send to everyone), our address (null means automatically insert it), message )
            Message msg = new Message(null, null, envelopeXml);

            channel.send(msg);
        } catch (JAXBException ex) {
            System.out.println("Failed to write task object to the channel. Error message:" + ex.getMessage());
        }
    }

    private void writeToChannel(JChannel channel, String message) throws Exception {
        message = "[" + user_name + "] " + message;

        Message msg = new Message(null, null, message);

        channel.send(msg);
    }


    
    
    
    
}
