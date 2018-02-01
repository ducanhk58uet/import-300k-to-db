package io.yoobi.html;

import io.yoobi.model.Cell;
import io.yoobi.model.DataResponse;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 2/1/2018.
 */
public class HtmlUtils
{
    public static void printHtml(PrintStream out, DataResponse response)
    {
        printHtml(out, response, 0);
    }

    public static void printHtml(PrintStream out, DataResponse response, int idx)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head></head><body>");

        sb.append("<table border=1>");

        /**
         * Print table headers
         */
        sb.append("<thead>");
        sb.append("<tr>");
        response.getHeaders().forEach(head -> sb.append("<th>" + head + "</th>"));
        sb.append("</tr>");
        sb.append("</thead><tbody>");

        /**
         * Print table data
         */
        Map<Integer, List<Cell>> data = response.getData().get(idx);
        data.keySet().forEach(k ->
        {
            sb.append("<tr>");
            data.get(k).forEach(cell -> sb.append("<td>" + cell.getValue() + "</td>"));
            sb.append("</tr>");
        });

        sb.append("</tbody></table>");
        sb.append("</body></html>");

        out.println(sb.toString());
        out.close();
    }
}
