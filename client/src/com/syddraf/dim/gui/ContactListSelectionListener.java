/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
/**
 *
 * @author syddraf
 */
public class ContactListSelectionListener implements ListSelectionListener{

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        MainWindow window = ((MainWindow)MainWindow.getWindows()[0]);
        DIMContact dim = (DIMContact)window.getContactList().getSelectedValue();
        window.getHistoryFrame().setText(dim.getHistory());
    }
    
}
