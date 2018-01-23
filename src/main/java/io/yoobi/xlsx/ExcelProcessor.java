package io.yoobi.xlsx;

import io.yoobi.intefaces.PredictResult;
import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/22/2018.
 */
public class ExcelProcessor
{
    private ECConfig config;
    private final int minColumns;
    private OPCPackage xlsxPackage;
    private Map<Integer, Map<Integer, List<Cell>>> sheetTable = new HashMap<>();
    private Integer sheet = -1;



    private ExcelProcessor(OPCPackage pkg, int minColumns, ECConfig config)
    {
        this.xlsxPackage = pkg;
        this.minColumns = minColumns;
        this.config = config;
    }

    public static ExcelProcessor newInstance(File xlsxFile, int minColumns, ECConfig config)
    throws InvalidFormatException
    {
        OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
        return new ExcelProcessor(p, minColumns, config);
    }

    /**
     * Initiates the processing of the XLS Workbook
     *
     * @throws IOException
     * @throws OpenXML4JException
     * @throws SAXException
     */
    public Object extract() throws IOException, OpenXML4JException, SAXException
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
                ++index;

                //optional choose single or multi sheets
                if (sheet != -1 && index != sheet) continue;

                String sheetName = iter.getSheetName();
                System.out.println(">>> Scan each Sheet: " + sheetName + "[index= " + index + " ]");
                processSheet(stylesTable, strings, stream, new PredictResult()
                {
                    @Override
                    public void onListener(Map<Integer, List<Cell>> table)
                    {
                        sheetTable.put(idx, table);
                    }
                });

            }

        }

        return sheetTable.size() == 1 ? sheetTable.get(0) : sheetTable;
    }

    public ExcelProcessor getSheet(int idx)
    {
        this.sheet = idx;
        return this;
    }

    public void processSheet(StylesTable styles,
                             ReadOnlySharedStringsTable strings,
                             InputStream inputStream, PredictResult result)
            throws IOException, SAXException
    {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(inputStream);
        try
        {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            XLSXFilter xlsxFilter = new XLSXFilter(this.minColumns, this.config);
            ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, xlsxFilter, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
            result.onListener(xlsxFilter.collectMap());
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

}
