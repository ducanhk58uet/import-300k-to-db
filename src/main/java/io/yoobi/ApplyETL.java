package io.yoobi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yoobi.csv.CSVFileReader;
import io.yoobi.exception.MappingColumnException;
import io.yoobi.exception.MergingErrorException;
import io.yoobi.model.*;
import io.yoobi.xls.XLSHandler;
import io.yoobi.xlsx.ExcelProcessor;
import org.bouncycastle.asn1.ocsp.ResponseData;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class ApplyETL
{
    public static ObjectMapper mapper = new ObjectMapper();

    public static void execute(File filename)
    {
        try
        {
            ECConfig config = mapper.readValue(filename, ECConfig.class);
            System.out.println("Read config: " + config);
            execute(config);
        }
        catch (MappingColumnException e1)
        {
            e1.printStackTrace();
        }
        catch (MergingErrorException e2)
        {
            e2.printStackTrace();
        }
        catch (IOException e3)
        {
            e3.printStackTrace();
        }

    }

    public static void execute(ECConfig config)
    throws MappingColumnException, MergingErrorException, IOException
    {

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

    private static void extractExcelXLSX(ECConfig config)
    throws MappingColumnException
    {
        if (!config.tableDatas.isEmpty())
        {
            for (TableData tableData: config.tableDatas)
            {
                if (tableData.columns.size() != config.columns.size())
                {
                    throw new MappingColumnException("XLSX Can not mapping column with this config");
                }
            }
        }

        //Step 1: Get extract date time with cofig
        //Step 2: Get filter data with config
        try
        {
            File f = new File(config.getPath());
            //Not link sheet
            if (config.getLinkSheet() == null || config.getLinkSheet().isEmpty())
            {
                Map<Integer, Map<Integer, List<Cell>>> dataTable = ExcelProcessor.newInstance(f, 50, config).extract();
                System.out.println("Choose sheet 1: " + dataTable.size());

                System.out.println("Data: \n" + mapper.writeValueAsString(dataTable.get(0)));

                //Map<Integer, List<Cell>> results = ExcelBuilder.merge(dataTable.get(0), dataTable.get(1), config.getLinkSheets().get(0));
                //System.out.println("Data: \n" + mapper.writeValueAsString(results));
            }
            else
            {

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void extractExcelXLS(ECConfig config) throws MappingColumnException
    {
        if (!config.tableDatas.isEmpty())
        {
            for (TableData tableData: config.tableDatas)
            {
                if (tableData.columns.size() != config.columns.size()) {
                    throw new MappingColumnException("XLS Can not mapping column with this config");
                }
            }
        }

        try
        {
            long startTime = System.currentTimeMillis();
            XLSHandler xlsHandler = XLSHandler.with(config.getPath());
            xlsHandler.process();
            Map<Integer, List<Cell>> results = xlsHandler.getDataTable().get(0);
            PrintStream out = new PrintStream(new File("C:\\Users\\GEMVN\\Desktop\\etc\\results.json"));
            mapper.writeValue(out, results);
            out.close();
            System.out.println("Lose time: " + (System.currentTimeMillis() - startTime));
            //System.out.println("Size: \n" + results.get(0).size());
            //System.out.println("Size: \n" + results.get(1).size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static void extractCVS(ECConfig config)
    throws MappingColumnException, MergingErrorException {
        if (!config.tableDatas.isEmpty())
        {
            for (TableData tableData: config.tableDatas)
            {
                if (tableData.columns.size() != config.columns.size())
                {
                    throw new MappingColumnException("CSV Can not mapping column with this config");
                }
            }
        }

        if (config.getLinkSheet() != null && !config.getLinkSheet().isEmpty())
        {
            throw new MergingErrorException("Invalid merging CSV with linksheet");
        }

        try
        {
            CSVFileReader csv = new CSVFileReader(config, ',');
            for(String[] row: csv.readRows(50))
            {
                for (String sk: row)
                {
                    System.out.print(sk +"\t");
                }
                System.out.println("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
