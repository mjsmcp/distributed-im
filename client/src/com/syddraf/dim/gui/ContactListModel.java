/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author syddraf
 */
public class ContactListModel extends DefaultListModel implements ListModel {
    ListDataListener ll = null;
    @Override
    public int getSize() {
        return ContactManager.getInstance().size();
    }

    @Override
    public Object getElementAt(int i) {
        return ContactManager.getInstance().get(i);
    }

    @Override
    public void addElement(Object o) {
        super.addElement(o);
        ContactManager.getInstance().add((DIMContact)o);
    }
    
    @Override
    public void addListDataListener(ListDataListener ll) {
       
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
       
    }
    
}
