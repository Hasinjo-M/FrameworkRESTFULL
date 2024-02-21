/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Hasinjo
 */
public class Main {
   public static void main(String[] args) {
        String chemin = "D:\\S5\\Naina\\test\\hufdhjkshfqjgh\\fgsjhdfgkjqsdg\\testFrameworkRESTAPI\\build\\web\\";
        String motAvantBuildWeb = getMotAvantBuildWeb(chemin);

        if (motAvantBuildWeb != null) {
            System.out.println(motAvantBuildWeb);
        } else {
            System.out.println("Le motif n'a pas été trouvé dans la chaîne.");
        }
    }

    private static String getMotAvantBuildWeb(String chemin) {
        int indexBuildWeb = chemin.indexOf("\\build\\web\\");
        if (indexBuildWeb != -1) {
            chemin =  chemin.substring(0, indexBuildWeb);
        }
        String[] rep = chemin.split("\\\\");
        return rep[rep.length-1];
    }
}
