package in.appsplanet.wedsource.pojo;

import java.io.Serializable;

/**
 * Created by root on 3/11/15.
 */
public class Finance implements Serializable {
    private int id;
    private int eventId;
    private int userId;
    private int budget;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }
}
