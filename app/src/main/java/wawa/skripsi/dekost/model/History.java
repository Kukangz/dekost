package wawa.skripsi.dekost.model;

import java.util.ArrayList;

/**
 * Created by Admin on 10/01/2016.
 */
public class History {

    private String description,date,value;

    private Integer type;
    private ArrayList<String> genre;

    public History() {
    }

    public History(String description, String date, String value) {
        this.description = description;
        this.date = date;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        this.description = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
