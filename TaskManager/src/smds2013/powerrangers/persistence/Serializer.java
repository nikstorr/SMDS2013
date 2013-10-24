package smds2013.powerrangers.persistence;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class Serializer {

	
    public static List<Task> DeserializeTaskCollection(String taskListXml) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskCollection.class);
        StringReader reader = new StringReader(taskListXml);
        // un-marshall
        List<Task> tasklist = ((TaskCollection) jaxbContext.createUnmarshaller().unmarshal(reader)).tasks;
        return tasklist;
    }

    // we may not want this to return a anything?
    public static String SerializeTaskCollection(List<Task> taskcollection) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskCollection.class);
        TaskCollection tasks = new TaskCollection(taskcollection);
        StringWriter writer = new StringWriter();
        // marshall
        jaxbContext.createMarshaller().marshal(tasks, writer);
        return writer.toString();
    }
}

