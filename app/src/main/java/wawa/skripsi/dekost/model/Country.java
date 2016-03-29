package wawa.skripsi.dekost.model;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13/01/2016.
 */
public class Country extends SugarRecord {
    String name;
    String capital;
    String code;

    public Country(){
    }

    public Country(String name, String capital, String code){
        this.name = name;
        this.capital = capital;
        this.code = code;
    }
}