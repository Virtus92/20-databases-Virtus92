package at.codersbay;

public class Client {
    public int id;
    public String firstname;
    public String lastname;
    public boolean active;
    public float creditLimit;

    public Client(int id, String firstname, String lastname, boolean active, float creditLimit){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.active = active;
        this.creditLimit = creditLimit;
    }

    public Client(String firstname, String lastname, boolean active, float creditLimit){
        this.firstname = firstname;
        this.lastname = lastname;
        this.active = active;
        this.creditLimit = creditLimit;
    }

    @Override
    public String toString(){
        return "Client-ID: " + id + ", Name: " + firstname + " " + lastname + " ist aktiv: " + active + " und sein Kreditlimit beträgt: " + creditLimit;
    }
}
