/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1789.dao.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import scaffolding.config.base.ConfigBase;

/**
 *
 * @author Hasinjo
 */
public class Connexion {
    private String base;
    private String user;
    private String password;
    private String database;
    private String hote;
    private String port;

    public Connexion() {
    }
   
    
    public Connexion(ConfigBase config) {
        this.base = config.getServer();
        this.user = config.getUser();
        this.password = config.getPassword();
        this.database = config.getBase_name();
        this.hote = config.getHote();
        this.port = config.getPort();
    }
    
    // base de donner; user; password; database utiliser
    public Connexion(String base, String user, String password, String database) {
        this.base = base;
        this.user = user;
        this.password = password;
        this.database = database;
    }
    
    
    public Connection getconnection() throws  Exception{
        Connection connexion;
        try {
            Class.forName(this.ClassforName());
            connexion = DriverManager.getConnection(this.DriverManager(), this.user, this.password);
            connexion.setAutoCommit(false);
            return connexion;
        } catch (Exception e) {
            throw  new Exception(e.getMessage());
        }
    }

    
    private String ClassforName(){
        if("oracle".equals(this.base)){
            return "oracle.jdbc.driver.OracleDriver";
        }else if("postgresql".equals(this.base)){
            return "org.postgresql.Driver";
        }
        return null;
    }
     
    private String DriverManager(){
         if("oracle".equals(this.base)){
            return "jdbc:oracle:thin:@localhost:1521:"+this.database;
        }else if("postgresql".equals(this.base)){
            return "jdbc:postgresql://" + this.hote+ ":"+ this.port +"/"+this.database;
        }
        return null;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHote() {
        return hote;
    }

    public void setHote(String hote) {
        this.hote = hote;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
   
    
    
}

