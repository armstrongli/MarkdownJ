package com.fragmentime.markdownj.elements.block;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Block extends Element {

    public Block() {
        super.setType(Element.BLOCK);
        super.setAcceptAnalyzed(false);
    }

    public static boolean isBlock(String content) {
        return content != null && content.trim().startsWith("```");
    }

    public boolean isBlockEnd() {
        return super.getData().get(super.getData().size() - 1).trim().equals("```") && super.getData().size() > 1;
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        String languageType = this.getData().get(0).trim().substring(3);
        sb.append("<pre><code class=\"language-").append(languageType).append("\"").append(">").append("\n");
        for (int i = 1; i < this.getData().size() - 1; i++) {
            sb.append(this.getData().get(i)).append("\n");
        }
        sb.append("</code></pre>").append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
