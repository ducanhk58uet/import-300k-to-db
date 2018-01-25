package io.zinzin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GEMVN on 1/25/2018.
 */
public class ExtractDateSimple
{
    public static void main(String[] args)
    {
        String vk = "Giao dịch được trích chọn từ ngày 1/12/2017 đến 01-2018";
        String p1 = "dd/MM/yyyy";
        String p2 = "dd/MM/yyyy";

        String rxg = "(\\d+(/|-)\\d+(/|-)\\d+)|(\\d+(/|-)\\d+)";
        Matcher matcher = Pattern.compile(rxg).matcher(vk);

        int index = 0;

        while (matcher.find())
        {
            try
            {
                if (index == 0)
                {
                    //Start date
                    Date startDate = (new SimpleDateFormat(p1)).parse(matcher.group());
                }
                else if (index == 1)
                {
                    //End date
                    Date endDate = (new SimpleDateFormat(p2)).parse(matcher.group());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            index++;
        }

    }

    private static String detectDate(String p1, String p2, String value)
    {
        //System.out.println(p1.replaceAll("(dd)", ));

        return null;
    }
}
