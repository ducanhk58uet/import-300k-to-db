package io.yoobi.intefaces;

import io.yoobi.model.Cell;

import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/25/2018.
 */
public interface TypedMapping
{
    boolean accept(Cell cell, Map<Integer, List<Cell>> sourceTable);
}
