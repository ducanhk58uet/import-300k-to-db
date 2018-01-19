package zk.springboot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GEMVN on 1/18/2018.
 */
public class ApplicationTest {

    public static void main(String[] args)
    {
        String input = "(Kỳ 2: Từ 20-12-2017 đến 01-03-2018)";
        String rxg = "\\d{2}-\\d{2}-\\d{4}";

        Matcher matcher = Pattern.compile(rxg).matcher(input);

        if (matcher.find())
        {
            System.out.println(matcher.group(0));
        }
    }
}
