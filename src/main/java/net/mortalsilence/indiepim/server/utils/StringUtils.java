package net.mortalsilence.indiepim.server.utils;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 13.11.12
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static String getCommaSeparatedString(List<String> strings) {
        if(strings == null)
            return null;
        final StringBuffer buf = new StringBuffer();
        final Iterator<String> it = strings.iterator();
        while(it.hasNext()) {
            buf.append(it.next());
            if(it.hasNext())
                buf.append(", ");
        }
        return buf.toString();
    }

    public static String extractContentPreview(String contentText) {
        return contentText.substring(0, Math.min(contentText.length(), 500)).replace("\n", "");
    }

}
