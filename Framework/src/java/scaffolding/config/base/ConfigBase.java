/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scaffolding.config.base;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Hasinjo
 */
public class ConfigBase {
    @SerializedName("type_server")
    private String server;
    @SerializedName("hote")
    private String hote;
    @SerializedName("port")
    private String port;
    @SerializedName("base_ name")
    private String base_name;
    @SerializedName("user")
    private String user;
    @SerializedName("password")
    private String password;
    @SerializedName("path_generate")
    private String path_generate;
    @SerializedName("package_name")
    private String package_name;
    @SerializedName("language")
    private String language;
    

    
    public String getStringConnexion(){
        return  null;
    }
    
    
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getHote() {
        return hote;
    }

    public void setHote(String hote) {
        this.hote = hote;
    }

    public String getBase_name() {
        return base_name;
    }

    public void setBase_name(String base_name) {
        this.base_name = base_name;
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPath_generate() {
        return path_generate;
    }

    public void setPath_generate(String path_generate) {
        this.path_generate = path_generate;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
   
   
   
   
}

