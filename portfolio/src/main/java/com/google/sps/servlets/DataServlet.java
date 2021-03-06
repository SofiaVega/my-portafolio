// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

/** Returns an array of comments in JSON form */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private int numComments = 3;
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  UserService userService = UserServiceFactory.getUserService();

  /** Returns an array of comments in JSON of size numComments */
  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    ArrayList<String> comments = getComments();
    String json = finalJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /** 
   * Adds the comment and username from the 'new-comment' and 'username' 
   * form to the array, modifies the number of comments to return 
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    String username = request.getParameter("username");
    String comment = request.getParameter("new-comment");
    String inputNumber = request.getParameter("input-number");
    String email;
    if(userService.isUserLoggedIn()){
      email = userService.getCurrentUser().getEmail();
    } else {
      email = "null";
    }
    if (inputNumber != null) {
      numComments = Integer.parseInt(inputNumber);
    }
    if (comment != null) {
      storeComment(username, comment, email);
    }
    response.sendRedirect("/index.html");
  }

  /** Converts the comments Array to json using gson */
  private String convertToJson(ArrayList<String> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /** 
   * Builds a json string with an array of comments
   * Example:
   * { 
   *  "array": [ 
   *    { "username": "Guest", "text": "First comment", "email": "test@example.com" }, 
   *    { "username": "Guest", "text": "Second comment", "email": "test@example.com" }
   *  ] 
   * }
   */
  private String finalJson(ArrayList<String> comments){
    String json;
    json = "{ \"array\": [ ";
    for (int i=0; i< comments.size(); i++){
      json += comments.get(i);
      if(i < comments.size()-1){
        json += ", ";
      }
    }
    json += "] }";
    return json;
  }

  /** Creates and stores new comment entities */
  private void storeComment(String username, String text, String email){
    Comment comment = new Comment(text, username, email);
    Entity commentEntity = comment.getEntity();
    datastore.put(commentEntity);
  }

  /**Retrieves all comment entities from datastore and returns an array of length numComments */
  private ArrayList<String> getComments(){
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    ArrayList<String> comments = new ArrayList<String>();
    Iterable<Entity> commentsIterable = results.asIterable(FetchOptions.Builder.withLimit(numComments));
    String json;
    for(Entity comment : commentsIterable){
      Comment newComment = new Comment(comment);
      json = newComment.getJson();
      comments.add(json);
    }
    return comments;
  }
}
