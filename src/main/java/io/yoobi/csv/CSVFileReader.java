package io.yoobi.csv;

import com.opencsv.CSVReader;
import io.yoobi.model.ECConfig;
import io.yoobi.model.TableData;

import java.io.FileReader;
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
    public List<String[]> readRows()
    throws Exception
    {
        int batchSize = this.config.getBatchSize();
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
                if (rowNum < tableData.getStartRow()) continue;
                String[] rowFilter = new String[tableData.getColumns().size()];
                int index = 0;
                for (int idx = 0; idx < row.length; idx++)
                {
                    if (tableData.getColumns().contains(String.valueOf(idx)))
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
