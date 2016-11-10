package com.fragmentime.markdownj.elements.header;

import com.fragmentime.markdownj.elements.Element;
import com.fragmentime.markdownj.exceptions.ElementNotMatchException;

/**
 * Created by Beancan on 2016/10/26.
 */
public class Header extends Element {

    public Header() {
        super.setType(Element.HEADER);
    }

    public static boolean isHeader(String content) {
        return content != null && content.trim().length() > 0 && content.trim().startsWith("#");
    }

    @Override
    public String render() {
        char[] chars = this.getData().get(0).trim().toCharArray();
        int headerSize = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '#') {
                headerSize++;
            } else {
                break;
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<h").append(headerSize).append(">");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            sb.append(this.getData().get(0).trim().substring(headerSize).trim());
        }
        sb.append("</h").append(headerSize).append(">").append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
