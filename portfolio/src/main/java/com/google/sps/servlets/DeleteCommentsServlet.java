package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Servlet that deletes all Comment entities */
@WebServlet("/delete-data")
public class DeleteCommentsServlet extends HttpServlet{
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment");
    PreparedQuery results = datastore.prepare(query);

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType("text/html;");
        response.getWriter().println();
    }
    /* Deletes all Comment entities */
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        Query query = new Query("Comment");
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            datastore.delete(entity.getKey());
        }
    }
    
}