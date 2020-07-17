package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns json with log in or log out URL */
@WebServlet("/login")
public class LoginServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json;");
        String logged;
        String url;
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            String logoutURL = userService.createLogoutURL("/");
            logged = "in";
            url=logoutURL;
        } else {
            String loginURL = userService.createLoginURL("/");
            logged = "out";
            url = loginURL;
        }
        String json = convertToJson(logged, url);
        response.getWriter().println(json);
    }

    /** 
     * Creates json: "logged" contains either "in" or "out" values
     * "url" contains login url or logout url
     */
    private String convertToJson(String logged, String url){
        String json;
        json = "{ \"logged\": \"";
        json += logged;
        json += "\", \"url\": \"";
        json += url;
        json += "\" }";
        return json;
    }
    
}
