package io.yoobi.csv;

import com.opencsv.CSVReader;
import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class CSVFileReader implements IDataFileReader
{
    private int rowNum = 0; //dong hien tai
    private CSVReader reader;
    private ECConfig config;
    private TableData tableData = null;

    public CSVFileReader(ECConfig config) throws Exception
    {
        this.config = config;
        this.reader = new CSVReader(new FileReader(config.getPath()), config.getDelimiter().charAt(0));
        if (this.config.getTableDatas().get(0) != null)
        {
            this.tableData = this.config.getTableDatas().get(0);
        }
    }

    @Override
    public int rowNum()
    {
        return this.rowNum;
    }

    @Override
    public Map<Integer, List<Cell>> readRows()
    throws Exception
    {
        int batchSize = this.config.getBatchSize();
        Map<Integer, List<Cell>> dataRows = (tableData == null) ? getList(batchSize) : getListByQuery(batchSize);
        return dataRows;
    }

    public Map<Integer, List<Cell>> getList(int batchSize)
    throws Exception
    {
        Map<Integer, List<Cell>> dataRows = new LinkedHashMap<>();
        if (batchSize > 0)
        {
            String[] row;
            while((row = reader.readNext()) != null)
            {
                rowNum++;
                List<Cell> cellList = new ArrayList<>();
                for (int i = 0; i < row.length; i++)
                {
                    Cell cell = new Cell(String.valueOf(i), row[i]);
                    cellList.add(cell);
                }
                dataRows.put(rowNum, cellList);
                if (dataRows.size() > batchSize)
                {
                    break;
                }
            }
        }
        return dataRows;
    }

    public Map<Integer, List<Cell>> getListByQuery(int batchSize)
    throws Exception
    {
        Map<Integer, List<Cell>> dataRows = new LinkedHashMap<>();
        if (batchSize > 0)
        {
            String[] row;
            while((row = reader.readNext()) != null)
            {
                rowNum++;
                if (rowNum < tableData.getStartRow()) continue;
                String[] rowFilter = new String[tableData.getColumns().size()];

                List<Cell> cellList = new ArrayList<>();
                for (int idx = 0; idx < row.length; idx++)
                {
                    if (tableData.getColumns().contains(String.valueOf(idx)))
                    {
                        Cell cell = new Cell(String.valueOf(idx), row[idx]);
                        cellList.add(cell);
                    }
                }
                dataRows.put(rowNum, cellList);
                if (dataRows.size() > batchSize)
                {
                    break;
                }
            }
        }
        return dataRows;
    }
}
