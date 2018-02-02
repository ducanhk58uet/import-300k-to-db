package io.yoobi.model;

import java.util.*;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class DataResponse
{
    private int errorCode;
    private String message;
    private Map<Integer, Set<String>> headers = new LinkedHashMap<>();
    private Map<Integer, Map<Integer, List<Cell>>> data;
    private List<Date> dateList = new ArrayList<>();

    public DataResponse() {}

    public DataResponse(DataResponseBuilder builder)
    {
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.headers = builder.headers;
        this.data = builder.data;
        this.dateList = builder.dateList;
    }

    public static DataResponseBuilder newInstance()
    {

        return new DataResponseBuilder();
    }

    public static class DataResponseBuilder
    {
        private int errorCode;
        private String message;
        private Map<Integer, Set<String>> headers;
        private Map<Integer, Map<Integer, List<Cell>>> data;
        private List<Date> dateList = new ArrayList<>();

        public DataResponseBuilder() {}

        public DataResponseBuilder setErrorCode(int errorCode)
        {
            this.errorCode = errorCode;
            return this;
        }

        public DataResponseBuilder setMessage(String message)
        {
            this.message = message;
            return this;
        }

        public DataResponseBuilder setHeaders(Map<Integer, Set<String>> headers)
        {
            this.headers = headers;
            return this;
        }

        public DataResponseBuilder setData(Map<Integer, Map<Integer, List<Cell>>> data)
        {
            this.data = data;
            return this;
        }

        public DataResponseBuilder setDateList(List<Date> dateList)
        {
            this.dateList = dateList;
            return this;
        }

        public DataResponse build()
        {
            return new DataResponse(this);
        }
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Map<Integer, Set<String>> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<Integer, Set<String>> headers)
    {
        this.headers = headers;
    }

    public Map<Integer, Map<Integer, List<Cell>>> getData()
    {
        return data;
    }

    public void setData(Map<Integer, Map<Integer, List<Cell>>> data)
    {
        this.data = data;
    }

    public List<Date> getDateList()
    {
        return dateList;
    }

    public void setDateList(List<Date> dateList)
    {
        this.dateList = dateList;
    }
}
