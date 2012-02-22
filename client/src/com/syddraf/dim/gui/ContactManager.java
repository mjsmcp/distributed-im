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
    
    private ArrayList<DIMContact> onlinePinned = new ArrayList<DIMContact>();
    private ArrayList<DIMContact> onlineUnpinned = new ArrayList<DIMContact>();
    private ArrayList<DIMContact> offline = new ArrayList<DIMContact>();
    
    private ArrayList<DIMContact> generatedContacts = new ArrayList<DIMContact>();
    
    private ArrayList<DIMContact> contacts = new ArrayList<DIMContact>();
    
    private ContactManager() {
    this.add("Emily", 0, true);
    this.add("Brandon Conway", 1, false);
    this.add("Yi-Chin Sun", 1, false);
    this.add("Keiko Nakajima", 1, true);
    this.add("Julie Adams", 1, true);
    this.add("Larry Hamm", 1, true);
    this.add("Lauren Arpin", 0, false);

    }
    
    private static ContactManager instance = null;
    public static ContactManager getInstance() {
        if(instance == null)
            instance = new ContactManager();
        return instance;
    }
    
    
    public void add(String name, int status, boolean pinned) {
        DIMContact contact = new DIMContact(name, status, pinned);
        if(contact.getStatus() == 1 && contact.isPinned())
            this.onlinePinned.add(contact);
        else if(contact.getStatus() == 1 && !contact.isPinned())
            this.onlineUnpinned.add(contact);
        else
            this.offline.add(contact);
        
        this.invalidateList();
        
    }
    
    private void invalidateList() {
        this.generatedContacts.clear();
        this.generatedContacts.addAll(this.onlinePinned);
        this.generatedContacts.addAll(this.onlineUnpinned);
        
        this.generatedContacts.addAll(this.offline);
        
    
    }
    public void add(DIMContact contact) {
        if(contact.getStatus() == 1 && contact.isPinned())
            this.onlinePinned.add(contact);
        else if(contact.getStatus() == 1 && !contact.isPinned())
            this.onlineUnpinned.add(contact);
        else
            this.offline.add(contact);
        
        this.invalidateList();
    }
    public int size() {
        return this.generatedContacts.size();
    }
    
    public Object get(int i) {
        return this.generatedContacts.get(i);
    }
}
