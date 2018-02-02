package io.yoobi.xlsx;

import io.yoobi.exception.DateParsingException;
import io.yoobi.exception.PositionNotFoundException;
import io.yoobi.intefaces.PredictResult;
import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * Created by GEMVN on 1/22/2018.
 */
public class ExcelProcessor
{
    private ECConfig config;
    private final int batchSize;
    private OPCPackage xlsxPackage;
    private Map<Integer, Map<Integer, List<Cell>>> sheetTable = new HashMap<>();
    private Map<Integer, Set<String>> collectHeaders = new LinkedHashMap<>();
    private List<Date> dateList = new ArrayList<>();

    private ExcelProcessor(OPCPackage pkg, ECConfig config)
    {
        this.xlsxPackage = pkg;
        this.batchSize = config.getBatchSize();
        this.config = config;
    }

    public static ExcelProcessor newInstance(File xlsxFile, ECConfig config)
    throws InvalidFormatException
    {
        OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
        return new ExcelProcessor(p, config);
    }

    /**
     * Initiates the processing of the XLS Workbook
     *
     * @throws IOException
     * @throws OpenXML4JException
     * @throws SAXException
     */
    public Map<Integer, Map<Integer, List<Cell>>> extract() throws IOException, OpenXML4JException, SAXException
    {
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable stylesTable = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;

        while (iter.hasNext())
        {
            try (InputStream stream = iter.next())
            {
                //Scan each sheet
                final int idx = index;

                String sheetName = iter.getSheetName();
                System.out.println(">>> Scan each Sheet: " + sheetName + "[index= " + index + " ]");
                this.processSheet(stylesTable, strings, stream, idx, new PredictResult()
                {
                    @Override
                    public boolean afterCheck()
                    {
                        return config.getTableDatas() == null || config.getTableDatas().isEmpty() || containsSheet(idx);
                    }

                    @Override
                    public void onListener(Map<Integer, List<Cell>> table)
                    {
                        sheetTable.put(idx, table);
                    }

                    @Override
                    public void onHeaders(Set<String> header)
                    {
                        Set<String> results = new LinkedHashSet<>();
                        for (String hk: header)
                        {
                            results.add(hk);
                        }
                        collectHeaders.put(idx, results);
                    }
                });
            }
            ++index;
        }

        return sheetTable;
    }

    private boolean containsSheet(int idx)
    {
        for (TableData tk: this.config.getTableDatas())
        {
            if (tk.getSheetIdx() == idx) return true;
        }
        return false;
    }

    private void processSheet(StylesTable styles,
                             ReadOnlySharedStringsTable strings,
                             InputStream inputStream,
                             int currentSheet,
                             PredictResult result)
            throws IOException, SAXException
    {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(inputStream);
        try
        {
            if (result.afterCheck())
            {
                XMLReader sheetParser = SAXHelper.newXMLReader();
                XLSXHandler xlsxHandler = new XLSXHandler(this.config, currentSheet);
                ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, xlsxHandler, formatter, false);
                sheetParser.setContentHandler(handler);
                sheetParser.parse(sheetSource);

                //callback map data for each sheet
                result.onListener(xlsxHandler.collectMap());

                //callback get headers
                result.onHeaders(xlsxHandler.getHeaders());
            }
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

    public Map<Integer, Set<String>> getCollectHeaders() { return this.collectHeaders; }

    public List<Date> extractDateList()
    throws DateParsingException, PositionNotFoundException, ParseException
    {
        List<Date> dateList = new ArrayList<>();
        Date sDate = null;
        Date eDate = null;
        if (config.getPositionStartDate() == null || config.getPositionStartDate().isEmpty())
        {
            //Neu khong dien startDate, mac dinh tra ve du lieu rong
            return dateList;
        }
        else if (config.getPositionEndDate() != null && !config.getPositionEndDate().isEmpty())
        {
            //Neu da dien endDate, can kiem tra tinh hop le ve cu phap
            boolean hasEndDate = ExcelBuilder.isValidPair(config.getPositionEndDate(), config.getFormatEndDate());
            if (!hasEndDate) throw new DateParsingException("Syntax error! Cannot parse StartDate. Simple valid syntax is: '0:A9' with 0 is index of sheet and A9 is Cell");

            //Sau khi da hop le cua End Date, lay du lieu date theo pattern
            String valueDate = ExcelBuilder.getValueFromConfig(sheetTable, config.getPositionEndDate());
            eDate = ExcelBuilder.parsingDateSimple(valueDate, config.getFormatEndDate(), 1);
            dateList.add(eDate);
        }

        //Neu da dien startDate, can kiem tra tinh hop le va cu phap
        boolean hasStartDate = ExcelBuilder.isValidPair(config.getPositionStartDate(), config.getFormatStartDate());
        if (!hasStartDate) throw new DateParsingException("Syntax error! Cannot parse StartDate. Simple valid syntax is: '0:A9' with 0 is index of sheet and A9 is Cell");

        //Sau khi da kiem tra tinh hop le cua Start Date, lay du lieu start date theo pattern
        String valueDate = ExcelBuilder.getValueFromConfig(sheetTable, config.getPositionStartDate());
        sDate = ExcelBuilder.parsingDateSimple(valueDate, config.getFormatStartDate(), 0);
        dateList.add(sDate);

        return dateList;
    }

}
