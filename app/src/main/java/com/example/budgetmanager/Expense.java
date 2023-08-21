package com.example.budgetmanager;

public class Expense {

    private int id;
    private String name;
    private String category;
    private String date;
    private float amount;

    public Expense(int id, String name, String category, String date, float amount){

        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.amount = amount;

    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getCategory(){
        return category;
    }

    public String getDate(){
        return date;
    }

    public float getAmount(){
        return amount;
    }


    public void setID(int id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setAmount(float amount){
        this.amount = amount;
    }

}
