package main.java.apivk;

public class VkUserDul {
    private int id;
    private String first_name;
    private String last_name;
    private String bdate;
    private boolean is_closed;
    private boolean can_access_closed;

    public VkUserDul() {

    }

    public String getBdate() {
        return bdate;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFullName() {
        if(last_name != null) {
            return first_name + " " + last_name;
        } else {
            return first_name;
        }
    }
}
