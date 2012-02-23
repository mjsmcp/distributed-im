/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.gui;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author syddraf
 */
public class ContactRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean hasFocused) {
        
        
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, hasFocused);
        if(value instanceof DIMContact) {
            DIMContact contact = (DIMContact)value;
            String name = contact.getName();
            int status = contact.getStatus();
            switch(status) {
                case 1:
                    if(contact.isPinned())
                        label.setIcon(new ImageIcon("img/pinned.png"));
                    else
                        label.setIcon(new ImageIcon("img/online.png"));
                    
                    break;
                case 0:
                    label.setIcon(new ImageIcon("img/offline.png"));
                    break;
            }
        } else {
            label.setIcon(null);
        }
        
        return label;
    }
}
