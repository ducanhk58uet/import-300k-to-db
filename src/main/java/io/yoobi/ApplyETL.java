package io.yoobi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;
import io.yoobi.xlsx.ExcelProcessor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class ApplyETL
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

            switch (config.type)
            {
                case "xlsx":
                    extractExcelXLSX(config);
                    break;
                case "xls":
                    extractExcelXLS(config);
                    break;
                case "csv":
                    extractCVS(config);
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
        if (config.tableDatas.get(0) != null)
        {
            for (TableData tableData: config.tableDatas)
            {
                if (tableData.columns.size() != config.columns.size()) { return; }
            }
        }


        //Step 1: Get extract date time with cofig
        //Step 2: Get filter data with config
        try
        {
            File f = new File(config.getPath());
            Map<Integer, List<Cell>> dataTable = (Map<Integer, List<Cell>>) ExcelProcessor.newInstance(f, -1, config).getSheet(1).extract();
            System.out.println("Choose sheet 1: " + dataTable.size());
            System.out.println("Data: \n" + mapper.writeValueAsString(dataTable));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void extractExcelXLS(ECConfig config)
    {
        //TODO
    }

    private static void extractCVS(ECConfig config)
    {
        //TODO
    }
}
