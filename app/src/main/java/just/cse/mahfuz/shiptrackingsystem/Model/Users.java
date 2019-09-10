package just.cse.mahfuz.shiptrackingsystem.Model;

public class Users {
    String sImage, sShipName, sShipID, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;
    String sLatitude,sLongitude,sSpeed;
    String sDestination,sDeadWeight,sDraught,sJourneyDate;

    public Users() {
    }

    public Users(String sImage, String sShipName, String sShipID, String sCountry, String sOwnerName, String sOwnerEmail, String sOwnerPhone, String sLatitude, String sLongitude, String sSpeed) {
        this.sImage = sImage;
        this.sShipName = sShipName;
        this.sShipID = sShipID;
        this.sCountry = sCountry;
        this.sOwnerName = sOwnerName;
        this.sOwnerEmail = sOwnerEmail;
        this.sOwnerPhone = sOwnerPhone;
        this.sLatitude = sLatitude;
        this.sLongitude = sLongitude;
        this.sSpeed = sSpeed;
    }

    public Users(String sImage, String sShipName, String sShipID, String sCountry, String sOwnerName, String sOwnerEmail, String sOwnerPhone, String sLatitude, String sLongitude, String sSpeed, String sDestination, String sDeadWeight, String sDraught, String sJourneyDate) {
        this.sImage = sImage;
        this.sShipName = sShipName;
        this.sShipID = sShipID;
        this.sCountry = sCountry;
        this.sOwnerName = sOwnerName;
        this.sOwnerEmail = sOwnerEmail;
        this.sOwnerPhone = sOwnerPhone;
        this.sLatitude = sLatitude;
        this.sLongitude = sLongitude;
        this.sSpeed = sSpeed;
        this.sDestination = sDestination;
        this.sDeadWeight = sDeadWeight;
        this.sDraught = sDraught;
        this.sJourneyDate = sJourneyDate;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }

    public String getsShipName() {
        return sShipName;
    }

    public void setsShipName(String sShipName) {
        this.sShipName = sShipName;
    }

    public String getsShipID() {
        return sShipID;
    }

    public void setsShipID(String sShipID) {
        this.sShipID = sShipID;
    }

    public String getsCountry() {
        return sCountry;
    }

    public void setsCountry(String sCountry) {
        this.sCountry = sCountry;
    }

    public String getsOwnerName() {
        return sOwnerName;
    }

    public void setsOwnerName(String sOwnerName) {
        this.sOwnerName = sOwnerName;
    }

    public String getsOwnerEmail() {
        return sOwnerEmail;
    }

    public void setsOwnerEmail(String sOwnerEmail) {
        this.sOwnerEmail = sOwnerEmail;
    }

    public String getsOwnerPhone() {
        return sOwnerPhone;
    }

    public void setsOwnerPhone(String sOwnerPhone) {
        this.sOwnerPhone = sOwnerPhone;
    }

    public String getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(String sLatitude) {
        this.sLatitude = sLatitude;
    }

    public String getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(String sLongitude) {
        this.sLongitude = sLongitude;
    }

    public String getsSpeed() {
        return sSpeed;
    }

    public void setsSpeed(String sSpeed) {
        this.sSpeed = sSpeed;
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

    public String getsJourneyDate() {
        return sJourneyDate;
    }

    public void setsJourneyDate(String sJourneyDate) {
        this.sJourneyDate = sJourneyDate;
    }
}
