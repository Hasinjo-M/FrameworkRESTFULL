/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import etu1789.dao.connexion.Connexion;

/**
 *
 * @author Hasinjo
 */
public class Connexion_projet extends Connexion{
    public Connexion_projet(String base, String user, String password, String database) {
        super(base, user, password, database);
    }

    public Connexion_projet() {
        setBase("postgresql");
        setUser("postgres");
        setPassword("Hasinjo2");
        setDatabase("testscal");
        setHote("localhost");
        setPort("5432");
    }
}
