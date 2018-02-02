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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class ApplyETL
{
    private static ObjectMapper mapper = new ObjectMapper();
    private static Map<Integer, Set<String>> headers = new LinkedHashMap<>();
    private static List<Date> dateList = new ArrayList<>();

    public static DataResponse execute(File filename)
    {

        DataResponse response = new DataResponse();
        try
        {
            ECConfig config = mapper.readValue(filename, ECConfig.class);
            System.out.println("Read config: " + config);
            Map<Integer, Map<Integer, List<Cell>>> data = execute(config);

            response = DataResponse.newInstance()
                                    .setData(data)
                                    .setMessage("SUCCESS")
                                    .setErrorCode(0)
                                    .setDateList(dateList)
                                    .setHeaders(headers).build();

            if (!dateList.isEmpty())
            {
                for (Date date: response.getDateList())
                {
                    System.out.println("Has date: " + new SimpleDateFormat("dd/MM/yyyy").format(date));
                }
            }

        }
        catch (Exception e)
        {
            response.setErrorCode(1);
            response.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public static Map<Integer, Map<Integer, List<Cell>>> execute(ECConfig config)
    throws Exception
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
            throws Exception
    {
        if(!checkMappingColumns(config))
        {
            throw new MappingColumnException("XLSX Can not mapping column with this config");
        }

        File f = new File(config.getPath());

        //init process extraction excel xlsx
        ExcelProcessor processor = ExcelProcessor.newInstance(f, config);

        //processor call extract table
        Map<Integer, Map<Integer, List<Cell>>> dataTable = processor.extract();

        //processor cell extract date
        if (config.getPositionStartDate() != null)
        {
            dateList = processor.extractDateList();
        }

        headers = processor.getCollectHeaders();
        //Not link sheet
        if (config.getLinkSheet() == null || config.getLinkSheet().isEmpty())
        {
            //processor cell extract headers

            return dataTable;
        }
        else
        {
            Map<Integer, List<Cell>> mergeTable = ExcelBuilder.mergeMulti(dataTable, config.getLinkSheets());
            Map<Integer, Map<Integer, List<Cell>>> results = new LinkedHashMap<>();
            results.put(0, mergeTable);

            headers = ExcelBuilder.mergeHeaders(headers, config.getLinkSheets());
            return results;
        }
    }

    private static Map<Integer, Map<Integer, List<Cell>>> extractExcelXLS(ECConfig config)
            throws MappingColumnException, ParsingErrorException
    {
        if(!checkMappingColumns(config))
        {
            throw new MappingColumnException("XLSX Can not mapping column with this config");
        }

        try
        {
            XLSHandler xlsHandler = XLSHandler.with(config);
            xlsHandler.process();
            Map<Integer, Map<Integer, List<Cell>>> dataTable = xlsHandler.getDataTable();

            headers = xlsHandler.getCollectHeaders();

            if (config.getLinkSheet() != null && !config.getLinkSheet().isEmpty())
            {

                Map<Integer, List<Cell>> mergeTable = ExcelBuilder.mergeMulti(dataTable, config.getLinkSheets());
                Map<Integer, Map<Integer, List<Cell>>> results = new LinkedHashMap<>();
                results.put(0, mergeTable);
                headers = ExcelBuilder.mergeHeaders(headers, config.getLinkSheets());
                return results;
            }
            else
            {
                return dataTable;
            }

        }
        catch (Exception e)
        {
            throw new ParsingErrorException(e.getMessage());
        }

    }

    private static boolean checkMappingColumns(ECConfig config)
    {
        int totalColumn = 0;
        if (!config.tableDatas.isEmpty())
        {
            for (TableData tableData: config.tableDatas)
            {
                totalColumn += tableData.getColumns().size();
            }

            if (config.getTableDatas().size() > 1)
            {
                totalColumn = totalColumn - config.getTableDatas().size() + 1;
            }

            if (totalColumn != config.columns.size())
            {
                return false;
            }
        }
        return true;
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
