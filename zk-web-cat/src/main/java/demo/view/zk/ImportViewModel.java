package demo.view.zk;


import demo.connection.DBUtils;
import io.yoobi.ApplyETL;
import io.yoobi.FileManager;
import io.yoobi.YoobiTest;
import io.yoobi.model.Cell;
import io.yoobi.model.DataResponse;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class ImportViewModel {


    @Init
    public void init() {
        //System.out.println(">>>> ERROR_CODE: " + dataResponse.getErrorCode());
        //this.dataResponse = dataResponse;
    }



    @Command(value = "importData")
    public void importData()
    {
        long startTime = System.currentTimeMillis();

        File file = FileManager.getDesktopFile("etc/config_3_multi_sheet.json");
        DataResponse response = ApplyETL.execute(file);
        YoobiTest.connectMultiSheet(response);
        //model.addAttribute("dataResponse", response);


        String sql = "insert into EC_REPORT_2 (ID, VALUE_01, VALUE_02) values (?, ?, ?)";
        Connection connection = DBUtils.getConnection();

        try (PreparedStatement ps = connection.prepareStatement(sql))
        {
            final int batchSize = 1000;
            int count = 0;

            for (Integer rowNumber: response.getData().get(0).keySet()) {
                List<Cell> cellList = response.getData().get(0).get(rowNumber);
                if (cellList.size() >= 2)
                {
                    ps.setInt(1, count);
                    ps.setString(2, cellList.get(0).getValue().toString());
                    ps.setString(3, cellList.get(1).getValue().toString());
                    ps.addBatch();

                    count++;
                    if(count % batchSize == 0) {
                        ps.executeBatch();
                    }
                }
            }

            ps.executeBatch(); // insert remaining records
            //ps.close();
            //connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }


        System.out.println("LostTime: " + (System.currentTimeMillis() - startTime));
    }

}
