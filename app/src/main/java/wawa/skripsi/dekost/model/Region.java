package wawa.skripsi.dekost.model;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13/01/2016.
 */
public class Region extends SugarRecord {
    String name;
    String district;

    public Region(){
    }

    public Region(String name, String district){
        this.name = name;
        this.district = district;
    }
}