package io.yoobi.xlsx;

import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/23/2018.
 */
public class XLSXFilter implements SheetContentsHandler
{
    private final int minColumns;
    private final ECConfig config;
    private int currentRow = -1;
    private int currentCol = -1;

    private Map<Integer, List<Cell>> tableData = new LinkedHashMap<>(); //Row - List<Cell>

    public XLSXFilter(int minColumns, ECConfig config)
    {
        this.minColumns = minColumns;
        this.config = config;
    }

    @Override
    public void startRow(int i)
    {
        currentRow = i;
        if (!tableData.containsKey(i))
        {
            tableData.put(i, new ArrayList<>());
        }
        else
        {
            System.err.println("Contains key");
        }
    }

    @Override
    public void endRow(int i)
    {
        if (tableData.containsKey(i))
        {
            //System.out.println("Pushed  = " + tableData.get(i).size() + " items");
            //TODO - clear if number of cell in a row = 0
        }
    }

    @Override
    public void cell(String s, String s1, XSSFComment xssfComment)
    {
        tableData.get(currentRow).add(new Cell(s, s1));
    }

    @Override
    public void headerFooter(String s, boolean b, String s1)
    {

    }

    public Map<Integer, List<Cell>> collectMap()
    {
        return tableData;
    }
}
