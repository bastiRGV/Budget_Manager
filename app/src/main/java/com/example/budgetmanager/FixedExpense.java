package com.example.budgetmanager;

public class FixedExpense {

    private int id;
    private String name;
    private float amount;

    public FixedExpense(int id, String name, float amount){

        this.id = id;
        this.name = name;
        this.amount = amount;

    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public float getAmount(){
        return amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
