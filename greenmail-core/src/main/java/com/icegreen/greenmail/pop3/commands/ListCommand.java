/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the Apache license 2.0
 * This file has been used and modified. Original file can be found on http://foedus.sourceforge.net
 */
package com.icegreen.greenmail.pop3.commands;

import com.icegreen.greenmail.foedus.util.MsgRangeFilter;
import com.icegreen.greenmail.pop3.Pop3Connection;
import com.icegreen.greenmail.pop3.Pop3State;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.store.SimpleStoredMessage;

import java.util.Iterator;
import java.util.List;


public class ListCommand
        extends Pop3Command {
    public boolean isValidForState(Pop3State state) {

        return state.isAuthenticated();
    }

    public void execute(Pop3Connection conn, Pop3State state,
                        String cmd) {
        try {
            MailFolder inbox = state.getFolder();
            String[] cmdLine = cmd.split(" ");
            if (cmdLine.length > 1) {
                String msgNumStr = cmdLine[1];
                List msgList = inbox.getMessages(new MsgRangeFilter(msgNumStr, false));
                if (msgList.size() != 1) {
                    conn.println("-ERR no such message");

                    return;
                }

                SimpleStoredMessage msg = (SimpleStoredMessage) msgList.get(0);
                conn.println("+OK " + msgNumStr + " " + msg.getMimeMessage().getSize());
            } else {
                List messages = inbox.getNonDeletedMessages();
                conn.println("+OK");
                for (Iterator i = messages.iterator(); i.hasNext();) {
                    SimpleStoredMessage msg = (SimpleStoredMessage) i.next();
                    conn.println(inbox.getMsn(msg.getUid()) + " " + msg.getMimeMessage().getSize());
                }

                conn.println(".");
            }
        } catch (Exception me) {
            conn.println("-ERR " + me);
        }
    }
}