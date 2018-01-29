package io.yoobi.xlsx;


import io.yoobi.model.Cell;
import io.yoobi.model.LinkSheet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/23/2018.
 */
public class ExcelBuilder
{
    public static Map<Integer, List<Cell>> merge(Map<Integer, List<Cell>> t1, Map<Integer, List<Cell>> t2, LinkSheet link)
    {
        Map<Integer, List<Cell>> results = new LinkedHashMap<>();

        for (Integer rowNumber: t1.keySet())
        {
            String value = getValueFromLink(t1.get(rowNumber), link.getSourceColumn());
            List<Cell> items = getOneRowByValue(t2, link.getDestinationColumn(), value);
            t1.get(rowNumber).addAll(items);
            List<Cell> cellFullList = t1.get(rowNumber);
            results.put(rowNumber, cellFullList);
        }

        return results;
    }

    private static List<Cell> getOneRowByValue(Map<Integer, List<Cell>> t2, String column, String value)
    {
        for (Integer rowNumber: t2.keySet())
        {
            String v1 = getValueFromLink(t2.get(rowNumber), column);
            if (v1.equals(value))
            {
                List<Cell> results = removeColumn(t2.get(rowNumber), column);
                return results;
            }
        }
        return new ArrayList<>();

    }

    private static List<Cell> removeColumn(List<Cell> items, String column)
    {
        for (Cell cell: items)
        {
            if (cell.getColumn().equals(column))
            {
                items.remove(cell);
            }
        }

        return items;
    }

    private static String getValueFromLink(List<Cell> cellList, String column)
    {
        for (Cell ck: cellList)
        {
            if (ck.getColumn().equals(column))
            {
                return ck.getValue().toString();
            }
        }
        return "";
    }
}
