/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

public class ChatBubbleMessage {
    private final int updateCounterCreated;
    private final String author;
    private final String[] messageLines;

    public ChatBubbleMessage(String author, String[] messageLines, int created) {
        this.author = author;
        this.messageLines = messageLines;
        this.updateCounterCreated = created;
    }

    public String getAuthor() {
        return this.author;
    }

    public String[] getMessageLines() {
        return this.messageLines;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }
}
