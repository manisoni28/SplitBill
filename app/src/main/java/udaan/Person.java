package udaan;

import java.io.Serializable;

public class Person implements Serializable {

    private Double paid;
    private Double owes;

    Person(Double paid, Double owes) {
        this.paid = paid;
        this.owes = owes;
    }

    Double getPaid() {
        return this.paid;
    }

    Double getOwes() {
        return this.owes;
    }

    void setOwes(Double owes) {
        this.owes = owes;
    }
}
