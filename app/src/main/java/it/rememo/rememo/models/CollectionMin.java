package it.rememo.rememo.models;

public class CollectionMin {
    private String id;
    private String name;
    private String description;
    private int numberOfItems;

    public CollectionMin(String id, String name, String description, int numberOfItems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numberOfItems = numberOfItems;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }
}
