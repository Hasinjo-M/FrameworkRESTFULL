/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import com.google.gson.annotations.SerializedName;
import connexion.Connexion_projet;
import etu1789.dao.Generic2;
import etu1789.dao.annotation.Column;
import etu1789.dao.annotation.Table;
import etu1789.framework.annotation.AnnotationScop;
import etu1789.framework.annotation.AnnotationSession;
import etu1789.framework.annotation.AnnotationUrl;
import etu1789.framework.annotation.RestApi;
import etu1789.framework.modelview.ModelView;
import etu1789.framework.upload.FileUpload;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Hasinjo
 */
@AnnotationScop(scop = "singleton")
@Table(libelle = "emp", base = "postgresql")
public class Emp extends Generic2{
    @Column(libelle = "idemp", primaryKey = true)
    private  Integer idEmploye;
    @Column(libelle = "nom")
    @SerializedName("nom")
    private String nom;
    private FileUpload photo;

    // Ce nom est fixé par convention pour tout les classes
    HashMap<String, Object> session = new HashMap<String, Object>();

    public Emp() {
    }

    public Emp(int idEmploye, String nom, String prenom, int age) {
        this.idEmploye = idEmploye;
        this.nom = nom;
    }

    public List<Emp> listEmp() throws SQLException {
        Connection c = null;
        try {
            c = new Connexion_projet().getconnection();
            return this.findAll(c);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            c.close();
        }
        return null;
    }
    
    @AnnotationSession
    @AnnotationUrl(url = "emp-all")
    public ModelView findAll() throws SQLException {
        ModelView view = new ModelView("page.jsp");
        view.addItem("lst", this.listEmp());
        view.addItem("test", 12);

        if(session.containsKey("idUser")) {
            System.out.println("**YES ITS'CONTAINS" + session.get("idUser"));
        }        
        return view;
    }

    @RestApi(method = "GET",params = false)
    @AnnotationUrl(url = "emps")
    public List<Emp> findAllToJson() throws SQLException {
        String currentWorkingDirectory = System.getProperty("user.dir");

        System.out.println("Mety bfjhdsgj "+currentWorkingDirectory);
        
        
        return this.listEmp();
    }

    @AnnotationUrl(url = "emp-add")
    public ModelView add() {
        ModelView view = new ModelView("add.jsp");
        try {
            System.out.println("--------  Emp-Add ---------");
            System.out.println("Nom: " + this.nom);
         
        } catch(Exception ex) {
            // ex.printStackTrace();
        }
        
        return view;
    }
    
    @RestApi(method = "GET",params = true)
    @AnnotationUrl(url = "emps")
    public Emp findByIdZ(int id) throws SQLException {
        Connection c = null;
        try {
            c = new Connexion_projet().getconnection();
            Emp p = new Emp();
            p.setIdEmploye(id);
            return (Emp) p.findById(c);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            c.close();
        }
        return null;
    }

    @RestApi(method = "DELETE",params = true)
    @AnnotationUrl(url = "emps")
    public List<Emp> delete(int id) throws SQLException {
        Connection c = null;
        try {
            c = new Connexion_projet().getconnection();
            Emp p = new Emp();
            p.setIdEmploye(id);
            p.delete(c);
            c.commit();
            return  this.findAll(c);
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
        }finally{
            c.close();
        }
        return null;
    }
    
    @RestApi(method = "POST",params = false)
    @AnnotationUrl(url = "emps")
    public List<Emp> addEmp() throws SQLException {
        Connection c = null;
        try {
            c = new Connexion_projet().getconnection();
            this.create(c);
            c.commit();
            this.setNom(null);
            return  this.findAll(c);
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
        }finally{
            c.close();
        }
        return null;
    }
    
    @RestApi(method = "PUT",params = true)
    @AnnotationUrl(url = "emps")
    public List<Emp> UpdateEmp(int id) throws SQLException {
        Connection c = null;
        try {
            c = new Connexion_projet().getconnection();
            Emp p  = new Emp();
            p.setIdEmploye(id);
            p.update(c, this);
            c.commit();
            this.setNom(null);
            return  this.findAll(c);
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
        }finally{
            c.close();
        }
        return null;
    }

    public Integer getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }
    
     public void setIdEmploye(String idEmploye) throws Exception {
         try {
             setIdEmploye(Integer.parseInt(idEmploye));
         } catch (Exception e) {
             throw new Exception("erreur");
         }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public FileUpload getPhoto() {
        return photo;
    }

    public void setPhoto(FileUpload photo) {
        this.photo = photo;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

   
}

