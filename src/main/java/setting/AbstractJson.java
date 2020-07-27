package main.java.setting;

public abstract class AbstractJson<T extends Entity> {
    public abstract T findEntityBy();
    public abstract boolean create();
}
