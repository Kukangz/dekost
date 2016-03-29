package wawa.skripsi.dekost.util;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class CustomMultiSelectedPreference  {

    public List<CharSequence> entries;
    public List<CharSequence> entriesValues;
    // note: AttributeSet  is needed in super class
    public CustomMultiSelectedPreference(Context context,AttributeSet attrs, ArrayList<CharSequence> data,ArrayList<CharSequence> id) {

        this.entries = new ArrayList<CharSequence>();
        this.entriesValues = new ArrayList<CharSequence>();

    }

    public List<CharSequence> getEntries(){

        return entries;
    }

    public List<CharSequence> getEntriesValues(){

        return entriesValues;
    }
}
