package com.fragmentime.markdownj.elements.table;

import com.fragmentime.markdownj.elements.Element;

/**
 * Created by Beancan on 2016/10/27.
 */
public class Table extends Element {

    public Table() {
        super.setType(Element.TABLE);
    }

    public static boolean isTable(String content, String content2) {
        return content != null && content.trim().startsWith("|")
                &&
                content2 != null && content2.trim().startsWith("|---");
    }

    public static boolean isTableItem(String content) {
        return content != null && content.trim().startsWith("|");
    }

    @Override
    public String render() {
        int cols = this.getData().get(1).split("\\|").length;
        StringBuilder table = new StringBuilder();

        {
            String[] header = this.getData().get(0).split("\\|", cols);
            table.append("<table>").append("\n");
            table.append("<tr>").append("\n");
            for (int cIdx = 1; cIdx < cols; cIdx++) {
                table.append("<th>");
                if (cIdx >= header.length) {
                    table.append("&nbsp;");
                } else {
                    table.append(header[cIdx]);
                }
                table.append("</th>").append("\n");
            }
            table.append("</tr>").append("\n");
        }

        if (this.getData().size() > 2) {
            for (int i = 2; i < this.getData().size(); i++) {
                String[] row = this.getData().get(0).split("\\|", cols);
                table.append("<tr>").append("\n");
                for (int cIdx = 1; cIdx < cols; cIdx++) {
                    table.append("<td>");
                    if (cIdx >= row.length) {
                        table.append("&nbsp;");
                    } else {
                        table.append(row[cIdx]);
                    }
                    table.append("</td>").append("\n");
                }
                table.append("</tr>").append("\n");
            }
        }

        table.append("</table>").append("\n");
        if (this.hasLeft()) {
            table.append(this.getLeft().render());
        }
        return table.toString();
    }
}
