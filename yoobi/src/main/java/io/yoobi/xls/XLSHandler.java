package io.yoobi.xls;

import io.yoobi.model.Cell;
import io.yoobi.model.ECConfig;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by GEMVN on 1/31/2018.
 */
public class XLSHandler implements HSSFListener
{
    private POIFSFileSystem fs;

    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;

    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    private List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();

    //private Map<Integer, List<Cell>> collectMap = new LinkedHashMap<>();
    private Map<Integer, Map<Integer, List<Cell>>> dataTable = new LinkedHashMap<>();

    private int batchSize;

    private Map<Integer, Set<String>> collectHeaders = new LinkedHashMap<>();

    public XLSHandler(POIFSFileSystem fs, int batchSize)
    {
        this.fs = fs;
        this.batchSize = batchSize;
    }

    public static XLSHandler with(ECConfig config) throws IOException
    {
        FileInputStream inputStream = new FileInputStream(config.getPath());
        return new XLSHandler(new POIFSFileSystem(inputStream), config.getBatchSize());
    }

    public void process() throws IOException
    {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        request.addListenerForAllRecords(formatListener);
        factory.processWorkbookEvents(request, fs);
    }

    @Override
    public void processRecord(Record record)
    {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;
        List<Cell> cellList = null;

        switch (record.getSid())
        {
            case BoundSheetRecord.sid:
                boundSheetRecords.add((BoundSheetRecord) record);
                break;
            case BOFRecord.sid:
                BOFRecord br = (BOFRecord) record;
                if (br.getType() == BOFRecord.TYPE_WORKSHEET)
                {
                    sheetIndex++;
                    if (orderedBSRs == null)
                    {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                    }

                    dataTable.put(sheetIndex, null);
                    collectHeaders.put(sheetIndex, new LinkedHashSet<>());
                    System.out.println(orderedBSRs[sheetIndex].getSheetname() + " [" + (sheetIndex + 1) + "]:");
                }
                break;

            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                break;

            case BlankRecord.sid:
                BlankRecord brec = (BlankRecord) record;

                thisRow = brec.getRow();
                thisColumn = brec.getColumn();
                thisStr = "";
                break;
            case BoolErrRecord.sid:
                BoolErrRecord berec = (BoolErrRecord) record;

                thisRow = berec.getRow();
                thisColumn = berec.getColumn();
                thisStr = "";
                break;

            case FormulaRecord.sid:
                FormulaRecord frec = (FormulaRecord) record;

                thisRow = frec.getRow();
                thisColumn = frec.getColumn();

                thisStr = "";

                break;
            case StringRecord.sid:
                thisStr = "";
                break;

            case LabelRecord.sid:
                LabelRecord lrec = (LabelRecord) record;

                thisRow = lrec.getRow();
                thisColumn = lrec.getColumn();
                thisStr = '"' + lrec.getValue() + '"';
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord lsrec = (LabelSSTRecord) record;

                thisRow = lsrec.getRow();
                thisColumn = lsrec.getColumn();
                if (sstRecord == null)
                {
                    thisStr = "";
                }
                else
                {
                    thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
                }
                break;
            case NoteRecord.sid:
                NoteRecord nrec = (NoteRecord) record;

                thisRow = nrec.getRow();
                thisColumn = nrec.getColumn();

                thisStr = "";
                break;
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;

                thisRow = numrec.getRow();
                thisColumn = numrec.getColumn();

                thisStr = formatListener.formatNumberDateCell(numrec);
                break;
            case RKRecord.sid:
                RKRecord rkrec = (RKRecord) record;

                thisRow = rkrec.getRow();
                thisColumn = rkrec.getColumn();

                thisStr = "";
                break;
            default:
                break;
        }

        // Handle missing column
        if (record instanceof MissingCellDummyRecord)
        {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            thisStr = "";
        }


        //Them du lieu vao CELL
        if (thisStr != null && !thisStr.isEmpty())
        {
            String cname = CellReference.convertNumToColString(thisColumn);
            String address = cname + (thisRow+1);

            if (dataTable.containsKey(sheetIndex))
            {
                Cell cell = new Cell(address, thisStr);
                Map<Integer, List<Cell>> collectMap = dataTable.get(sheetIndex) != null ? dataTable.get(sheetIndex) : new LinkedHashMap<>();
                cellList = collectMap.containsKey(thisRow + 1) ? collectMap.get(thisRow+1) : new ArrayList<>();
                if (cellList.size() > batchSize)
                {
                    return;
                }
                cellList.add(cell);
                collectMap.put(thisRow+1, cellList);
                dataTable.put(sheetIndex, collectMap);
                //add column to headers
                if (collectHeaders.containsKey(sheetIndex))
                {
                    String column = sheetIndex + ":" + cname;
                    collectHeaders.get(sheetIndex).add(column);
                }
            }

        }


    }

    public Map<Integer, Map<Integer, List<Cell>>> getDataTable()
    {
        return this.dataTable;
    }

    public Map<Integer, Set<String>> getCollectHeaders()
    {
        return this.collectHeaders;
    }
}
