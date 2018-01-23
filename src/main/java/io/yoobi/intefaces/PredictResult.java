package io.yoobi.intefaces;

import io.yoobi.model.Cell;

import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/23/2018.
 */
public interface PredictResult
{
    boolean afterCheck();

    void onListener(Map<Integer, List<Cell>> table);
}
