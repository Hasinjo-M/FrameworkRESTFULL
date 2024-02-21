/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1789.framework.servlet;

import com.google.gson.Gson;
import etu1789.framework.mapping.Mapping;
import etu1789.framework.modelview.ModelView;
import etu1789.framework.upload.FileUpload;
import etu1789.framework.utilitaire.Utilitaire;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Hasinjo
 */
@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, List<Mapping>> mappingUrls;
    HashMap<String, Object> classInstances;
    private static String nameProject;
    
     public void afficherContenuMappingUrls() {
        for (Map.Entry<String, List<Mapping>> entry : mappingUrls.entrySet()) {
            String cle = entry.getKey();
            List<Mapping> valeurs = entry.getValue();

            System.out.println("Clé : " + cle);
            for (Mapping valeur : valeurs) {
                System.out.println("    className : " + valeur.getClassName());
                System.out.println("    methode : " + valeur.getMethode());
                System.out.println("    authentification : " + valeur.getAuthentification());
                System.out.println("    needSession : " + valeur.isNeedSession());
                System.out.println("    returnJson : " + valeur.returnJson());
                System.out.println("    method : " + valeur.getMethod());
                 System.out.println("   aprams : " + valeur.isParams());
                System.out.println();
            }
        }
    }
    
    public String setterName(String attr) {
        return "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
    }

    // La fonction qui caste
    public void callSetter(Method theSetter, Object objectInst, Object objectVal) {
        try {
            if (objectVal instanceof Integer) {
                //int intValue = (int) objectVal; // cast to int
                theSetter.invoke(objectInst, (int) objectVal);
            } else if (objectVal instanceof Double) {
                //double doubleValue = (double) objectVal; // cast to double
                theSetter.invoke(objectInst, (double) objectVal);
            } else if (objectVal instanceof Date) {
                //Date dateValue = (Date) objectVal; // cast to Date
                theSetter.invoke(objectInst, (Date) objectVal);
            } else {
                //String stringValue = (String) objectVal; // cast to String
                theSetter.invoke(objectInst, (String) objectVal);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }

    // Instancier les attributs d'un objet
    public void fillAttribute(HttpServletRequest request, Object objectInst, boolean need) {
        try {
            Field[] attributs = objectInst.getClass().getDeclaredFields();
            for(Field field : attributs) {
                // Envoyer la session http si la methode est annotée
                if(field.getName() == "session" && need) {
                    Method setter = objectInst.getClass().getMethod(setterName(field.getName()), field.getType());
                    Object session = getSessionAttribute(request);
                    setter.invoke(objectInst, session);
                }

                String value = request.getParameter(field.getName());
                if(value != null) {
                    Method setter = objectInst.getClass().getMethod(setterName(field.getName()), field.getType());
                    Object attr = new Utilitaire().castToAppropriateClass(value, field.getType());
                    callSetter(setter, objectInst, attr);
                }
                try {
                    // Instancier la fileUpload si il y en a 
                    if( request.getPart(field.getName()) != null) {
                        Part file = request.getPart(field.getName());
                        
                        Method setter = objectInst.getClass().getMethod(setterName(field.getName()), field.getType());
                        Object fileUpload = new FileUpload("C:\\", file.getSubmittedFileName(), partToByte(file));
                        setter.invoke(objectInst, fileUpload);
                    }
                } catch (Exception e) {
                        // e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recuperer les types des parametres d'une fonction
    public Class<?>[] getParameterType(Method[] methods, String methodeName) {
        for (Method method : methods) {
            if(method.getName() == methodeName) {
                return method.getParameterTypes();
            }
        }

        return null;
    }

    // Recuperer les valeurs pour les parametres d'une fonction
    public Object[] getParameterValues(HttpServletRequest request, Parameter[] args, Class<?> obj, Method m) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Utilitaire utilitaire = new Utilitaire();
        
        if(utilitaire.isRestApiWithParams(m) == false){
            Object[] valueArgs = new Object[args.length];
            for (int i=0; i<args.length; i++) {
                valueArgs[i] = new Utilitaire().castToAppropriateClass(request.getParameter(args[i].getName()), args[i].getType());
            }
            return valueArgs;
        }
        return  new Object[0];
    }
    
    public Object getParameterValues(String value, Parameter[] args) {
        Utilitaire utilitaire = new Utilitaire();
        for (int i=0; i<args.length; i++) {
            return new Utilitaire().castToAppropriateClass(value, args[i].getType());
         }
        return  null;
    }
    

    // Convertir un Objet Part a un tableau de bite
    public byte[] partToByte(Part file) throws Exception {
        InputStream is = file.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while((bytesRead = is.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        byte[] ans = bos.toByteArray();

        bos.close();
        is.close();

        return ans;
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */

    //verifier si on doit creer ou reprendre une instance existante
    public Object apropriateClassInstance(Class<?> classInstance) throws Exception {
        if(classInstances.containsKey(classInstance.getName())) {
            if(classInstances.get(classInstance.getName()) == null) {
                classInstances.replace(classInstance.getName(), classInstance.newInstance());
            }

            return classInstances.get(classInstance.getName());
        }

        return classInstance.newInstance();
    }

    // Verifier si on doit redirectioner vers un vue ou envoyer un Json
    public void sendResponse(HttpServletRequest request, HttpServletResponse response, ModelView mv) {
        try {
            if(mv.getToJSON()) {
                Gson gson = new Gson();
                String json = gson.toJson(mv.getData());
                PrintWriter out = response.getWriter();
                out.print(json);
            } else {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/web/" + mv.getView());
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
      }
    }

    //  Recuperer les valeurs dans httpsessions
    public HashMap<String, Object> getSessionAttribute(HttpServletRequest request) {
        HashMap<String, Object> res = new HashMap<String, Object>();
        // Parcourir les sessions existantes et ajouter dans un hashmap
        Enumeration<String> attributeNames = request.getSession().getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            Object value = request.getSession().getAttribute(key);
            res.put(key, value);
        }
        return res;
    }

    // Verifier si l'utilisateur actuel est autorisée a acceder a une methode
    public boolean checkAuthentifiction(String[] profils, String required) {
        for(String profil : profils) {
            if(profil.equalsIgnoreCase(required)) {
                return true;
            }
        }
        return false;
    }
    
    private Mapping getMapping(String url,  String urlBrut, String method){
        List<Mapping> listMapping = mappingUrls.get(url);
        Utilitaire t = new Utilitaire();
        boolean check = t.checkextractNumber(urlBrut);
        if(listMapping.size() == 1) return listMapping.get(0);
        else{
            for (Mapping mapping : listMapping) {
                if(mapping.getMethod().equals(method) && check==mapping.isParams()){
                    return  mapping;
                }
            }
        }
        return null;
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String methode)
           throws ServletException, IOException {
          try {
                response.setHeader("Access-Control-Allow-Origin", "*");
                String url = new Utilitaire().getDataFromURL(request.getRequestURI(), methode, nameProject);
                String slug = url;
                // Si l'url tapée par le client existe
                if(this.mappingUrls.containsKey(slug)) {
                    Mapping relative = getMapping(url, request.getRequestURI(), methode);
                    Class<?> classInstance = Class.forName(relative.getClassName());
                    Object objectInstance = apropriateClassInstance(classInstance);
                    System.out.println(relative.returnJson()+"    "+relative.getMethod());
                    fillAttribute(request, objectInstance, relative.isNeedSession());
                    Class<?>[] functionParameters = getParameterType(classInstance.getDeclaredMethods(), relative.getMethode());
                    Method function = objectInstance.getClass().getMethod(relative.getMethode(), functionParameters);
                    Parameter[] args = function.getParameters();
                    Object[] valueArgs = getParameterValues(request, args, classInstance, function);

                    // Si la methode est accessible ou l'utilisateur est autorisée
                    if(relative.getAutentification() == "*" || checkAuthentifiction((String[]) request.getSession().getAttribute("profil"), relative.getAutentification())) {
                        if(!relative.returnJson()) {
                            ModelView view = (ModelView) function.invoke(objectInstance, valueArgs);
                            // Supprimer les sessions spécifiées
                            setSession(request, view.getSessionRemove(), view.isInvalidateSession());
                            if(!view.getSession().isEmpty()) {
                                // Ajouter ou modifier des sessions
                                addSession(request, view.getSession());
                            }
                            // Renvoyer dans request.attribute les données envoyée par le modelview si il y en a
                            for(HashMap.Entry<String, Object> entry : view.getData().entrySet()) {
                                request.setAttribute(entry.getKey(), entry.getValue());
                            }
                          
                            this.sendResponse(request, response, view);
                        } else {
                            objectInstance = getGson(request, classInstance, objectInstance);
                            Gson gson = new Gson();
                            response.setContentType("application/json"); // Définir le type de contenu comme JSON
                            response.setCharacterEncoding("UTF-8");
                            Utilitaire t = new Utilitaire();
                            PrintWriter out = response.getWriter();
                            if(t.getRestApiMethod(function).equals(methode)){
                                String pars = new Utilitaire().extractNumber(request.getRequestURI(), function);
                                if(pars != null){
                                    valueArgs = new Object[1];
                                    valueArgs[0] = getParameterValues(pars, args);
                                }
                                String json = gson.toJson(function.invoke(objectInstance, valueArgs));
                                out.print(json);
                            }else{
                                out.print("Bad request");
                            }
                            
                        }
                    } else {
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/web/error.jsp");
                        request.setAttribute("error", "Privilege " + relative.getAutentification() + " requis pour executer cette action");
                        dispatcher.forward(request, response);
                    }
               } else {
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/web/index.html");
                    //if(url.length != 1) {
                        dispatcher = request.getRequestDispatcher("/web/error.jsp");
                        request.setAttribute("error", "Erreur: url inconnue "+url+"   "+request.getRequestURI());
                    //}
                    dispatcher.forward(request, response);
               }
        } catch (Exception e) {
              e.printStackTrace();
        }
    }

   public Object getGson(HttpServletRequest request, Class clazz, Object obj) throws IOException {
    BufferedReader reader = request.getReader();
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    Gson gson = new Gson();

    // Lire les données du corps de la requête
    while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
    }

    String donneesDuCorps = stringBuilder.toString();
    if (!donneesDuCorps.isEmpty()) {
        // Désérialiser les données JSON en une instance de la classe spécifiée par clazz
        return gson.fromJson(donneesDuCorps, clazz);
    }

    return obj;
}

    
    
    // Fonction qui ajout ou modifie les sessions
    public void addSession(HttpServletRequest request, HashMap<String, Object> session) {
        Set<String> keys = session.keySet();
        
        for (String key : keys) {
            request.getSession().setAttribute(key, session.get(key));
        }
    }

    // Fonction qui supprime ou invalide les valeurs sessions spécifiées
    public void setSession(HttpServletRequest request, ArrayList<String> listSession, boolean invalidate) {
        if(invalidate) {
            request.getSession().invalidate();
        } else {
            for(String session : listSession) {
                request.getSession().removeAttribute(session);
            }
        }
    }
    
    @Override
    public  void init() {
        ServletContext context = getServletContext();
        String path = context.getRealPath("/");
        Utilitaire t = new Utilitaire();
        System.out.println("path "+path+"  "+t.getNameProject(path));
        this.nameProject = t.getNameProject(path);
        this.mappingUrls = new HashMap<String, List<Mapping>>();
        this.classInstances = new HashMap<String, Object>();
        new Utilitaire().fillMappingUrlValues(this.mappingUrls, this.classInstances);
        //afficherContenuMappingUrls();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "GET");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "POST");
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, "PUT");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Logique pour supprimer une ressource
        processRequest(request, response, "DELETE");
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // Pré-vérification CORS, répond avec les en-têtes appropriés
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400"); // Cache les résultats des pré-vérifications CORS pendant 24 heures
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "";
    }// </editor-fold>

}

