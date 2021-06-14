package it.rememo.rememo.models;

import java.util.Map;

public class StatDay {
    public final static String COLLECTION_NAME = "days";

    private String day;
    //private Map<String, Integer, Integer> collections;

    public StatDay(String day) {
        Init(day);
    }

    public void Init(String day) {
        this.day = day;
    }
}
