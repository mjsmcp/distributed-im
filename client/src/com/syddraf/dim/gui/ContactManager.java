/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;
import com.syddraf.dim.model.DIMMessage;
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
    
    
    public void postMessage(String name, DIMMessage msg) {
        for(int i = 0; i < this.generatedContacts.size(); ++i) {
            if(this.generatedContacts.get(i).getName().equals(name)) {
                this.generatedContacts.get(i).receiveMessage(name, msg.bodyMessage());
            }
        }
    }
    private ContactManager() {
    this.add("test_server", 1, true);
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
    
    public boolean checkIfEqual(String name) {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.generatedContacts.get(i).getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }
    
    private void invalidateList() {
        this.generatedContacts.clear();
        this.generatedContacts.addAll(this.onlinePinned);
        this.generatedContacts.addAll(this.onlineUnpinned);
        this.generatedContacts.addAll(this.offline);
        
        MainWindow window = (MainWindow)MainWindow.getWindows()[0];
        window.getContactList().repaint();
        System.out.println("Repainting");    
    }
    
    public boolean isPinned(String name) {
       for(int i = 0; i < this.onlinePinned.size(); ++i) {
            if(this.onlinePinned.get(i).getName().equals(name)) {
                return this.onlinePinned.get(i).isPinned();
            }
        }
       
       for(int i = 0; i < this.onlineUnpinned.size(); ++i) {
            if(this.onlineUnpinned.get(i).getName().equals(name)) {
                return this.onlineUnpinned.get(i).isPinned();
            }
        }
        for(int i = 0; i < this.offline.size(); ++i) {
            if(this.offline.get(i).getName().equals(name)) {
                return this.offline.get(i).isPinned();
            }
        }
        
        return false;
    }
    public void unpin(String name) {
        for(int i = 0; i < this.onlinePinned.size(); ++i) {
            if(this.onlinePinned.get(i).getName().equals(name)) {
                this.onlinePinned.get(i).unpin();
                this.onlineUnpinned.add(this.onlinePinned.get(i));
                this.onlinePinned.remove(i);
                this.invalidateList();
                return;
            }
        }
        for(int i = 0; i < this.offline.size(); ++i) {
            if(this.offline.get(i).getName().equals(name)) {
                this.offline.get(i).unpin();
                this.invalidateList();
                return;
            }
        }
    }
    
    public void pin(String name) {
        for(int i = 0; i < this.onlineUnpinned.size(); ++i) {
            if(this.onlineUnpinned.get(i).getName().equals(name)) {
                this.onlineUnpinned.get(i).pin();
                this.onlinePinned.add(this.onlineUnpinned.get(i));
                this.onlineUnpinned.remove(i);
                this.invalidateList();
                return;
            }
        }
        for(int i = 0; i < this.offline.size(); ++i) {
            if(this.offline.get(i).getName().equals(name)) {
                this.offline.get(i).pin();
                this.invalidateList();
                return;
            }
        }
        
    }
    
    public void bringOnline(String name) {
        for(int i = 0; i < this.offline.size(); ++i) {
            if(this.offline.get(i).getName().equals(name)) {
                if(this.offline.get(i).isPinned()) {
                    this.onlinePinned.add(this.offline.get(i));
                } else {
                    this.onlineUnpinned.add(this.offline.get(i));
                }
                this.invalidateList();
                return;
            }
        }
    }
    
    public void bringOffline(String name) {
        for(int i = 0; i < this.onlinePinned.size(); ++i) {
            if(this.onlinePinned.get(i).getName().equals(name)) {
                this.offline.add(this.onlinePinned.get(i));
                this.onlinePinned.remove(i);
                this.invalidateList();
            }
        }
        for(int i = 0; i < this.onlineUnpinned.size(); ++i) {
            if(this.onlineUnpinned.get(i).getName().equals(name)) {
                this.offline.add(this.onlineUnpinned.get(i));
                this.onlineUnpinned.remove(i);
                this.invalidateList();
            }
        }
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
