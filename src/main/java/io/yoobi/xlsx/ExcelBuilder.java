package io.yoobi.xlsx;


import io.yoobi.exception.DateParsingException;
import io.yoobi.exception.PositionNotFoundException;
import io.yoobi.model.Cell;
import io.yoobi.model.LinkSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GEMVN on 1/23/2018.
 */
public class ExcelBuilder
{
    public static Map<Integer, List<Cell>> mergeSingle(Map<Integer, List<Cell>> t1, Map<Integer, List<Cell>> t2, LinkSheet link)
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

    public static Map<Integer, List<Cell>> mergeMulti(Map<Integer, Map<Integer, List<Cell>>> sheetTable, List<LinkSheet> linkSheets)
    {
        Map<Integer, List<Cell>> results = new LinkedHashMap<>();


        for (int i = 0; i < linkSheets.size(); i++)
        {
            LinkSheet lk = linkSheets.get(i);
            if (i == 0)
            {
                results = mergeSingle(sheetTable.get(lk.getSourceSheet()), sheetTable.get(lk.getDestinationSheet()), lk);
            }
            else if (i > 0)
            {
                results = mergeSingle(results, sheetTable.get(lk.getDestinationSheet()), lk);
            }
        }

        return results;
    }

    public static boolean isValidPair(String position, String pattern)
    {
        String rxg = "^\\d+:\\w+\\d+$";
        Matcher matcher = Pattern.compile(rxg).matcher(position);
        if (!matcher.find())
        {
            return false;
        }

        if (pattern == null || pattern.isEmpty())
        {
            return false;
        }

        return true;
    }

    public static String getValueFromConfig(Map<Integer, Map<Integer, List<Cell>>> sheetTable, String position)
    throws PositionNotFoundException
    {
        String[] p = position.split(":");
        int idx = Integer.parseInt(p[0]);
        String address = p[1];
        Map<Integer, List<Cell>> table = sheetTable.get(idx);
        for (Integer rowNum: table.keySet())
        {
            for (Cell cell: table.get(rowNum))
            {
                if (cell.getAddress().equals(address))
                {
                    return cell.getValue().toString();
                }
            }
        }
        throw new PositionNotFoundException("Cannot found postion with for parse date");
    }

    public static Date parsingDateSimple(String vk, String p, final int idx)
    throws ParseException, DateParsingException
    {
        String rxg = "(\\d+(/|-)\\d+(/|-)\\d+)|(\\d+(/|-)\\d+)";
        Matcher matcher = Pattern.compile(rxg).matcher(vk);
        int index = 0;
        while (matcher.find())
        {
            if (index == idx)
            {
                return (new SimpleDateFormat(p)).parse(matcher.group());
            }
            index++;
        }

        return new SimpleDateFormat(p).parse(vk);
    }


    public static void main(String[] args)
    {
        System.out.println(isValidPair("0:A9", "dd/MM/yyyy"));
        System.out.println(isValidPair("0:AZ99", "dd/MM/yyyy"));
        System.out.println(isValidPair("1:AZ99", "dd/MM/yyyy"));
        System.out.println(isValidPair("1:AZZ999", "dd/MM/yyyy"));

        System.out.println(isValidPair("A9", "dd/MM/yyyy"));
        System.out.println(isValidPair("0", "dd/MM/yyyy"));
        System.out.println(isValidPair("0:A9:A", "dd/MM/yyyy"));
        System.out.println(isValidPair("ABC", "dd/MM/yyyy"));
        System.out.println(isValidPair("0A9", "dd/MM/yyyy"));
        System.out.println(isValidPair("0:A9", ""));
        System.out.println(isValidPair("0:9", "dd/MM/yyyy"));
        System.out.println(isValidPair("0:A", "dd/MM/yyyy"));

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
                return items;
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

    public static Map<Integer, Set<String>> mergeHeaders(Map<Integer, Set<String>> collectHeaders, List<LinkSheet> linkSheets)
    {
        Set<String> headers = collectHeaders.get(0);
        for (LinkSheet lk: linkSheets)
        {
            String column = lk.getDestinationSheet() + ":" + lk.getSourceColumn();
            for (String hk: collectHeaders.get(lk.getDestinationSheet()))
            {
                if (!hk.equals(column))
                {
                    headers.add(hk);
                }
            }
        }
        Map<Integer, Set<String>> results = new LinkedHashMap<>();
        results.put(0, headers);
        return results;
    }
}
