package in.appsplanet.wedsource.pojo;

import java.io.Serializable;

/**
 * Created by root on 3/11/15.
 */
public class Transaction implements Serializable {

    private int id;
    private int type;
    private int financeId;
    private int amount;
    private String name;
    private String description;
    private String date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFinanceId() {
        return financeId;
    }

    public void setFinanceId(int financeId) {
        this.financeId = financeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Name: "+name+" "+" Amount: "+amount+(type==1?" Type: Income":" Type: Expense")+"\n";
    }
}
