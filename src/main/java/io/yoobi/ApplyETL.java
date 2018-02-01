package io.yoobi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yoobi.csv.CSVFileReader;
import io.yoobi.exception.MappingColumnException;
import io.yoobi.exception.MergingErrorException;
import io.yoobi.exception.ParsingErrorException;
import io.yoobi.model.*;
import io.yoobi.xls.XLSHandler;
import io.yoobi.xlsx.ExcelBuilder;
import io.yoobi.xlsx.ExcelProcessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class ApplyETL
{
    public static ObjectMapper mapper = new ObjectMapper();

    public static DataResponse execute(File filename)
    {

        DataResponse response = new DataResponse();
        try
        {
            ECConfig config = mapper.readValue(filename, ECConfig.class);
            System.out.println("Read config: " + config);
            Map<Integer, Map<Integer, List<Cell>>> data = execute(config);
            response.setData(data);
            response.setErrorCode(0);
        }
        catch (MappingColumnException e1)
        {
            response.setErrorCode(1);
            response.setMessage(e1.getMessage());
            e1.printStackTrace();
        }
        catch (MergingErrorException e2)
        {
            response.setErrorCode(2);
            response.setMessage(e2.getMessage());
            e2.printStackTrace();
        }
        catch (IOException e3)
        {
            response.setErrorCode(3);
            response.setMessage(e3.getMessage());
            e3.printStackTrace();
        }
        catch (ParsingErrorException e4)
        {
            response.setErrorCode(4);
            response.setMessage(e4.getMessage());
            e4.printStackTrace();
        }

        return response;
    }

    public static Map<Integer, Map<Integer, List<Cell>>> execute(ECConfig config)
    throws MappingColumnException, MergingErrorException, IOException, ParsingErrorException
    {

        Map<Integer, Map<Integer, List<Cell>>> results = new LinkedHashMap<>();

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
                results = extractExcelXLSX(config);
                break;
            case "xls":
                results = extractExcelXLS(config);
                break;
            case "csv":
                results = extractCVS(config);
                break;
        }
        return results;
    }

    private static Map<Integer, Map<Integer, List<Cell>>> extractExcelXLSX(ECConfig config)
            throws MappingColumnException, ParsingErrorException
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

        try
        {
            File f = new File(config.getPath());
            Map<Integer, Map<Integer, List<Cell>>> dataTable = ExcelProcessor.newInstance(f, config).extract();
            //Not link sheet
            if (config.getLinkSheet() == null || config.getLinkSheet().isEmpty())
            {
                return dataTable;
            }
            else
            {
                //TODO - need handle implement merging
                Map<Integer, List<Cell>> results = new LinkedHashMap<>();
//                for (LinkSheet lk: config.getLinkSheets())
//                {
//                    ExcelBuilder.merge(dataTable.get(lk.getSourceColumn()), dataTable.get(lk.getDestinationSheet()), lk);
//                }

                return null;
            }

        }
        catch (Exception e)
        {
            throw new ParsingErrorException(e.getMessage());
        }
    }

    private static Map<Integer, Map<Integer, List<Cell>>> extractExcelXLS(ECConfig config)
            throws MappingColumnException, ParsingErrorException
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
            XLSHandler xlsHandler = XLSHandler.with(config.getPath());
            xlsHandler.process();
            Map<Integer, Map<Integer, List<Cell>>> results = xlsHandler.getDataTable();
            //TODO - need handle implement merging
            return results;
        }
        catch (Exception e)
        {
            throw new ParsingErrorException(e.getMessage());
        }

    }

    private static Map<Integer, Map<Integer, List<Cell>>> extractCVS(ECConfig config)
    throws MappingColumnException, MergingErrorException, ParsingErrorException
    {
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
            CSVFileReader csv = new CSVFileReader(config);
            Map<Integer, Map<Integer, List<Cell>>> results = new HashMap<>();
            results.put(0, csv.readRows());
            return results;
        }
        catch (Exception e)
        {
            throw new ParsingErrorException(e.getMessage());
        }

    }
}
