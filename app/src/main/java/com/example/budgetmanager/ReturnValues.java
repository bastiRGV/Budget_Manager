package com.example.budgetmanager;

public class ReturnValues {

    private float budgetGesamt;
    private float budgetUebrig;
    private float ausgaben;
    private String curency;
    private float[] chartData;

    public ReturnValues(float budgetGesamt, float budgetUebrig, float ausgaben, String curency, float[] chartData) {
        this.budgetGesamt = budgetGesamt;
        this.budgetUebrig = budgetUebrig;
        this.ausgaben = ausgaben;
        this.curency = curency;
        this.chartData = chartData;
    }

    public float getBudgetGesamt() {
        return budgetGesamt;
    }

    public void setBudgetGesamt(float budgetGesamt) {
        this.budgetGesamt = budgetGesamt;
    }

    public float getBudgetUebrig() {
        return budgetUebrig;
    }

    public void setBudgetUebrig(float budgetUebrig) {
        this.budgetUebrig = budgetUebrig;
    }

    public float getAusgaben() {
        return ausgaben;
    }

    public void setAusgaben(float ausgaben) {
        this.ausgaben = ausgaben;
    }

    public String getCurency() {
        return curency;
    }

    public void setCurency(String curency) {
        this.curency = curency;
    }

    public float[] getChartData() {
        return chartData;
    }

    public void setChartData(float[] chartData) {
        this.chartData = chartData;
    }

}
