package just.cse.mahfuz.shiptrackingsystem.Model;

public class History {

    String sStartDate, sEndDate, sDestination, sDeadWeight, sDraught, timestamp;

    public History() {
    }

    public History(String sStartDate, String sEndDate, String sDestination, String sDeadWeight, String sDraught, String timestamp) {
        this.sStartDate = sStartDate;
        this.sEndDate = sEndDate;
        this.sDestination = sDestination;
        this.sDeadWeight = sDeadWeight;
        this.sDraught = sDraught;
        this.timestamp = timestamp;
    }

    public String getsStartDate() {
        return sStartDate;
    }

    public void setsStartDate(String sStartDate) {
        this.sStartDate = sStartDate;
    }

    public String getsEndDate() {
        return sEndDate;
    }

    public void setsEndDate(String sEndDate) {
        this.sEndDate = sEndDate;
    }

    public String getsDestination() {
        return sDestination;
    }

    public void setsDestination(String sDestination) {
        this.sDestination = sDestination;
    }

    public String getsDeadWeight() {
        return sDeadWeight;
    }

    public void setsDeadWeight(String sDeadWeight) {
        this.sDeadWeight = sDeadWeight;
    }

    public String getsDraught() {
        return sDraught;
    }

    public void setsDraught(String sDraught) {
        this.sDraught = sDraught;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
