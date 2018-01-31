package io.yoobi.csv;

import java.util.List;

/**
 * Created by GEMVN on 1/31/2018.
 */
public interface IDataFileReader
{
    List<String[]> readRows(int batchSize) throws Exception;

    int rowNum();
}
