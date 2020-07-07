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
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns an array of comments in JSON form */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  int maxComments=3;
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  /* Returns an array of comments in JSON 
   * Size of array is maxComments */
  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    ArrayList<String[]> comments = new ArrayList<String[]>();
    int index=0;
    for (Entity entity : results.asIterable()) {
      if(index<maxComments){
        String[] str ={(String) entity.getProperty("username"),(String) entity.getProperty("text")};
        comments.add(str);
      }
      index++;
    }
    String json = convertToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
    
  }

  /* Adds the comment and username from the 'new-comment' and 'username' 
   * form to the array, modifies the numberr of comments to return 
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    String username = request.getParameter("username");
    String comment = request.getParameter("new-comment");
    String max = request.getParameter("max");
    if(max!=null){
      try {
        maxComments = Integer.parseInt(max);
      }
      catch (NumberFormatException e)
      {
        //maxComments = 3;
      }
    }
    if(comment!=null){
      storeComment(username, comment);
    }
    response.sendRedirect("/index.html");
  }

  /* Converts the comments Array to json using gson */
  private String convertToJson(ArrayList<String[]> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /* Creates and stores new comment entities */
  public void storeComment(String username, String text){
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("username", username);
    commentEntity.setProperty("text", text);
    commentEntity.setProperty("timestamp", System.currentTimeMillis());
    datastore.put(commentEntity);
  }
}
