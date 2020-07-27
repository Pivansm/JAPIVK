package main.java.exporttxt;

import java.util.ArrayList;
import java.util.List;

public class Records<T> {
    List<T> cell;

    public Records() {
        cell = new ArrayList<>();
    }

    public void setCell(List<T> cell) {
        this.cell = cell;
    }

    public List<T> getCell() {
        return cell;
    }

    public void addCell(T cell) {
        this.cell.add(cell);
    }

    public int cellCount() {
        return cell.size();
    }

    public T getCell(int index) {
        return cell.get(index);
    }
}
