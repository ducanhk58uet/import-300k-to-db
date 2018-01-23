package io.yoobi.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class TableData
{
    public int sheetIdx;
    public int startRow;
    public List<String> columns;

    public TableData(String s)
    {
        this.sheetIdx = Integer.parseInt(this.readValueByField(s, "S"));
        this.startRow = Integer.parseInt(this.readValueByField(s, "R"));
        String ex1 = StringUtils.substringBetween(s, "[", "]");
        this.columns = Arrays.asList(ex1.split(","));
    }

    private String readValueByField(String vk, String pr)
    {
        return StringUtils.substringBetween(vk, pr + ":", ",");
    }

    public int getSheetIdx()
    {
        return sheetIdx;
    }

    public void setSheetIdx(int sheetIdx)
    {
        this.sheetIdx = sheetIdx;
    }

    public int getStartRow()
    {
        return startRow;
    }

    public void setStartRow(int startRow)
    {
        this.startRow = startRow;
    }

    public List<String> getColumns()
    {
        return columns;
    }

    public void setColumns(List<String> columns)
    {
        this.columns = columns;
    }

    @Override
    public String toString()
    {
        return "TableData{" +
                "sheetIdx=" + sheetIdx +
                ", startRow=" + startRow +
                ", columns=" + columns +
                '}';
    }
}
