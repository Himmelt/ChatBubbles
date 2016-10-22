/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

public class ChatParseLine {
    private final String regex;
    private final int[] nameRefs;
    private final int textRef;

    public ChatParseLine(String regex) {
        this.regex = regex;
        this.nameRefs = new int[]{1};
        this.textRef = 2;
    }

    public ChatParseLine(String regex, int nameRef, int textRef) {
        this.regex = regex;
        this.nameRefs = new int[]{nameRef};
        this.textRef = textRef;
    }

    public ChatParseLine(String regex, String nameRef) {
        this.regex = regex;
        String[] nameRefsString = nameRef.split(",");
        this.nameRefs = new int[nameRefsString.length];

        for(int t = 0; t < nameRefsString.length; ++t) {
            this.nameRefs[t] = Integer.parseInt(nameRefsString[t]);
        }

        this.textRef = this.nameRefs[this.nameRefs.length - 1] + 1;
    }

    public ChatParseLine(String regex, String nameRef, int textRef) {
        this.regex = regex;
        String[] nameRefsString = nameRef.split(",");
        this.nameRefs = new int[nameRefsString.length];

        for(int t = 0; t < nameRefsString.length; ++t) {
            this.nameRefs[t] = Integer.parseInt(nameRefsString[t]);
        }

        this.textRef = textRef;
    }

    public String getRegex() {
        return this.regex;
    }

    public int getTextRef() {
        return this.textRef;
    }

    public int[] getNameRefs() {
        return this.nameRefs;
    }
}
