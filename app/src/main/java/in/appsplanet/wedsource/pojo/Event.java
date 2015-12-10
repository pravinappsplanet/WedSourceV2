package in.appsplanet.wedsource.pojo;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    /**
     * "id": "1", "name": "Ganpati", "venue": "Pune", "time": "11.00", "date":
     * "2015-10-01", "description": "test description"
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String venue;
    private String time;
    private String date;
    private String description;
    private int id;
    private ArrayList<Guest> guest = new ArrayList<Guest>();
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the venue
     */
    public String getVenue() {
        return venue;
    }

    /**
     * @param venue the venue to set
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getPDFData() {

        String guestStr = "";
        for (Guest guestData :
                guest) {
            guestStr = guestStr + " Name: " + guestData.getName() + " No.Of Persons: " + guestData.getNoOfPersons()+"\n";
        }


        String financeStr = "";
        for (Transaction transaction : transactions){
            financeStr = financeStr +transaction.toString();
        }


            Log.d("test", "Guest:" + guest.size() + " Guest Str:" + guestStr);
        return "Name:" + name + "\n" +
                "Time:" + time + " " +
                "Date:" + date + " " +
                "Venue:" + venue + "\n" +
                "Guest:" + guestStr+"\n" +
                "Finance:\n" + financeStr;

    }

    public ArrayList<Guest> getGuest() {
        return guest;
    }

    public void setGuest(ArrayList<Guest> guest) {
        this.guest = guest;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
