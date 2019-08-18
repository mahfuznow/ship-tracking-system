package just.cse.mahfuz.shiptrackingsystem.Model;

public class Users {
    String sImage, sShipName, sShipID, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    public Users() {
    }

    public Users(String sImage, String sShipName, String sShipID, String sPassword, String sCountry, String sOwnerName, String sOwnerEmail, String sOwnerPhone) {
        this.sImage = sImage;
        this.sShipName = sShipName;
        this.sShipID = sShipID;

        this.sCountry = sCountry;
        this.sOwnerName = sOwnerName;
        this.sOwnerEmail = sOwnerEmail;
        this.sOwnerPhone = sOwnerPhone;
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
}
