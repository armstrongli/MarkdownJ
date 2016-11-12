package com.fragmentime.markdownj.elements.text;

import com.fragmentime.markdownj.elements.Element;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Text extends Element {

    public Text() {
        super.setType(Element.TEXT);
    }

    public static final String REGEX_IMAGE = "!\\s{0,}\\[[^\\]]{0,}\\]\\s{0,}\\([^\\)]{0,}\\)";
    public static final String REGEX_LINK = "\\[[^\\]]{0,}\\]\\s{0,}\\([^\\)]{0,}\\)";
    public static final String REGEX_BLOCK = "`[^`]{1,}`";
    public static final String REGEX_BOLD = "(\\*\\*|__)(?=\\S)(.+?[*_]*)(?<=\\S)\\1";
    public static final String REGEX_ITALIC = "(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1";

    public static final String TEXT_BLOCK = "block";
    public static final String TEXT_IMAGE = "image";
    public static final String TEXT_LINK = "link";
    public static final String TEXT_ITALIC = "italic";
    public static final String TEXT_BOLD = "bold";

    public static boolean hasImage(String text) {
        return Pattern.compile(REGEX_IMAGE).matcher(text).find();
    }

    public static List<String> splitImage(String text) {
        return split(text, REGEX_IMAGE);
    }

    public static List<String> splitLink(String text) {
        return split(text, REGEX_LINK);
    }

    public static List<String> splitBlock(String text) {
        return split(text, REGEX_BLOCK);
    }

    public static List<String> splitBold(String text) {
        return split(text, REGEX_BOLD);
    }

    public static List<String> splitItalic(String text) {
        return split(text, REGEX_ITALIC);
    }

    public static List<String> split(String text, String regex) {
        List<String> result = new ArrayList<String>();
        Matcher m = Pattern.compile(regex).matcher(text);
        int index = 0;
        int left = -1, right = -1;
        while (m.find()) {
            if (right < 0) {
                left = m.start(index);
                right = m.end(index);
                if (left > 0) {
                    result.add(text.substring(0, left));
                }
                result.add(text.substring(left, right));
            } else {
                if (m.start(index) > right) {
                    result.add(text.substring(right, m.start(index)));
                }
                left = m.start(index);
                right = m.end(index);
                result.add(text.substring(left, right));
            }
        }
        if (right < text.length() - 1 && right >= 0) {
            result.add(text.substring(right, text.length()));
        }

        return result;
    }

    public static boolean hasLink(String text) {
        return Pattern.compile(REGEX_LINK).matcher(text).find();
    }

    public static boolean hasBlock(String text) {
        return Pattern.compile(REGEX_BLOCK).matcher(text).find();
    }

    public static boolean hasBold(String text) {
        return Pattern.compile(REGEX_BOLD).matcher(text).find();
    }

    public static boolean hasItalic(String text) {
        return Pattern.compile(REGEX_ITALIC).matcher(text).find();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (String item : this.getData()) {
            sb.append(item).append(" ");
        }
        return sb.toString();
    }

    @Override
    public String render() {
        if (TEXT_BLOCK.equals(this.getType())) {
            return renderBlock();
        }
        if (TEXT_IMAGE.equals(this.getType())) {
            return renderImage();
        }
        if (TEXT_LINK.equals(this.getType())) {
            return renderLink();
        }
        if (TEXT_ITALIC.equals(this.getType())) {
            return renderItalic();
        }
        if (TEXT_BOLD.equals(this.getType())) {
            return renderBold();
        }

        StringBuilder sb = new StringBuilder("");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            for (String item : this.getData()) {
                sb.append(item).append(" ");
            }
        }
        sb.append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }

    private String renderBlock() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<span class=\"block\">");
        sb.append(this.getData().get(0));
        sb.append("</span>");

        sb.append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }

    private String renderImage() {
        StringBuilder sb = new StringBuilder("");
        String data = this.getData().get(0).trim();
        int altLeft = data.indexOf("[") + 1, altRight = data.indexOf("]"), srcLeft = data.indexOf("(") + 1, srcRight = data.indexOf(")");
        String src = data.substring(srcLeft, srcRight).replace("\"", "");
        sb.append("<image alt=\"").append(data.substring(altLeft, altRight).replace("\"", "")).append("\" ")
                .append("src=\"").append(src).append("\"/>");
        sb.append("\n");

        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }

    private String renderLink() {
        StringBuilder sb = new StringBuilder("");
        String data = this.getData().get(0).trim();
        int txtLeft = data.indexOf("[") + 1, txtRight = data.indexOf("]"), srcLeft = data.indexOf("(") + 1, srcRight = data.indexOf(")");
        String src = data.substring(srcLeft, srcRight).replace("\"", "");
        sb.append("<a ").append("href=\"").append(src).append("\">");
        // TODO the text of link can be render again cause it can include other elements
        sb.append(data.substring(txtLeft, txtRight));
        sb.append("</a>");
        sb.append("\n");

        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }

    private String renderItalic() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<i>");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            sb.append(this.getData().get(0));
        }
        sb.append("</i>");

        sb.append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }

    private String renderBold() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<b>");
        if (this.hasRight()) {
            sb.append(this.getRight().render());
        } else {
            sb.append(this.getData().get(0));
        }
        sb.append("</b>");

        sb.append("\n");
        if (this.hasLeft()) {
            sb.append(this.getLeft().render());
        }
        return sb.toString();
    }
}
