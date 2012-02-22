/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;
import java.util.*;
/**
 *
 * @author syddraf
 */
public class ContactManager {
    private ArrayList<DIMContact> contacts = new ArrayList<DIMContact>();
    
    private ContactManager() {
    contacts.add(new DIMContact("Yi-Chin", 1));
    contacts.add(new DIMContact("Dr. Adams", 0));
    }
    
    private static ContactManager instance = null;
    public static ContactManager getInstance() {
        if(instance == null)
            instance = new ContactManager();
        return instance;
    }
    
    public void add(String name, int status) {
        contacts.add(new DIMContact(name, status));
        
    }
    public void add(DIMContact o) {
        contacts.add(o);
        System.out.println("Contact " + o.getName() + " added");
    }
    public int size() {
        return contacts.size();
    }
    
    public Object get(int i) {
        return this.contacts.get(i);
    }
}
