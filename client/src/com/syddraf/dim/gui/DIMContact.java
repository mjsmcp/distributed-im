/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;

/**
 *
 * @author syddraf
 */
public class DIMContact {
    private String contactName = "<None>";
    private int status = 0;
    private String history = "";
    
    public DIMContact(String name, int status) {
        this.contactName = name;
        this.status = status;
    }
    
    public String getName() { return contactName; }
    public int getStatus() { return status; }
    
    public String toString() {
        return this.contactName;
    }
    
    public void postMessage(String person, String message) {
        this.history = this.history + "<" + person + "> " + message + "\n";
    }
    
    public String getHistory() { return this.history; }
}
