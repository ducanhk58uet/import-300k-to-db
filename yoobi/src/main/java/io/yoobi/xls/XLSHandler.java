package io.yoobi.xls;

import io.yoobi.model.Cell;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private int lastRowNumber;
    private int lastColumnNumber;

    public XLSHandler(POIFSFileSystem fs)
    {
        this.fs = fs;
    }

    public static XLSHandler with(String filename) throws IOException
    {
        FileInputStream inputStream = new FileInputStream(filename);
        return new XLSHandler(new POIFSFileSystem(inputStream));
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

        //Xu ly hang moi
        if (thisRow != -1 && thisRow != lastRowNumber)
        {
            lastColumnNumber = -1;
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
                cellList.add(cell);
                collectMap.put(thisRow+1, cellList);
                dataTable.put(sheetIndex, collectMap);
            }

        }

        //Cap nhap so hang hien tai
        if (thisRow > -1)
        {
            lastRowNumber = thisRow;
        }
        if (thisColumn > -1)
        {
            lastColumnNumber = thisColumn;
        }

        if (record instanceof LastCellOfRowDummyRecord)
        {
            lastColumnNumber = -1;
        }
    }

    public Map<Integer, Map<Integer, List<Cell>>> getDataTable()
    {
        return this.dataTable;
    }

}
