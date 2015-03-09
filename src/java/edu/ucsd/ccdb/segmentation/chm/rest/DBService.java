package edu.ucsd.ccdb.segmentation.chm.rest;


import  java.io.*;
import  java.net.*;
import  java.sql.*;
import  java.util.*;

import  javax.sql.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
/**
 * Re-usable database connection class
 * 
 * @author Willy W. Wong
 */
public class DBService {


  static String DB_DRIVER = "org.postgresql.Driver";


  static String URL = "jdbc:postgresql://postgres.crbs.ucsd.edu/ccdbv2_2";//"jdbc:oracle:thin:@"+HOST+":"+PORT+":"+DATABASENAME;
  static String USERNAME ="";
  static String PASSWORD ="";





    // Load the driver when this class is first loaded
    static {
        try {
            Class.forName(DB_DRIVER).newInstance();
        } catch (ClassNotFoundException cnfx) {
            cnfx.printStackTrace();
        } catch (IllegalAccessException iaex) {
            iaex.printStackTrace();
        } catch (InstantiationException iex) {
            iex.printStackTrace();
        }
    }


    


    /**
     * Returns a normal connection to the database
     * 
     * @return 
     */
    public  Connection getConnection () {
       //System.out.println(debug());
         System.out.println(URL);
         try {
            return  DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }
    
    /**
     * This method is for the testing purpose only.
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args)throws Exception
    {
        DBService db = new DBService();
        Connection c =db.getConnection();
        String sql = "select * from project";
        PreparedStatement ps = c.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next())
        {
            System.out.println(rs.getString(1));
        }
    }

}

