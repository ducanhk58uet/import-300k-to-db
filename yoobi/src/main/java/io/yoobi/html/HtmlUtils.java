package io.yoobi.html;

import io.yoobi.model.Cell;
import io.yoobi.model.DataResponse;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        if (response.getErrorCode() == 0)
        {
            successBuilder(sb, response, idx);
        }
        else
        {
            errorBuilder(sb, response);
        }

        sb.append("</body></html>");

        out.println(sb.toString());
        out.close();
    }

    private static void successBuilder(StringBuilder sb, DataResponse response, int idx)
    {
        sb.append("<h3>There are " + response.getData().get(idx).size() + " items</h3>");
        sb.append("<table border=1>");
        /**
         * Print table headers
         */
        sb.append("<thead>");
        sb.append("<tr>");
        if (response.getHeaders() != null)
        {
            Set<String> headers = response.getHeaders().get(idx);
            headers.forEach(h -> sb.append("<th style='background-color: grey;'>" + h + "</th>"));
        }

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
    }

    private static void errorBuilder(StringBuilder sb, DataResponse response)
    {
        sb.append("<h4>" + response.getMessage() + "</h4>");
    }
}
