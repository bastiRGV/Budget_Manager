package com.example.budgetmanager;

public class Expense {

    private String name;
    private String category;
    private String date;
    private float amount;

    public Expense(String name, String category, String date, float amount){

        this.name = name;
        this.category = category;
        this.date = date;
        this.amount = amount;

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
