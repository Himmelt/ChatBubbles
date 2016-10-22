/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ReflectionUtils {
    public ReflectionUtils() {
    }

    public static Object getPrivateFieldByName(Object o, String fieldName) {
        Field[] fields = o.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(o);
                } catch (IllegalAccessException var5) {
                    ;
                }
            }
        }

        return null;
    }

    public static Object getPrivateFieldByType(Object o, Class objectClasstype, Class fieldClasstype) {
        return getPrivateFieldByType(o, objectClasstype, fieldClasstype, 0);
    }

    public static Object getPrivateFieldByType(Object o, Class objectClasstype, Class fieldClasstype, int index) {
        Class objectClass;
        for (objectClass = o.getClass(); !objectClass.equals(objectClasstype) && objectClass.getSuperclass() != null; objectClass = objectClass.getSuperclass()) {
            ;
        }

        int counter = 0;
        Field[] fields = objectClass.getDeclaredFields();

        for (int i = 0; i < fields.length; ++i) {
            if (fieldClasstype.equals(fields[i].getType())) {
                if (counter == index) {
                    try {
                        fields[i].setAccessible(true);
                        return fields[i].get(o);
                    } catch (IllegalAccessException var9) {
                        ;
                    }
                }

                ++counter;
            }
        }

        return null;
    }

    public static Object getFieldByName(Object o, String fieldName) {
        Field[] fields = o.getClass().getFields();

        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(o);
                } catch (IllegalAccessException var5) {
                    ;
                }
            }
        }

        return null;
    }

    public static ArrayList<Field> getFieldsByType(Object o, Class objectClassBaseType, Class fieldClasstype) {
        ArrayList matches = new ArrayList();

        for (Class objectClass = o.getClass(); !objectClass.equals(objectClassBaseType) && objectClass.getSuperclass() != null; objectClass = objectClass.getSuperclass()) {
            Field[] fields = objectClass.getDeclaredFields();

            for (int i = 0; i < fields.length; ++i) {
                if (fieldClasstype.equals(fields[i].getType())) {
                    fields[i].setAccessible(true);
                    matches.add(fields[i]);
                }
            }
        }

        return matches;
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException var2) {
            return false;
        }
    }
}
