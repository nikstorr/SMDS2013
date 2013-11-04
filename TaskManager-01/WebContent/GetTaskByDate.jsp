<%-- 
    Document   : GetTaskByDate
--%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="org.jdom2.JDOMException"%>
<%@page import="org.jdom2.output.XMLOutputter"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="itu.dk.smds.e2013.common.TasksJDOMParser"%>
<%@page import="org.jdom2.Document"%>
<%@page import="java.io.InputStream"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Get Task By Date</title>
    </head>
    <body>
        <h1>Tasks with date <%= request.getParameter("date") %></h1>
        
      
        

        <textarea id="txtAreaTaskXml" cols="100" rows="30" >
        
        <%


            try {


                InputStream xmlStream = getServletContext().getResourceAsStream("/WEB-INF/task-manager-xml.xml");

                String query = "//task[@date=" + request.getParameter("date") + "]";

                Document tasksDoc = TasksJDOMParser.GetTasksByQuery(xmlStream, query);


                new XMLOutputter().output(tasksDoc, out);



            } catch (JDOMException ex) {
                Logger.getLogger(TasksJDOMParser.class.getName()).log(Level.SEVERE, null, ex);
            }


        %>


</textarea>

    </body>
</html>
