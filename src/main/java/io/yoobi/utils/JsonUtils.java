package io.yoobi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by GEMVN on 1/25/2018.
 */
public class JsonUtils
{
    private static ObjectMapper mapper = new ObjectMapper();

    public static String print(Object obj)
    {
        try
        {
            return mapper.writeValueAsString(obj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
