package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Iterable;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns an array of Markers in JSON form */
@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    /** Gets all Markers from the datastore and returns them as JSON */
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Query query = new Query("Marker");
        PreparedQuery results = datastore.prepare(query);
        List<Entity> markerList = results.asList(FetchOptions.Builder.withLimit(20));
        String json = listToJson(markerList);
        response.getWriter().println(json);
    }

    /**Convert all Marker entities to a json object */
    private String listToJson(List<Entity> markers){
        String json;
        json = "{ \"array\": [ ";
        for (int i=0; i< markers.size(); i++){
            json += markerToString(markers.get(i));
            if(i<markers.size()-1){
            json += ", ";
            }
        }
        json += "] }";
        return json;
    }

    /** Converts a Marker entity to a string */
    private String markerToString(Entity marker){
        String json;
        json = "{ \"lat\": ";
        json += marker.getProperty("lat");
        json += ", \"lng\": ";
        json += marker.getProperty("lng");
        json += " }";
        return json;
    }
    
    /** Creates a new Marker entity and stores it in datastore */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lng = Double.parseDouble(request.getParameter("lng"));
        Entity markerEntity = new Entity("Marker");
        markerEntity.setProperty("lat", lat);
        markerEntity.setProperty("lng", lng);
        datastore.put(markerEntity);
    }
}
