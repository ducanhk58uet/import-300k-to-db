package io.yoobi.xlsx;

import io.yoobi.intefaces.TypedMapping;
import io.yoobi.model.Cell;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/23/2018.
 */
public class ExcelBuilder
{
    public static void main(String[] args)
    {
        Map<Integer, List<Cell>> t1 = new LinkedHashMap<>();
        List<Cell> items = new ArrayList<>();
        items.add(new Cell("A1","123"));
        items.add(new Cell("A2", "8787"));
        t1.put(1, items);

        Map<Integer, List<Cell>> t2 = new LinkedHashMap<>();
        List<Cell> items2 = new ArrayList<>();
        items2.add(new Cell("A3","123"));
        items2.add(new Cell("A4", "8787"));
        items2.add(new Cell("A5", "8787"));
        t2.put(2, items2);

        for (Integer rowNumber: t1.keySet())
        {
            //t1.merge(rowNumber, items2, );
        }

    }

    private static List<Cell> test(List<Cell> c1, List<Cell> c2)
    {
        System.out.println(c1.size() + " -- " + c2.size());
        c1.addAll(c2);
        return c1;
    }

    public static Map<Integer, List<Cell>> merge(Map<Integer, List<Cell>> sourceTable,
                                                 Map<Integer, List<Cell>> desTable, TypedMapping type)
    {
        Map<Integer, List<Cell>> results = new LinkedHashMap<>();

        //sourceTable.containsValue()

        return results;
    }


}
