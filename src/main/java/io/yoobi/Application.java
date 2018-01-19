package io.yoobi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;

import java.io.File;
import java.io.IOException;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class Application
{
    public static final File FILE_CONFIG = FileManager.getDesktopFile("etc/config_1.json");
    public static ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args)
    {
        try
        {
            ECConfig config = mapper.readValue(FILE_CONFIG, ECConfig.class);
            System.out.println("Read config: " + config);

            for (String s: config.positionAddressPattern.split("},"))
            {
                TableData tableData = new TableData(s);
                config.tableDatas.add(tableData);
            }

            System.out.println("Extract position Address: " + config.tableDatas.toString());

            switch (config.type) {
                case "xlsx":
                    extractExcelXLSX(config);
                    break;
            }



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void extractExcelXLSX(ECConfig config)
    {
        if (config.tableDatas.get(0) != null && config.columns.size() != config.tableDatas.get(0).columns.size())
        {
            System.out.println("Error: Database column not mapping with Excel File");
            return;
        }
    }
}
