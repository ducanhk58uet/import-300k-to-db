package io.yoobi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class ECConfig
{
    @JsonProperty(value = "import_table")
    public String importTable;

    @JsonProperty(value = "columns_import")
    public List<String> columns;

    @JsonProperty(value = "file_type")
    public String type;

    @JsonProperty(value = "is_delete_old_data")
    public boolean isDeleted;

    @JsonProperty(value = "position_start_date")
    public String positionStartDate;

    @JsonProperty(value = "position_end_date")
    public String positionEndDate;

    @JsonProperty(value = "format_start_date")
    public String formatStartDate;

    @JsonProperty(value = "format_end_date")
    public String formatEndDate;

    @JsonProperty(value = "position_address_pattern")
    public String positionAddressPattern;

    @JsonProperty(value = "link_sheet")
    public String linkSheet;

    @JsonProperty(value = "path")
    public String path;

    @JsonIgnoreProperties
    public List<TableData> tableDatas = new ArrayList<>();

    @JsonIgnoreProperties
    public List<LinkSheet> linkSheets = new ArrayList<>();

    public String getImportTable()
    {
        return importTable;
    }

    public void setImportTable(String importTable)
    {
        this.importTable = importTable;
    }

    public List<String> getColumns()
    {
        return columns;
    }

    public void setColumns(List<String> columns)
    {
        this.columns = columns;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isDeleted()
    {
        return isDeleted;
    }

    public void setDeleted(boolean deleted)
    {
        isDeleted = deleted;
    }

    public String getPositionStartDate()
    {
        return positionStartDate;
    }

    public void setPositionStartDate(String positionStartDate)
    {
        this.positionStartDate = positionStartDate;
    }

    public String getPositionEndDate()
    {
        return positionEndDate;
    }

    public void setPositionEndDate(String positionEndDate)
    {
        this.positionEndDate = positionEndDate;
    }

    public String getFormatStartDate()
    {
        return formatStartDate;
    }

    public void setFormatStartDate(String formatStartDate)
    {
        this.formatStartDate = formatStartDate;
    }

    public String getFormatEndDate()
    {
        return formatEndDate;
    }

    public void setFormatEndDate(String formatEndDate)
    {
        this.formatEndDate = formatEndDate;
    }

    public String getPositionAddressPattern()
    {
        return positionAddressPattern;
    }

    public void setPositionAddressPattern(String positionAddressPattern)
    {
        this.positionAddressPattern = positionAddressPattern;
    }

    public String getLinkSheet()
    {
        return linkSheet;
    }

    public void setLinkSheet(String linkSheet)
    {
        this.linkSheet = linkSheet;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public List<TableData> getTableDatas()
    {
        return tableDatas;
    }

    public void setTableDatas(List<TableData> tableDatas)
    {
        this.tableDatas = tableDatas;
    }

    public List<LinkSheet> getLinkSheets()
    {
        return linkSheets;
    }

    public void setLinkSheets(List<LinkSheet> linkSheets)
    {
        this.linkSheets = linkSheets;
    }

    @Override
    public String toString()
    {
        return "ECConfig{" +
                "importTable='" + importTable + '\'' +
                ", columns=" + columns +
                ", type='" + type + '\'' +
                ", isDeleted=" + isDeleted +
                ", positionStartDate='" + positionStartDate + '\'' +
                ", positionEndDate='" + positionEndDate + '\'' +
                ", formatStartDate='" + formatStartDate + '\'' +
                ", formatEndDate='" + formatEndDate + '\'' +
                ", positionAddressPattern='" + positionAddressPattern + '\'' +
                ", linkSheet='" + linkSheet + '\'' +
                '}';
    }
}
