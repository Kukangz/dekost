package wawa.skripsi.dekost.model;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13/01/2016.
 */
public class District extends SugarRecord {
    String name;
    String city;

    public District(){
    }

    public District(String name, String city){
        this.name = name;
        this.city = city;
    }
}