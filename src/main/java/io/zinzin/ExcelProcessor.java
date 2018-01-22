package io.zinzin;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by Andy on 01/18/2018.
 */
public class ExcelProcessor
{
    public static final String SAMPLE_XLSX_FILE_PATH = "D:/02-DOI-SOAT/CHITIET7 20171205.xlsx";
    public static final String SAMPLE_XLS_FILE_PATH = "D:/02-DOI-SOAT/MB_BP01_ONE_20170801_201708011.xls";
    public static final String SAMPLE_XLSX_FILE_PATH_2 = "D:/02-DOI-SOAT/MB_BP01_ONE_20170801_201708011.xlsx";
    public static final String SAMPLE_CSV_FILE_PATH = "D:/02-DOI-SOAT/CHITIET7 20171205.csv";

    public static Map<String, List<Object>> datasets = new HashMap<>();

    public static void main(String[] args)
            throws Exception
    {
        long time = System.currentTimeMillis();
        XLSX2CSV.start(new String[]{SAMPLE_XLSX_FILE_PATH_2, "3"});
        System.out.println("Finish: " + (System.currentTimeMillis() - time));

//        long time = System.currentTimeMillis();
//        ExampleEventUserModel.start(new String[]{SAMPLE_XLSX_FILE_PATH_2});
//        System.out.println("Finish: " + (System.currentTimeMillis() - time));

//        long time = System.currentTimeMillis();
//        XLS2CSVmra.start(new String[]{SAMPLE_XLS_FILE_PATH});
//        System.out.println("Finish: " + (System.currentTimeMillis() - time));
        
        //readExcelBasic();

    }

    public static void readExcelBasic()
    throws Exception
    {
        long time = System.currentTimeMillis();
        System.out.println("Reading Excel: " + SAMPLE_XLSX_FILE_PATH);
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        for (Sheet sheet : workbook)
        {
            System.out.println("Scan with: " + sheet.getSheetName());
            extractPerSheet(sheet);
        }

        System.out.println("Finish: " + (System.currentTimeMillis() - time));
        System.out.println("There are " + datasets.size() + "items");
        workbook.close();

        System.out.println("Start insert database...");
        time = System.currentTimeMillis();
        Connection connection = ConnectionObject.getConnection();
        PreparedStatement pstmt = null;
        try {
            String sqlQuery = "INSERT INTO EC_REPORT VALUES (?,?)";

            int count = 0;
            int batchSize = 50;
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sqlQuery);

            for (String key : datasets.keySet()) {
                if (datasets.get(key).size() > 2) {
                    System.out.println("Duplicate value with key: " + key);
                } else {
                    try
                    {
                        pstmt.setBigDecimal(1, new BigDecimal(key));
                        pstmt.setDouble(2, (Double) datasets.get(key).get(0));
                        pstmt.addBatch();
                        count++;

                        if (count % batchSize == 0) {
                            System.out.println("Commit batch");
                            int[] result = pstmt.executeBatch();
                            System.out.println("Number of rows inserted: " + result.length);
                            connection.commit();
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        continue;
                    }

                }

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            connection.rollback();
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
            if (connection != null)
            {
                connection.close();
            }
        }
        System.out.println("Finish: " + (System.currentTimeMillis() - time));
    }

    private static void extractPerSheet(Sheet sheet)
    {

        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            String vk = "";
            Double nk = null;
            while (cellIterator.hasNext())
            {
                Cell cell = cellIterator.next();

                if (cell.getCellTypeEnum().equals(CellType.STRING))
                {
                    vk = cell.getStringCellValue();
                }
                else
                {
                    nk = cell.getNumericCellValue();
                }
            }

            if (!vk.isEmpty() && nk != null)
            {
                List<Object> items = datasets.get(vk);
                if (items == null)
                {
                    items = new ArrayList<>();
                }
                items.add(nk);
                datasets.put(vk, items);
            }
        }

    }
}
