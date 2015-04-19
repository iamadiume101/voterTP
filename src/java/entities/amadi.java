/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import entities.Credentials;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;

/**
 *
 * @author IKEM
 */

@Path ("/amadi")
public class amadi {
    
    //GET METHODS//
 /**
 *
 * @Return statments
 */
    @GET
    @Produces("application/json")
    public String doGet() {
        String st = getResults("SELECT * FROM ama");
        return st;
    }
 
 /**
 *
 * gets the id 
 */

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String doGet(@PathParam("id") String id) {
        String st = getResults("SELECT * FROM ama where id = ?", id);
        return st;
    }

    private String getResults(String query, String... params) {
        JsonArrayBuilder productArray = Json.createArrayBuilder();
        String xxx = new String();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            List list = new LinkedList();
            while (rs.next()) {
                       JsonObjectBuilder jsonobj = Json.createObjectBuilder()
                        .add("id", rs.getString("id"))
                        .add("Name", rs.getString("Name"))
                        .add("Email", rs.getString("Email"))
                        .add("Phone", rs.getString("Phone"))
                        .add("Party", rs.getString("Party"));
                xxx = jsonobj.build().toString();
                productArray.add(jsonobj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(amadi.class.getName()).log(Level.SEVERE, null, ex);
        }
          if (params.length == 0) {
            xxx = productArray.build().toString();
        }
        return xxx;
    }
    
    @POST
    @Consumes("application/json")
    public void doPost(String st) {
        
        JsonParser parser = Json.createParser(new StringReader(st));
        Map<String, String> ad = new HashMap<>();
        String Name = "", value;

        while (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            switch (e) {
                case KEY_NAME:
                    Name = parser.getString();
                    break;
                case VALUE_STRING:

                    value = parser.getString();
                    ad.put(Name, value);
                    break;
                case VALUE_NUMBER:
                    value = Integer.toString(parser.getInt());
                    ad.put(Name, value);
                    break;
            }
        }
        System.out.println(ad);
       Name = ad.get("Name");
        String Email = ad.get("Email");
        String Phone = ad.get("Phone");
        String Party = ad.get("Party");
        doUpdate("INSERT INTO ama (Name, Email, Phone, Party) values ( ?, ?, ?,?)", Name, Email, Phone, Party);
         //String st1 = getResults("SELECT * FROM ama");
         //return st1;
    
    }
        
          private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(amadi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
          
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public void doPut(@PathParam("id") String productID, String st) {
        JsonParser parser = Json.createParser(new StringReader(st));
        Map<String, String> ad = new HashMap<>();
        String Name = "", value;
        while (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            switch (e) {
                case KEY_NAME:
                    Name = parser.getString();
                    break;
                case VALUE_STRING:
                    value = parser.getString();
                    ad.put(Name, value);
                    break;
                case VALUE_NUMBER:
                    value = Integer.toString(parser.getInt());
                    ad.put(Name, value);
                    break;
            }
        }
        System.out.println(ad);
        String id = ad.get("id");
        Name = ad.get("Name");
        String Email = ad.get("Email");
        String Phone = ad.get("Phone");
        String Party = ad.get("Party");
        doUpdate("UPDATE ama SET id = ?, Name = ?, Email = ?, Phone = ?, Party = ? WHERE id = ?", id, Name, Email, Phone, Party, id);
    }

    
      
    @DELETE
    @Path("{id}")
    public void doDelete(@PathParam("id") String id, String st) {
        doUpdate("DELETE FROM ama WHERE id = ?", id);
 
    }
   }
