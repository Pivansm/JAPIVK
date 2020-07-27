package main.java.setting;

import java.io.Serializable;

public class Entity implements Serializable, Cloneable{
    private int id;

    public Entity() {
    }

    public Entity(int id) { this.id = id; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
