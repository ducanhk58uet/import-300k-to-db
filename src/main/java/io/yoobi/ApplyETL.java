package io.yoobi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import io.yoobi.model.LinkSheet;
import io.yoobi.model.TableData;
import io.yoobi.xlsx.ExcelBuilder;
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
    public static final File FILE_CONFIG = FileManager.getDesktopFile("etc/config_2.json");
    public static ObjectMapper mapper = new ObjectMapper();
    public static final int TC = 1;

    public static void main(String[] args)
    {
        try
        {
            ECConfig config = mapper.readValue(FILE_CONFIG, ECConfig.class);
            System.out.println("Read config: " + config);

            for (String s: config.positionAddressPattern.split("},"))
            {
                if (s.isEmpty()) continue;
                TableData tableData = new TableData(s);
                config.tableDatas.add(tableData);
            }

            for (String s: config.getLinkSheet().split("},"))
            {
                if (s.isEmpty()) continue;
                LinkSheet linkSheet = new LinkSheet(s);
                config.getLinkSheets().add(linkSheet);
            }

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
        if (!config.tableDatas.isEmpty())
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
            //Test 01: Not link sheet
            if (TC == 1)
            {
                Map<Integer, Map<Integer, List<Cell>>> dataTable = ExcelProcessor.newInstance(f, 50, config).extract();
                System.out.println("Choose sheet 1: " + dataTable.size());
                //System.out.println("Data: \n" + mapper.writeValueAsString(dataTable));
                Map<Integer, List<Cell>> results = ExcelBuilder.merge(dataTable.get(0), dataTable.get(1), config.getLinkSheets().get(0));
                System.out.println("Data: \n" + mapper.writeValueAsString(results));
            }

            //Test 02: Has linksheet
            if (TC == 2)
            {
                //ExcelProcessor.newInstance(f, 50, config).extractAngMerge();
            }

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
