package main.java.sqlitejdbc;

import java.util.ArrayList;
import java.util.List;

public class SetQueryFields {
    private String query;
    private List<SetFields> fieldsList;

    public SetQueryFields() {
        fieldsList = new ArrayList<>();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public List<SetFields> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<SetFields> fieldsList) {
        this.fieldsList = fieldsList;
    }
}
