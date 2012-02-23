/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;

import com.syddraf.dim.PreferenceManager;
import com.syddraf.dim.model.DIMMessage;
import com.syddraf.dim.model.DIMMessageBuilder;
import com.syddraf.dim.net.NetworkService;

/**
 *
 * @author syddraf
 */
public class DIMContact {
    private String contactName = "<None>";
    private int status = 0;
    private String history = "";
    private boolean pinned = false;
    
    public DIMContact(String name, int status, boolean pinned) {
        this.contactName = name;
        this.status = status;
        this.pinned = pinned;
    }
    
    public boolean isPinned() {
        return this.pinned;
    }
    public void pin() {
        this.pinned = true;
    }
    
    public void unpin() {
        this.pinned = false;
    }
    public String getName() { return contactName; }
    public int getStatus() { return status; }
    
    public String toString() {
        return this.contactName;
    }
    
    public void postMessage(String person, String message) {
        this.history = this.history + "<" + person + "> " + message + "\n";
        DIMMessage m = new DIMMessageBuilder().setFrom(PreferenceManager.getInstance().get("myName", "<None>")).setMessage(message).setTo(person).generate();
        //NetworkService.i().giveMessage(m);
    }
    
    public String getHistory() { return this.history; }
}
