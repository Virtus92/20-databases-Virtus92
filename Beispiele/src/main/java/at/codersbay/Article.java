package at.codersbay;

public class Article {
    private int id;
    private String name;
    private int ownerId;

    public Article(String name, int ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    public Article(int id, String name, int ownerId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
