package io.yoobi.model;


/**
 * Created by GEMVN on 1/19/2018.
 */
public class LinkSheet
{
    public int sourceSheet;
    public String sourceColumn;
    public int destinationSheet;
    public String destinationColumn;

    public LinkSheet(String s)
    {
        s = s.replace(" ", "").replace("{", "").replace("}", "");
        String[] args = s.split("~");
        if (args.length >= 2)
        {
            String[] t = args[0].split(":");
            this.sourceSheet = Integer.parseInt(t[0]);
            this.sourceColumn = t[1];

            t = args[1].split(":");
            this.destinationSheet = Integer.parseInt(t[0]);
            this.destinationColumn = t[1];
        }
    }


    public int getSourceSheet()
    {
        return sourceSheet;
    }

    public void setSourceSheet(int sourceSheet)
    {
        this.sourceSheet = sourceSheet;
    }

    public String getSourceColumn()
    {
        return sourceColumn;
    }

    public void setSourceColumn(String sourceColumn)
    {
        this.sourceColumn = sourceColumn;
    }

    public int getDestinationSheet()
    {
        return destinationSheet;
    }

    public void setDestinationSheet(int destinationSheet)
    {
        this.destinationSheet = destinationSheet;
    }

    public String getDestinationColumn()
    {
        return destinationColumn;
    }

    public void setDestinationColumn(String destinationColumn)
    {
        this.destinationColumn = destinationColumn;
    }
}
