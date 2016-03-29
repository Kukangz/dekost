package wawa.skripsi.dekost.model;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13/01/2016.
 */
public class Province extends SugarRecord {
    String name;
    String country;

    public Province(){
    }

    public Province(String name, String country){
        this.name = name;
        this.country = country;
    }
}