package udaan;


import java.io.Serializable;
import java.util.HashMap;

public class Expense implements Serializable {
    private String name;
    private Double cost;
    private HashMap<String,Boolean> checks;
    private HashMap<String,Integer> seeks;


    Expense(String name, Double cost, HashMap<String, Boolean> checks, HashMap<String, Integer> seeks) {
        this.name = name;
        this.cost = cost;
        this.checks = checks;
        this.seeks = seeks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    HashMap<String, Boolean> getChecks() {
        return checks;
    }

    public void setChecks(HashMap<String, Boolean> checks) {
        this.checks = checks;
    }

    HashMap<String, Integer> getSeeks() {
        return seeks;
    }

    public void setSeeks(HashMap<String, Integer> seeks) {
        this.seeks = seeks;
    }

    public String getName() {
        return this.name;
    }

    Double getCost() {
        return this.cost;
    }

}
