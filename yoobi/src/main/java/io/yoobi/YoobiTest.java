package io.yoobi;


import io.yoobi.html.HtmlUtils;
import io.yoobi.model.Cell;
import io.yoobi.model.DataResponse;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class YoobiTest
{
    public static final File output = FileManager.getDesktopFile("etc/output.html");

    public static void main(String[] args)
    throws Exception
    {
        long startTime = System.currentTimeMillis();

        File file = FileManager.getDesktopFile("etc/config_3_multi_sheet.json");
        DataResponse response = ApplyETL.execute(file);

        System.out.println("**************** RESPONSE RESULT *********************");

        PrintStream stream = new PrintStream(output);
        if (!response.getDateList().isEmpty())
        {
            for (Date date: response.getDateList())
            {
                System.out.println("Has date: " + new SimpleDateFormat("dd/MM/yyyy").format(date));
            }
        }

        connectMultiSheet(response);

        HtmlUtils.printHtml(stream, response, 0);
        Desktop.getDesktop().open(output);

        System.out.println("Lost Time: " + (System.currentTimeMillis() - startTime));
    }

    private static void connectMultiSheet(DataResponse response)
    {
        int n = 0;
        Map<Integer, List<Cell>> results = new LinkedHashMap<>();
        for (int i = 0; i < response.getData().size(); i++)
        {
            for (Integer rowNum: response.getData().get(i).keySet())
            {
                List<Cell> cellList = response.getData().get(i).get(rowNum);
                if (cellList.size() > 0)
                {
                    results.put(n, response.getData().get(i).get(rowNum));
                }
                n++;
            }
        }

        Map<Integer, Map<Integer, List<Cell>>> sheetTable = new LinkedHashMap<>();
        sheetTable.put(0, results);
        response.setData(sheetTable);
    }
}
