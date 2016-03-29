package wawa.skripsi.dekost.model;

import org.json.JSONObject;

/**
 * Created by Admin on 10/01/2016.
 */
public class Log {

    private String description,date,value;

    private Integer type;
    private String id = null;
    private JSONObject allresult;

    public Log() {
    }

    public Log(String description, String date, String value, int type) {
        this.description = description;
        this.date = date;
        this.value = value;
        this.type = type;
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

    public void setType(int type){
        this.type = type;
    }

    public Integer getType(){
        return this.type;
    }

    public JSONObject getAllresult() {
        return allresult;
    }

    public void setAllresult(JSONObject allresult) {
        this.allresult = allresult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
