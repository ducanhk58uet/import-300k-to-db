package io.yoobi.csv;

import io.yoobi.model.Cell;

import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/31/2018.
 */
public interface IDataFileReader
{
    Map<Integer, List<Cell>> readRows() throws Exception;

    int rowNum();
}
