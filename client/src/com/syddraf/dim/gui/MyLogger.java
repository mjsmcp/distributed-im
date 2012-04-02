/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author syddraf
 */
public class MyLogger {
    
    private static final Logger click =Logger.getLogger("CLICK");
    private static final Logger net = Logger.getLogger("NET");
    private static MyLogger instance = null;
    private static MyLogger getInstance() {
        if(instance == null)
            instance = new MyLogger();
        return instance;
    }
    private MyLogger() {
        
    }
    public static void logClick(String click) {
        getInstance().click.log(Level.SEVERE, String.format("(%s): %s%n", Long.toString(System.currentTimeMillis()), click));
        
    }
    
    public static void logNetworkActivity(String location, String activity) {
        getInstance().net.log(Level.SEVERE, String.format("(%s)[%s]: %s%n", Long.toString(System.currentTimeMillis()), location, activity));
    }
}
