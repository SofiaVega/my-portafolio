package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class Comment {
    private String text;
    private String username;
    private String email;
    private long timestamp;

    public Comment(String text, String username, String email){
        timestamp = System.currentTimeMillis();
        this.text = text;
        this.username = username;
        this.email = email;
    }
    /** 
     * Constructs a comment from a 'Comment' type entity 
     * @param commentEntity
     */
    public Comment(Entity commentEntity){
        text = (String) commentEntity.getProperty("text");
        username = (String) commentEntity.getProperty("username");
        email = (String) commentEntity.getProperty("email");
        timestamp = (long) commentEntity.getProperty("timestamp");
    }
    /** Returns a new entity of type 'Comment' */
    public Entity getEntity(){
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("username", username);
        commentEntity.setProperty("text", text);
        commentEntity.setProperty("timestamp", timestamp);
        commentEntity.setProperty("email", email);
        return commentEntity;
    }
    /**
    * Build a json string from a comment
    * Example:
    * { "username": "Guest", "text": "First Comment", 
    *   "email": "test@example.com" }
    * @return json
    */
    public String getJson(){
        String json;
        json = "{ \"username\": \"";
        json += username;
        json += "\", \"text\": \"";
        json += text;
        json += "\", \"email\": \"";
        json += email;
        json += "\" }";
        return json;
    }
}
