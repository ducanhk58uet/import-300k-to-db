package io.yoobi.csv;

import com.opencsv.CSVReader;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class CSVFileReader implements IDataFileReader
{
    private int rowNum = 0; //dong hien tai
    private CSVReader reader;
    private ECConfig config;
    private TableData tableData = null;
    public CSVFileReader(ECConfig config, char delimiter) throws Exception
    {
        this.config = config;
        System.out.println(this.config.getPath());
        this.reader = new CSVReader(new FileReader(config.getPath()), delimiter);
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
    public List<String[]> readRows(int batchSize)
    throws Exception
    {
        List<String[]> dataRows = (tableData == null) ? getList(batchSize) : getListByQuery(batchSize);
        return dataRows;
    }

    public List<String[]> getList(int batchSize)
    throws Exception
    {
        List<String[]> dataRows = new ArrayList<>();
        if (batchSize > 0)
        {
            String[] row;
            while((row = reader.readNext()) != null)
            {
                rowNum++;
                dataRows.add(row);
                if (dataRows.size() > batchSize)
                {
                    break;
                }
            }
        }
        return dataRows;
    }

    public List<String[]> getListByQuery(int batchSize)
    throws Exception
    {
        List<String[]> dataRows = new ArrayList<>();
        if (batchSize > 0)
        {
            String[] row;
            while((row = reader.readNext()) != null)
            {
                rowNum++;

                String[] rowFilter = new String[tableData.getColumns().size()];
                int index = 0;
                for (int idx = 0; idx < row.length; idx++)
                {
                    if (tableData.getColumns().contains(String.valueOf(idx + 1)))
                    {
                        rowFilter[index] = row[idx];
                        index++;
                    }
                }
                dataRows.add(rowFilter);
                if (dataRows.size() > batchSize)
                {
                    break;
                }
            }
        }
        return dataRows;
    }
}
