package io.yoobi.model;

/**
 * Created by GEMVN on 1/23/2018.
 */
public class Cell
{
    private String address;
    private Object value;

    public Cell(String address, String value)
    {
        this.value = value;
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getColumn()
    {
        return this.address.replaceAll("[0-9]", "");
    }

    @Override
    public String toString()
    {
        return "Cell{" +
                "address='" + address + '\'' +
                ", value=" + value +
                '}';
    }
}
