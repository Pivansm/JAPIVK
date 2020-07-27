package main.java.exporttxt;

import java.util.ArrayList;
import java.util.List;

public class TableRecordsAll {
    private List<Records> listRec;

    public TableRecordsAll() {
        listRec = new ArrayList<>();
    }

    public void addRecords(Records records) {
        listRec.add(records);
    }

    public List<Records> getListRec() {
        return listRec;
    }
}
