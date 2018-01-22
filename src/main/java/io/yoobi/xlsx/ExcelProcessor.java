package io.yoobi.xlsx;

import io.yoobi.model.ECConfig;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;

import java.io.InputStream;

/**
 * Created by GEMVN on 1/22/2018.
 */
public class ExcelProcessor
{
    private ECConfig config;
    private final int minColumns;
    private OPCPackage xlsxPackage;

    private ExcelProcessor(OPCPackage pkg, int minColumns, ECConfig config)
    {
        this.xlsxPackage = pkg;
        this.minColumns = minColumns;
        this.config = config;
    }

    public static ExcelProcessor newInstance(OPCPackage pkg, int minColumns, ECConfig config)
    {
        return new ExcelProcessor(pkg, minColumns, config);
    }

    public void processSheet(StylesTable styles,
                             ReadOnlySharedStringsTable strings,
                             XSSFSheetXMLHandler.SheetContentsHandler sheetHandler,
                             InputStream sheetInputStream)
    {




    }

}
