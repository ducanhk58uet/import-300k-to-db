package io.yoobi;


import io.yoobi.html.HtmlUtils;
import io.yoobi.model.DataResponse;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class YoobiTest
{
    public static final File output = FileManager.getDesktopFile("etc/output.html");

    public static void main(String[] args)
    throws Exception
    {
        File file = FileManager.getDesktopFile("etc/config_1.json");
        DataResponse response = ApplyETL.execute(file);

        PrintStream stream = new PrintStream(output);
        HtmlUtils.printHtml(stream, response, 1);
        Desktop.getDesktop().open(output);
    }
}
