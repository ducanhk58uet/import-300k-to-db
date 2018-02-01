package io.yoobi.xlsx;

import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.*;

/**
 * Created by GEMVN on 1/23/2018.
 */
public class XLSXHandler implements SheetContentsHandler
{
    private final int batchSize;
    private ECConfig config;
    private int currentRow = -1;
    private int currentSheet = -1;
    private Map<Integer, List<Cell>> collectMap = new LinkedHashMap<>(); //Row - List<Cell>
    private TableData tableData = null;
    private Set<String> headers = new LinkedHashSet<>();

    public XLSXHandler(int batchSize, ECConfig config, int currentSheet)
    {
        this.batchSize = batchSize;
        this.config = config;
        this.currentSheet = currentSheet;
    }

    public XLSXHandler(int batchSize, ECConfig config)
    {
        this.batchSize = batchSize;
        this.config = config;
    }

    @Override
    public void startRow(int i)
    {
        currentRow = i;
        if(collectMap.size() >= batchSize) return;

        for (TableData _table: config.getTableDatas())
        {
            if (_table.getSheetIdx() == currentSheet)
            {
                tableData = _table;
            }
        }

        if (tableData != null && tableData.getStartRow() <= currentRow)
        {
            collectMap.put(currentRow, new ArrayList<>());
        }
        else if (tableData == null)
        {
            collectMap.put(i, new ArrayList<>());
        }
    }

    @Override
    public void endRow(int i)
    {
        if (collectMap.containsKey(i))
        {
            //System.out.println("Pushed [" + i + "] = " + collectMap.get(i).size() + " items");
            //TODO - clear if number of cell in a row = 0
        }
    }

    @Override
    public void cell(String s, String s1, XSSFComment xssfComment)
    {
        if (collectMap.containsKey(currentRow))
        {
            if (tableData != null)
            {
                if (tableData.getColumns().contains(s.replaceAll("[0-9]", "")))
                {
                    Cell cell = new Cell(s ,s1);
                    collectMap.get(currentRow).add(cell);
                    headers.add(currentSheet + ":" + cell.getColumn());
                }
            }
            else
            {
                Cell cell = new Cell(s ,s1);
                collectMap.get(currentRow).add(cell);
                headers.add(currentSheet + ":" + cell.getColumn());
            }

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

    public Set<String> getHeaders()
    {
        return this.headers;
    }

}
