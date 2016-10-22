/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

import com.mamiyaotaru.chatbubbles.ChatBubbleMessage;
import com.mamiyaotaru.chatbubbles.ChatParseLine;
import com.mamiyaotaru.chatbubbles.RenderPlayerChatBubbles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.entity.RenderManager;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBubbles {
    Minecraft game;
    boolean haveRenderManager = false;
    public static ChatBubbles instance;
    private ArrayList<String> newChatLines;
    private ArrayList<ChatBubbleMessage> messages;
    int maxLineLength = 30;
    boolean debug = false;
    public int MESSAGELIFETIME = 300;
    private String serverName = "";
    private TreeMap<String, ChatParseLine> customParseLines;
    private ChatParseLine customChatParseLine;
    private boolean voxelEnabled;
    public RenderPlayerChatBubbles renderPlayerChatBubbles;

    public String ModName() {
        return "MamiyaOtaru\'s ChatBubbles";
    }

    public String getVersion() {
        return "3.1.5.0";
    }

    public ChatBubbles() {
        this.customParseLines = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        this.customChatParseLine = null;
        this.voxelEnabled = false;
        this.game = Minecraft.func_71410_x();
        instance = this;
        this.newChatLines = new ArrayList();
        this.messages = new ArrayList();
        this.loadCustomParseLines();
    }

    private void loadCustomParseLines() {
        this.customParseLines.put("mc.thevoxelbox.com", new ChatParseLine("^(?:[\\{\\[\\(<](\\w{2,16})[\\}\\]\\)>]:?|\\*?(\\w{2,16}):)(.*)", "1,2", 3));
        this.customParseLines.put("play.mc-sg.org", new ChatParseLine("^(?:<[^>]*>\\s*)?(\\w{2,16})\\s*>(.*)"));
        this.customParseLines.put("play.savagerealms.net", new ChatParseLine("^(?:\\[[^\\]]*\\]\\s*)*\\s*~?(\\w{2,16})\\s*(?:\\[[^\\]]*\\])?:\\s*(.*)"));
        this.customParseLines.put("rp.fr-minecraft.net", new ChatParseLine("^\\[(?:[^>]+>)?(\\w{2,16})\\|[^\\]]*\\]\\s*(.*)"));
        File settingsFile = new File(Minecraft.func_71410_x().field_71412_D, "/mods/chatbubbles/customRegexes.txt");

        try {
            if(settingsFile.exists()) {
                BufferedReader local;
                String lines;
                String[] iterator;
                ChatParseLine entry;
                for(local = new BufferedReader(new FileReader(settingsFile)); (lines = local.readLine()) != null; this.customParseLines.put(iterator[0], entry)) {
                    iterator = lines.split(" ");
                    entry = null;
                    if(iterator.length == 2) {
                        entry = new ChatParseLine(iterator[1], 1, 2);
                    } else if(iterator.length == 3) {
                        entry = new ChatParseLine(iterator[1], iterator[2]);
                    } else if(iterator.length == 4) {
                        entry = new ChatParseLine(iterator[1], iterator[2], Integer.parseInt(iterator[3]));
                    }
                }

                local.close();
            }
        } catch (Exception var11) {
            System.out.println("regex load error: " + var11.getLocalizedMessage());
        }

        try {
            PrintWriter var12 = new PrintWriter(new FileWriter(settingsFile));
            Set var13 = this.customParseLines.entrySet();
            Iterator var14 = var13.iterator();

            while(var14.hasNext()) {
                Map.Entry var15 = (Map.Entry)var14.next();
                ChatParseLine line = (ChatParseLine)var15.getValue();
                String nameRefs = "";
                int[] nameRefsInt = line.getNameRefs();

                for(int t = 0; t < nameRefsInt.length - 1; ++t) {
                    nameRefs = nameRefs + nameRefsInt[t] + ",";
                }

                nameRefs = nameRefs + nameRefsInt[nameRefsInt.length - 1];
                var12.println((String)var15.getKey() + " " + line.getRegex() + " " + nameRefs + " " + line.getTextRef());
            }

            var12.close();
        } catch (Exception var10) {
            System.out.println("regex write error: " + var10.getLocalizedMessage());
        }

    }

    public boolean onTickInGame(Minecraft game) {
        if(!this.haveRenderManager) {
            this.loadRenderManager();
        }

        this.checkForChanges();
        if(!this.voxelEnabled) {
            ArrayList currentTime = this.newChatLines;
            synchronized(this.newChatLines) {
                Iterator i$ = this.newChatLines.iterator();

                while(i$.hasNext()) {
                    String newLine = (String)i$.next();
                    String[] authorText = this.parseLine(this.pare(this.scrubCodes(newLine)));
                    if(!authorText[0].equals("")) {
                        String[] messageLines = this.formatMessage(authorText[1]);
                        ChatBubbleMessage newMessage = new ChatBubbleMessage(authorText[0], messageLines, this.game.field_71456_v.func_73834_c());
                        this.messages.add(0, newMessage);
                    }
                }

                this.newChatLines.clear();
            }
        }

        int currentTime1 = instance.game.field_71456_v.func_73834_c();

        while(this.messages.size() > 0 && currentTime1 - ((ChatBubbleMessage)this.messages.get(this.messages.size() - 1)).getUpdatedCounter() >= this.MESSAGELIFETIME) {
            this.messages.remove(this.messages.size() - 1);
        }

        return true;
    }

    private void loadRenderManager() {
        System.out.println("getting renderer");
        RenderManager renderManager = RenderManager.field_78727_a;
        if(renderManager == null) {
            System.out.println("failed to get render manager - chatbubbles");
        } else {
            this.renderPlayerChatBubbles = new RenderPlayerChatBubbles();
            this.renderPlayerChatBubbles.func_76976_a(RenderManager.field_78727_a);
            this.haveRenderManager = true;
        }

    }

    public Object getPrivateFieldByType(Object o, Class classtype) {
        return this.getPrivateFieldByType(o, classtype, 0);
    }

    public Object getPrivateFieldByType(Object o, Class classtype, int index) {
        int counter = 0;
        Field[] fields = o.getClass().getDeclaredFields();

        for(int i = 0; i < fields.length; ++i) {
            if(classtype.equals(fields[i].getType())) {
                if(counter == index) {
                    try {
                        fields[i].setAccessible(true);
                        return fields[i].get(o);
                    } catch (IllegalAccessException var8) {
                        ;
                    }
                }

                ++counter;
            }
        }

        return null;
    }

    private void checkForChanges() {
        String serverName = "";
        if(!this.game.func_71387_A()) {
            try {
                ServerData e = null;
                Object serverDataObj = this.getPrivateFieldByType(this.game, Minecraft.class, ServerData.class);
                if(serverDataObj != null) {
                    e = (ServerData)serverDataObj;
                }

                if(e != null) {
                    serverName = e.field_78845_b;
                }
            } catch (Exception var4) {
                ;
            }

            if(serverName != null) {
                serverName = serverName.toLowerCase();
            }
        }

        if(!this.serverName.equals(serverName) && serverName != null && serverName != "") {
            this.serverName = serverName;
            this.loadCustomParseLine(serverName);
            this.voxelEnabled = false;
        }

    }

    private void loadCustomParseLine(String serverName) {
        this.customChatParseLine = (ChatParseLine)this.customParseLines.get(serverName);
    }

    private Object decode(ByteBuffer dataBuffer) {
        if(dataBuffer != null) {
            try {
                byte[] e = new byte[dataBuffer.remaining()];
                dataBuffer.get(e);
                ObjectInputStream var3 = new ObjectInputStream(new ByteArrayInputStream(e));
                Serializable var4 = (Serializable)var3.readObject();
                return var4;
            } catch (IOException var5) {
                return null;
            } catch (ClassNotFoundException var6) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void clientString(String var1) {
        if(this.debug) {
            System.out.println("incoming message: " + var1);
        }

        if(!this.voxelEnabled) {
            ArrayList var2 = this.newChatLines;
            synchronized(this.newChatLines) {
                this.newChatLines.add(var1);
            }
        }

    }

    private String pare(String string) {
        string = string.replaceAll("(.)\\1{6,}+", "$1$1$1$1$1$1");
        return string;
    }

    private String scrubCodes(String string) {
        string = string.replaceAll("(ï¿½.)", "");
        return string;
    }

    private String[] parseLine(String chatText) {
        String[] authorText = new String[]{"", ""};
        if(this.debug) {
            System.out.println(chatText);
        }

        Pattern pattern = Pattern.compile("^(?:\\[[^\\]]*\\]\\s*)*<(?:[^>]*[^>\\w])?(\\w{2,16})(?:\\s*\\([^\\)]*\\))?>+(.*)");
        if(this.debug) {
            System.out.println("check 1 A");
        }

        Matcher matcher = pattern.matcher(chatText);
        if(this.debug) {
            System.out.println("check 1 B");
        }

        if(matcher.find()) {
            if(this.debug) {
                System.out.println("check 1 C");
            }

            authorText[0] = matcher.group(1);
            authorText[1] = matcher.group(2);
            if(this.debug) {
                System.out.println("check 1 D");
            }
        } else {
            pattern = Pattern.compile("^(?:(?:\\[[^\\]]*\\]|(?:([^\\w\\s]?)([^\\w\\s])(?:(?!\\2).)+\\2\\1))\\s*)*(\\w{2,16})(?:\\s*\\([^\\)]*\\))?:(.*)");
            if(this.debug) {
                System.out.println("check 2 A");
            }

            matcher = pattern.matcher(chatText);
            if(this.debug) {
                System.out.println("check 2 B");
            }

            if(matcher.find()) {
                if(this.debug) {
                    System.out.println("check 2 C");
                }

                authorText[0] = matcher.group(3);
                authorText[1] = matcher.group(4);
                if(this.debug) {
                    System.out.println("check 2 D");
                }
            } else {
                pattern = Pattern.compile("^(?:(?:\\[[^\\]]*\\]|(?:([^\\w\\s]?)([^\\w\\s])(?:(?!\\2).)+.*\\2\\1))\\s*)*([\\W&&\\S])(\\w{2,16})\\3+(?:\\s*\\([^\\)]*\\))?:?(.*)");
                if(this.debug) {
                    System.out.println("check 3 A");
                }

                matcher = pattern.matcher(chatText);
                if(this.debug) {
                    System.out.println("check 3 B");
                }

                if(matcher.find()) {
                    if(this.debug) {
                        System.out.println("check 3 C");
                    }

                    authorText[0] = matcher.group(4);
                    authorText[1] = matcher.group(5);
                    if(this.debug) {
                        System.out.println("check 3 D");
                    }
                } else {
                    pattern = Pattern.compile("^([^\\*]?)[\\*]*\\w*\\s*(?:\\1\\[[^\\]]*\\])?[\\s]*(\\w{2,16})(?::|(?:\\s*>))(.*)");
                    if(this.debug) {
                        System.out.println("check 4 A");
                    }

                    matcher = pattern.matcher(chatText);
                    if(this.debug) {
                        System.out.println("check 4 B");
                    }

                    if(matcher.find()) {
                        if(this.debug) {
                            System.out.println("check 4 C");
                        }

                        authorText[0] = matcher.group(2);
                        authorText[1] = matcher.group(3);
                        if(this.debug) {
                            System.out.println("check 4 D");
                        }
                    } else {
                        pattern = Pattern.compile("(?:[^:]*[^:\\w])?(\\w{2,16})(?:\\s*\\([^\\)]*\\))?(?::|(?:\\s*>))(.*)");
                        if(this.debug) {
                            System.out.println("check 5 A");
                        }

                        matcher = pattern.matcher(chatText);
                        if(this.debug) {
                            System.out.println("check 5 B");
                        }

                        if(matcher.find()) {
                            if(this.debug) {
                                System.out.println("check 5 C");
                            }

                            authorText[0] = matcher.group(1);
                            authorText[1] = matcher.group(2);
                            if(this.debug) {
                                System.out.println("check 5 D");
                            }
                        }

                        if(this.debug) {
                            System.out.println("check 5 F");
                        }
                    }

                    if(this.debug) {
                        System.out.println("check 4 F");
                    }
                }

                if(this.debug) {
                    System.out.println("check 3 F");
                }
            }

            if(this.debug) {
                System.out.println("check 2 F");
            }
        }

        if(this.debug) {
            System.out.println("check 1 F");
        }

        if(this.customChatParseLine != null) {
            pattern = Pattern.compile(this.customChatParseLine.getRegex());
            if(this.debug) {
                System.out.println("check 0 A");
            }

            matcher = pattern.matcher(chatText);
            if(this.debug) {
                System.out.println("check 0 B");
            }

            if(matcher.find()) {
                if(this.debug) {
                    System.out.println("check 0 C");
                }

                int[] possibleAuthorRefs = this.customChatParseLine.getNameRefs();

                for(int t = 0; t < possibleAuthorRefs.length; ++t) {
                    String possibleAuthor = matcher.group(possibleAuthorRefs[t]);
                    if(possibleAuthor != null) {
                        authorText[0] = possibleAuthor;
                    }
                }

                authorText[1] = matcher.group(this.customChatParseLine.getTextRef());
                if(this.debug) {
                    System.out.println("check 0 D");
                }

                if(this.debug) {
                    System.out.println("author: " + authorText[0]);
                }
            }

            if(this.debug) {
                System.out.println("check 0 F");
            }
        }

        return authorText;
    }

    protected String[] formatMessage(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, " ");
        StringBuilder output = new StringBuilder(message.length());

        String word;
        for(int lineLen = 0; tokenizer.hasMoreTokens(); lineLen += word.length()) {
            word = tokenizer.nextToken();
            if(lineLen + word.length() > this.maxLineLength) {
                if(lineLen != 0) {
                    output.append("~break~");
                    lineLen = 0;
                }

                while(lineLen == 0 && word.length() > this.maxLineLength) {
                    output.append(word.substring(0, this.maxLineLength));
                    output.append("-~break~");
                    lineLen = 0;
                    word = word.substring(this.maxLineLength);
                }
            }

            if(lineLen != 0) {
                output.append(" ");
                ++lineLen;
            }

            output.append(word);
        }

        message = output.toString();
        return message.split("~break~");
    }

    public ArrayList getMessages() {
        return this.messages;
    }

    public ArrayList<ChatBubbleMessage> getMessagesByAuthor(String author) {
        ArrayList relevantMessages = new ArrayList();
        Iterator i$ = this.messages.iterator();

        while(i$.hasNext()) {
            ChatBubbleMessage message = (ChatBubbleMessage)i$.next();
            if(message.getAuthor().equals(author)) {
                relevantMessages.add(message);
            }
        }

        return relevantMessages;
    }

    public Object getPrivateFieldByType(Object o, Class objectClasstype, Class fieldClasstype) {
        return this.getPrivateFieldByType(o, objectClasstype, fieldClasstype, 0);
    }

    public Object getPrivateFieldByType(Object o, Class objectClasstype, Class fieldClasstype, int index) {
        Class objectClass;
        for(objectClass = o.getClass(); !objectClass.equals(objectClasstype) && objectClass.getSuperclass() != null; objectClass = objectClass.getSuperclass()) {
            ;
        }

        int counter = 0;
        Field[] fields = objectClass.getDeclaredFields();

        for(int i = 0; i < fields.length; ++i) {
            if(fieldClasstype.equals(fields[i].getType())) {
                if(counter == index) {
                    try {
                        fields[i].setAccessible(true);
                        return fields[i].get(o);
                    } catch (IllegalAccessException var10) {
                        ;
                    }
                }

                ++counter;
            }
        }

        return null;
    }
}
