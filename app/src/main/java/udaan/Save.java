package udaan;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Save implements Serializable{

    private Date date;
    private Map<String, Person> people;
    private ArrayList<Expense> expenses;

    public Save(Date date, Map<String, Person> people, ArrayList<Expense> expenses) {
        this.date = date;
        this.people = people;
        this.expenses = expenses;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public void setPeople(Map<String, Person> people) {
        this.people = people;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }
}
