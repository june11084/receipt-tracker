package com.jun.myproject;


//Each row in the database can be represented by an object
//Columns will represent the objects properties
public class Category {

    private int _id;
    private String _categoryname;

    public Category() {
    }

    public Category(String receiptname) {
        this._categoryname = receiptname;
    }

    public void set_categoryname(String _categoryname) {
        this._categoryname = _categoryname;
    }

    public String get_categoryname() {
        return _categoryname;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }


}