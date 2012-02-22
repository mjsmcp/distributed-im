/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;

/**
 *
 * @author syddraf
 */
public class ContactManager {
    private DIMContact [] contacts = new DIMContact [] {
        new DIMContact("Yi-Chin", 1),
        new DIMContact("Dr. Adams", 0)
    };
    private ContactManager() {};
    private static ContactManager instance = null;
    public static ContactManager getInstance() {
        if(instance == null)
            instance = new ContactManager();
        return instance;
    }
    
    public int size() {
        return contacts.length;
    }
    
    public Object get(int i) {
        return this.contacts[i];
    }
}
