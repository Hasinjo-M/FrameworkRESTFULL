/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1789.framework.utilitaire;

import etu1789.dao.annotation.Column;
import etu1789.framework.annotation.AnnotationScop;
import etu1789.framework.annotation.AnnotationSession;
import etu1789.framework.annotation.AnnotationUrl;
import etu1789.framework.annotation.Authentification;
import etu1789.framework.annotation.RestApi;
import etu1789.framework.mapping.Mapping;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Hasinjo
 */
public class Utilitaire {

    public Utilitaire() {
    }
    
    // Fonction qui split et renvoye les données d'un url
    public static String getDataFromURL(String url, String methode, String nameProject) {
        Pattern pattern = Pattern.compile("/"+ nameProject +"/(.*?)(?:/\\d+)?$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return url; 
    }
    
    // Fonction qui retourne les packages/sous-package/...
    public List<String> getAllPackages(List<String> packages, String path, String debut) {
        String concat = ".";
        if(packages == null)
            packages = new ArrayList<>();
        if(debut == null) {
            debut = "";
            concat = "";
        }

        File dossier = new File(path);
        File[] files = dossier.listFiles();
        for (File file : files) {
            if(file.isDirectory()) {
                packages.add(debut + concat + file.getName());
                packages = getAllPackages(packages, file.getPath(), debut + concat + file.getName());
            }
        }

        return packages;
    }

    // Fonction qui set l'authentification d'une mapping.methode
    public String getAuthentification(Method method) {
        String ans = "*";
        if(method.isAnnotationPresent(Authentification.class)) {
            ans = method.getAnnotation(Authentification.class).required();
        }

        return ans;
    }

    // Fonction qui set le besoin de recuperer une session
    public boolean isNeedSession(Method method) {
        boolean ans = false;
        if(method.isAnnotationPresent(AnnotationSession.class)) {
            ans = true;
        }

        return ans;
    }

    // Fonction qui verifie si une mapping.methode doit retourner JSON
    public boolean isReturnJson(Method method) {
        boolean ans = false;
        if(method.isAnnotationPresent(RestApi.class)) {
            ans = true;
        }

        return ans;
    }
    

    // Fonction initialise et remplie une HashMap mappingUrl pour une package donnée
    public void addMappingUrl(HashMap<String, List<Mapping>> mappingUrls, HashMap<String, Object> classInstances, String packageName) {
        String path =  this.getClass().getClassLoader().getResource("").getPath().toString().replace("%20", " ");
        
        File pack = new File(path + packageName.replace('.', '\\'));
        File[] allClass = pack.listFiles();
        
        String[] pck = packageName.split("\\.");
        String pckName = packageName;
        if(pck.length > 0) {
            pckName = pck[pck.length - 1];
        }
        
         try {
                for(int i = 0; i < allClass.length; i++) {
                    try {
                        String className = pckName + "." + allClass[i].getName().split(".class")[0];
                        Class MyClass = Class.forName(className);
        
                        Method[] listMethode = MyClass.getDeclaredMethods();

                        //ajouter dans la list si la class doit etre singleton
                        if(MyClass.isAnnotationPresent(AnnotationScop.class)) {
                            classInstances.put(MyClass.getName(), null);
                        }

                        for(int x = 0; x < listMethode.length; x++) {
                            //ajouter dans la list si la methode est annotee AnnotationURL
                            if(listMethode[x].getAnnotation(AnnotationUrl.class) != null) {
                                Mapping mapping = new Mapping(className, listMethode[x].getName(), getAuthentification(listMethode[x]), isNeedSession(listMethode[x]), isReturnJson(listMethode[x]));
                                if(isReturnJson(listMethode[x]) == true){
                                    mapping.setMethod(getRestApiMethod(listMethode[x]));
                                    RestApi restApiAnnotation = listMethode[x].getAnnotation(RestApi.class);
                                    mapping.setParams(restApiAnnotation.params());
                                }
                                if (mappingUrls.containsKey(listMethode[x].getAnnotation(AnnotationUrl.class).url())) {
                                        // La clé existe déjà, ajoutez la nouvelle valeur à la liste existante
                                        mappingUrls.get(listMethode[x].getAnnotation(AnnotationUrl.class).url()).add(mapping);
                                } else {
                                    List<Mapping> nouvelleListe = new ArrayList<>();
                                    nouvelleListe.add(mapping);
                                    mappingUrls.put(listMethode[x].getAnnotation(AnnotationUrl.class).url(), nouvelleListe);
                                }
                                
                                
                               
                            }
                        }
                    } catch(Exception e) {
                        //e.printStackTrace();
                    }
                }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    
    // Fonction initialise et remplie une HashMap mappingUrl pour tout les packages
    public void fillMappingUrlValues(HashMap<String, List<Mapping>> mappingUrls, HashMap<String, Object> classInstances) {
        String path =  this.getClass().getClassLoader().getResource("").getPath().toString().replace("%20", " ");
        List<String> allPackage = getAllPackages(null, path, null);
         
        for(int i = 0; i < allPackage.size(); i++) {
            addMappingUrl(mappingUrls, classInstances, allPackage.get(i));
        }
    }

    // Fonction qui effectue les castes
    public  Object castToAppropriateClass(String valueInst, Class<?> classInst) {
        try {
            if(classInst.getSimpleName() == "int" || classInst.getSimpleName() == "Integer") {
                return Integer.parseInt(valueInst); // try to parse the valueInst as an integer
            } else if(classInst.getSimpleName() == "double" || classInst.getSimpleName() == "Double") {
                return Double.parseDouble(valueInst); // try to parse the valueInst as a double
            } else if(classInst.getSimpleName() == "Date") { 
                return new SimpleDateFormat("yyyy-MM-dd").parse(valueInst); // try to parse the valueInst as a date
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valueInst; // return the value as a string
    }
        
        
    public  String getPrimaryKeyColumnName(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation.primaryKey()) {
                    return columnAnnotation.libelle();
                }
            }
        }
        return null; // Aucune colonne annotée avec primaryKey = true trouvée
    }
    public String extractNumber(String url){
        Pattern pattern = Pattern.compile(".*/(\\d+)$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1); 
        }
        
        return null;
    }
    
    public boolean checkextractNumber(String url){
        Pattern pattern = Pattern.compile(".*/(\\d+)$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
    
    public String extractNumber(String url, Method m) {
        Pattern pattern = Pattern.compile(".*/(\\d+)$");
        Matcher matcher = pattern.matcher(url);
        String rep = null;
        if (matcher.find()) {
           rep =  extractNumber(url);
        }
        if(rep != null && isRestApiWithParams(m) == true){
            return rep;
        }
        return null;
        //return null; // Return a default message if no number is found
    }
    
    public  boolean isRestApiWithParams(Method method) {
        if (method.isAnnotationPresent(RestApi.class)) {
            RestApi restApiAnnotation = method.getAnnotation(RestApi.class);
            return restApiAnnotation.params();
        }
        return false;
    }
    
    
    public String getRestApiMethod(Method method) {
        if (method.isAnnotationPresent(RestApi.class)) {
                RestApi restApiAnnotation = method.getAnnotation(RestApi.class);
                return restApiAnnotation.method();
        }
        return null; 
    }
    
    public String getNameProject(String chemin){
        int indexBuildWeb = chemin.indexOf("\\build\\web\\");
        if (indexBuildWeb != -1) {
            chemin =  chemin.substring(0, indexBuildWeb);
        }
        String[] rep = chemin.split("\\\\");
        return rep[rep.length-1];
    }
    
    
}

