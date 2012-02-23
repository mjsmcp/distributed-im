/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syddraf.dim.net;

import com.syddraf.dim.model.DIMMessage;

/**
 *
 * @author syddraf
 */
public interface NetworkMessageListener {
    public void receiveMessage(String from, DIMMessage msg);
}
