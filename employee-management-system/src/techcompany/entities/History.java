package techcompany.entities;

public class History {
    private int id;
    private String idCard;
    private String timeIn;
    private String timeOut;

    public History() {
    }

    public History(int id, String idCard, String timeIn, String timeOut) {
        this.id = id;
        this.idCard = idCard;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }
}
