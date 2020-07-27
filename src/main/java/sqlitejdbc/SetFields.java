package main.java.sqlitejdbc;

public class SetFields {
    private String fieldName;
    private String fieldType;

    public SetFields() {

    }

    public SetFields(String _fieldName, String _fieldType) {
        this.fieldName = _fieldName;
        this.fieldType = _fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

}
