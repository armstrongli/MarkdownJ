package com.fragmentime.markdownj.elements.block;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Block extends Element {
    @Override
    public String getType() {
        return Element.BLOCK;
    }

    public static boolean isBlock(String content) {
        return content != null && content.trim().startsWith("```");
    }

    public boolean isBlockEnd() {
        return super.getData().get(super.getData().size() - 1).trim().equals("```") && super.getData().size() > 1;
    }
}
