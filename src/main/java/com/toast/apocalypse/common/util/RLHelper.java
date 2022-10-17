package com.toast.apocalypse.common.util;

import org.apache.commons.lang3.StringUtils;

import static net.minecraft.util.ResourceLocation.validPathChar;

/**
 * Imagine if the side stripper didn't automatically
 * generate OnlyIn annotations on every single thing
 * that only gets referenced on the client. DAMN IT!
 */
public class RLHelper {

    public static boolean isValidResourceLocation(String s) {
        String[] string = decompose(s, ':');
        return isValidNamespace(StringUtils.isEmpty(string[0]) ? "minecraft" : string[0]) && isValidPath(string[1]);
    }

    protected static String[] decompose(String s, char c) {
        String[] string = new String[]{"minecraft", s};
        int i = s.indexOf(c);
        if (i >= 0) {
            string[1] = s.substring(i + 1);
            if (i >= 1) {
                string[0] = s.substring(0, i);
            }
        }
        return string;
    }

    private static boolean isValidPath(String string) {
        for(int i = 0; i < string.length(); ++i) {
            if (!validPathChar(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    private static boolean isValidNamespace(String p_217858_0_) {
        for(int i = 0; i < p_217858_0_.length(); ++i) {
            if (!validNamespaceChar(p_217858_0_.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean validNamespaceChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }
}
