package io.yoobi.model;

import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class DataResponse
{
    private int errorCode;
    private String message;
    private List<String> headers;
    private Map<Integer, Map<Integer, List<Cell>>> data;

    public DataResponse() {}

    public DataResponse(DataResponseBuilder builder)
    {
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.headers = builder.headers;
        this.data = builder.data;
    }

    public class DataResponseBuilder
    {
        private int errorCode;
        private String message;
        private List<String> headers;
        private Map<Integer, Map<Integer, List<Cell>>> data;

        public DataResponseBuilder(int errorCode, String message, List<String> headers, Map<Integer, Map<Integer, List<Cell>>> data)
        {
            this.errorCode = errorCode;
            this.message = message;
            this.headers = headers;
            this.data = data;
        }

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

        public DataResponseBuilder setHeaders(List<String> headers)
        {
            this.headers = headers;
            return this;
        }

        public DataResponseBuilder setData(Map<Integer, Map<Integer, List<Cell>>> data)
        {
            this.data = data;
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

    public List<String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(List<String> headers)
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
}
