package org.Arc.Web;

public enum Website {
    CONEXED, RADIUS, ACUITY, NONE;

    public String toLink() {
        switch (this) {
            case CONEXED:
                return "https://mathnasium.craniumcafe.com/onlineabrams/classroom";
            case RADIUS:
                return "https://radius.mathnasium.com/Account/Login";
            case ACUITY:
                return "https://acuityscheduling.com/login.php";
            default:
                return "https://www.google.com";
        }
    }
    
    public String getUsername() {
        switch (this) {
            case CONEXED:
                return "onlineabrams@mathnasium.com";
            case RADIUS:
                return "Leon.Jaggon";
            case ACUITY:
                return "leon@mathnhp.com";
            default:
                return null;
        }
    }
    
    public String getPassword() {
        switch (this) {
            case CONEXED:
                return "Math1234$$";
            case RADIUS:
                return "Kaylyn2!";
            case ACUITY:
                return "Math1234$$";
            default:
                return null;
        }
    }
}
