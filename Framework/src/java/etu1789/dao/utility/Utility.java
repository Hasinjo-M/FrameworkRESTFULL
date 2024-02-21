/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1789.dao.utility;

import java.sql.Date;

/**
 *
 * @author Hasinjo
 */
public class Utility {
    /**** capital mot ****/
    public static String capitalword(String str) {
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }
    
    /*** Tranforme date ***/
    public static Date castDate(String date) throws Exception{
        try {
            return Date.valueOf(date);
        } catch (Exception e) {
            throw new Exception("Date invalide");
        }
    }
}