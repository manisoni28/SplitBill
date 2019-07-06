package udaan;

import java.io.Serializable;

public class MiniExpense implements Serializable {

    private String name;
    private Double cost;
    private Double quantity;

    MiniExpense(String name, Double cost, Double quantity) {
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Double getCost() {
        return cost;
    }

    Double getQuantity() {
        return quantity;
    }
}
