package io.yoobi.xlsx;

import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;
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
    private ECConfig config;
    private int currentRow = -1;
    private int currentCol = -1;
    private int currentSheet = -1;
    private Map<Integer, List<Cell>> collectMap = new LinkedHashMap<>(); //Row - List<Cell>

    public XLSXFilter(int minColumns, ECConfig config, int currentSheet)
    {
        this.minColumns = minColumns;
        this.config = config;
        this.currentSheet = currentSheet;
    }

    @Override
    public void startRow(int i)
    {
        currentRow = i;
        TableData tableData = this.config.getTableDatas().get(currentSheet);
        if (tableData != null && tableData.getStartRow() <= currentRow)
        {
            collectMap.put(currentRow, new ArrayList<>());
        }
        else if (this.config.getTableDatas().get(currentSheet) == null)
        {
            collectMap.put(i, new ArrayList<>());
        }
    }

    @Override
    public void endRow(int i)
    {
        if (collectMap.containsKey(i))
        {
            System.out.println("Pushed [" + i + "] = " + collectMap.get(i).size() + " items");
            //TODO - clear if number of cell in a row = 0
        }
    }

    @Override
    public void cell(String s, String s1, XSSFComment xssfComment)
    {
        if (collectMap.containsKey(currentRow))
        {
            collectMap.get(currentRow).add(new Cell(s, s1));
        }
    }

    @Override
    public void headerFooter(String s, boolean b, String s1)
    {

    }

    public Map<Integer, List<Cell>> collectMap()
    {
        return collectMap;
    }


}
