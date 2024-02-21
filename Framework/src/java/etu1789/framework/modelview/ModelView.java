/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1789.framework.modelview;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Hasinjo
 */
public class ModelView {
    String view;
    boolean toJSON = false;
    boolean invalidateSession = false;
    ArrayList<String> sessionRemove = new ArrayList<String>();

    HashMap<String, Object> data = new HashMap<String,Object>();
    HashMap<String, Object> session = new HashMap<String, Object>();

    public ModelView() {
    }

    public ModelView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public HashMap<String,Object> getData() {
        return data;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean getToJSON() {
        return this.toJSON;
    }

    public void setToJSON(boolean toJSON) {
        this.toJSON = toJSON;
    }

    public void addItem(String key, Object value) {
        data.put(key, value);
    }

    public HashMap<String, Object> getSession() {
        return this.session;
    }

    public void setSession(HashMap<String, Object> s) {
        this.session = s;
    }

    public void putToSession(String key, Object value) {
        this.session.put(key, value);
    }

    public boolean isInvalidateSession() {
        return this.invalidateSession;
    }

    public void invalidateSession() {
        this.invalidateSession = true;
    }

    public ArrayList<String> getSessionRemove() {
        return sessionRemove;
    }

    public void setSessionRemove(ArrayList<String> val) {
        this.sessionRemove = val;
    }
}

