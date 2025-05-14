package com.arr.simple.models;

public class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String header;
    private String title;
    private String value;
    private String plan;
    private String date;

    // Constructor para headers
    public ListItem(String header) {
        this.type = TYPE_HEADER;
        this.header = header;
    }

    // Constructor para items
    public ListItem(String title, String value, String plan, String date) {
        this.type = TYPE_ITEM;
        this.title = title;
        this.value = value;
        this.plan = plan;
        this.date = date;
    }

    // Getters
    public int getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public String getTitle() {
        return title;
    }

    public String getPlan() {
        return plan;
    }

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }
}
