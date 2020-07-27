package main.java.sqlitejdbc;

import main.java.exporttxt.Records;
import main.java.exporttxt.TableRecordsAll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static main.java.runner.Main.LOGGER;

public class SQLiteDAO extends AbstractDAO {
    private static final String SQL_CRT_TBL1 = "CREATE TABLE IF NOT EXISTS POSTVK(IDPOST INTEGER, " +
            "IDGROUP INTEGER, CAPTVK VARCHAR(250), POSTTXT TEXT, IDCLIENT INTEGER, FIO VARCHAR(200), " +
            "BIRTHDT VARCHAR(20))";
    private static final String SQL_META_TBL = "SELECT * FROM POSTVK";
    private static final String SQL_CRT_TBL2 = "CREATE TABLE IF NOT EXISTS COMMENTVK(IDCOMM INTEGER, " +
            "IDGROUP INTEGER, IDPOST INTEGER, COMMTXT TEXT, IDCLIENT INTEGER, FIO VARCHAR(200), BIRTHDT VARCHAR(20))";
    private static final String SQL_INSERT_TBL2 = "INSERT INTO COMMENTVK(IDCOMM, " +
            "IDGROUP, COMMTXT, FIO, BIRTCHDT) VALUES (?, ?, ?, ?, ?)";

    public SQLiteDAO(Connection connection) {
        super(connection);
    }

    @Override
    public void runnerToQuery(String query) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(query);
            st.execute();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void createTable(){
        //Первая таблица
        runnerToQuery(SQL_CRT_TBL1);
        //Вторая таблица
        runnerToQuery(SQL_CRT_TBL2);
    }



    public SetQueryFields fieldsToSqlParameter(String nmTable) {
        SetQueryFields queryFields = new SetQueryFields();
        PreparedStatement st = null;
        try
        {
            st = connection.prepareStatement("SELECT * FROM " + nmTable);
            ResultSet rs = st.executeQuery();
            ResultSetMetaData metaData = st.getMetaData();
            List<String> stringList = new ArrayList<>();
            List<String> stringParam = new ArrayList<>();
            String sqlInz = "INSERT INTO " + nmTable + " ";
            for(int i = 1, collCount = metaData.getColumnCount(); i <= collCount; i++) {
                stringList.add(metaData.getColumnName(i));
                stringParam.add("?");
                queryFields.getFieldsList().add(new SetFields(metaData.getColumnName(i), metaData.getColumnTypeName(i)));
            }
            sqlInz += "(" + String.join(",", stringList) + ") VALUES (" + String.join(",", stringParam) + ")";
            st.close();
            queryFields.setQuery(sqlInz);

            return queryFields;
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public void insertBatch(TableRecordsAll tbl, SetQueryFields insertQuery, int iBatch) {
        if(tbl != null) {
            PreparedStatement st = null;
            try {
                st = connection.prepareStatement(insertQuery.getQuery());
                int countRow = 0;
                for (Records row : tbl.getListRec()) {
                    for (int i = 0, collCount = insertQuery.getFieldsList().size(); i < collCount; i++) {
                        SetFields field = insertQuery.getFieldsList().get(i);
                        Object coll = null;
                        if (row.cellCount() > i)
                            coll = row.getCell(i);
                        if (coll == null) {
                            st.setNull(i + 1, Types.NULL);
                            continue;
                        }

                        if(field.getFieldType().equals("VARCHAR")) {
                            st.setString(i+1, coll.toString());
                        }
                        if(field.getFieldType().equals("INTEGER")) {
                            int ci = Integer.parseInt(coll.toString());
                            st.setInt(i+1, ci);
                        }
                        if(field.getFieldType().equals("TEXT")) {
                            st.setString(i+1, coll.toString());
                        }

                    }
                    //Added to Batch
                    st.addBatch();
                    //Сброс данных в БД
                    countRow++;
                    if(countRow % iBatch == 0) {
                        int[] updateBatch = st.executeBatch();
                        st.clearBatch();
                    }

                }
                //
                int[] updateBatch = st.executeBatch();
                st.clearBatch();
                st.close();
                System.out.println("Insert Batch finish!");

            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void insertData() {
        try
        {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_TBL2);
            statement.setString(1, "");
            statement.setString(2, "");
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setString(5, "");

            statement.execute();
            statement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
