package io.yoobi;


import java.io.File;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class YoobiTest
{
    public static void main(String[] args)
    {
        File file = FileManager.getDesktopFile("etc/config_3.json");
        ApplyETL.execute(file);
    }
}
