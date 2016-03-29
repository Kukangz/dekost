package wawa.skripsi.dekost.model;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13/01/2016.
 */
public class City extends SugarRecord {
    String name;
    String province;

    public City(){
    }

    public City(String name, String province){
        this.name = name;
        this.province = province;
    }
}