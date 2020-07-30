package main.java.runner;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.responses.SearchResponse;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;

import main.java.apivk.ApiPostVK;
import main.java.apivk.VkUserDul;
import main.java.exporttxt.Records;
import main.java.exporttxt.ResultSetToTxt;
import main.java.exporttxt.TableRecordsAll;
import main.java.setting.DBConnector;
import main.java.setting.Setting;
import main.java.setting.SettingJson;
import main.java.sqlitejdbc.SQLiteDAO;
import main.java.sqlitejdbc.SetQueryFields;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainLaunch {
    private Setting setting;
    private SettingJson settingJson;
    private Connection connection;
    private DBConnector connector;
    private SQLiteDAO sqLiteDAO;

    public MainLaunch() throws SQLException, ClassNotFoundException {
        settingJson = new SettingJson();
        settingJson.create();
        setting = settingJson.findEntityBy();

        String conn_driver = "org.sqlite.JDBC";
        String conn_url = "jdbc:sqlite:dbsqlrb.sqlite";
        connector = new DBConnector(conn_driver, conn_url, null, null);
        sqLiteDAO = new SQLiteDAO(connector.getConnection());
    }

    public void processingFoundation() {
        try {
            //Строка вставки
            SetQueryFields insertQuery = sqLiteDAO.fieldsToSqlParameter("POSTVK");
            SetQueryFields insertComm = sqLiteDAO.fieldsToSqlParameter("COMMENTVK");
            //Заполнение тбл
            //postToFileTxt();
            //postToTableSqlite(insertQuery, insertComm);
            //postToTableReport();
            //getToGroupById();
            //Получить Id группы по краткому названии из файла
            //getToIdGroupReport();

            //Клиент
            //userFindId("492829072");
            //
            getToIdUserReport();
            //Список ID клиентов поиск
            //getUsersToId("");
            //Список друзей
            //getFriendsToId();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postToTableSqlite(SetQueryFields insertQuery, SetQueryFields insertComm) {

        ResultSetToTxt toTxt = new ResultSetToTxt("postvk.csv");
        ResultSetToTxt commToTxt = new ResultSetToTxt("commvk.csv");
        ApiPostVK apiPostVK = new ApiPostVK(setting);
        //ExecutorService executor = Executors.newFixedThreadPool(1);

        String[] strGroups = setting.getGroup_id().split("[, ]+");
        for(String group : strGroups) {
            System.out.println("Сообщество: " + group);
            GetResponse getPostGrp = apiPostVK.getPostGroup(Integer.parseInt(group));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
            int countZ = getPostGrp.getCount();
            System.out.println("Количество постов:" + countZ);
            TableRecordsAll tbl = new TableRecordsAll();

            for (var gr : getPostGrp.getItems()) {
                Records rs = new Records();

                rs.addCell(gr.getId());
                rs.addCell(group);

                String dateL = getDateFormatter(gr.getDate());
                System.out.println("Id:" + gr.getId() + " userId:" + gr.getFromId() + " data:" + dateL +" :" + gr.getText());
                rs.addCell(dateL);
                //Данные о клиенте
                VkUserDul userFioDr = getUserDul(apiPostVK, "" + gr.getFromId());
                System.out.println("ФИО: " + userFioDr.getFullName() + " Др:" + userFioDr.getBdate());
                //Каптион
                String strCaption = getCaption(gr.getAttachments());
                //Комментарии
                exportCommentsToRep(apiPostVK, group, gr.getId(), insertComm, commToTxt);
                //executor.submit(new CommentRunable(apiPostVK, group, gr.getId()));
                //executor.submit(() ->{
                //    String threadName = Thread.currentThread().getName();
                //    System.out.println("Gr" + threadName + " :" + group);
                //    exportCommentsToRep(apiPostVK, group, gr.getId(), insertComm, commToTxt);
                //});

                rs.addCell(strCaption);
                rs.addCell(gr.getText());
                rs.addCell(gr.getFromId());
                rs.addCell(userFioDr.getFullName());
                rs.addCell(userFioDr.getBdate());
                tbl.addRecords(rs);
            }
            int iCountPst = 0;
            for (int j = 11; j < countZ; j++) {
                if (j % 10 == 0) {
                    GetResponse getPostGrp2 = apiPostVK.getPostGroupOffs10(Integer.parseInt(group), j);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.fillInStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    if(getPostGrp2 != null) {
                        for (WallPostFull gr : getPostGrp2.getItems()) {
                            String dateL = getDateFormatter(gr.getDate());
                            System.out.println("Id:" + gr.getId() + " userId: " + gr.getFromId() + " data:" + dateL + ": " + gr.getText());
                            Records rs = new Records();

                            rs.addCell(gr.getId());
                            rs.addCell(group);
                            rs.addCell(dateL);
                            //Данные о клиенте
                            VkUserDul userFioDr = getUserDul(apiPostVK, "" + gr.getFromId());
                            System.out.println("ФИО: " + userFioDr.getFullName() + " Др:" + userFioDr.getBdate());
                            //Каптион
                            String strCaption = getCaption(gr.getAttachments());
                            //Комментарии
                            exportCommentsToRep(apiPostVK, group, gr.getId(), insertComm, commToTxt);
                            //new CommentRunable(apiPostVK, group, gr.getId());
                            //executor.submit(new CommentRunable(apiPostVK, group, gr.getId()));
                            ///executor.submit(() ->{
                            //    String threadName = Thread.currentThread().getName();
                            //    System.out.println("Gr" + threadName + " :" + group);
                            //    exportCommentsToRep(apiPostVK, group, gr.getId(), insertComm, commToTxt);
                            //});

                            rs.addCell(strCaption);
                            rs.addCell(gr.getText());
                            rs.addCell(gr.getFromId());
                            rs.addCell(userFioDr.getFullName());
                            rs.addCell(userFioDr.getBdate());
                            tbl.addRecords(rs);
                        }

                        iCountPst++;

                    }

                }

                if(iCountPst >= 100) {
                    toTxt.toFileTxtExport(tbl);
                    System.out.println("Запись данных в БД!");
                    tbl = new TableRecordsAll();
                    iCountPst = 0;
                }
            }

            //
            System.out.println("Запись данных в БД!");
            //sqLiteDAO.insertBatch(tbl, insertQuery, 1000);
            toTxt.toFileTxtExport(tbl);
            toTxt.fileClose();
            commToTxt.fileClose();
        }

        //try {
            //System.out.println("attempt to shutdown executor");
            //executor.shutdown();
            //executor.awaitTermination(5, TimeUnit.SECONDS);
        //}
        //catch (InterruptedException e) {
        //    System.err.println("tasks interrupted");
        //}
    }

    public VkUserDul getUserDul(ApiPostVK apiPostVK, String clientId) {

        VkUserDul userFioDr = new VkUserDul();
        try {
            if (clientId.indexOf('-') == -1) {
                userFioDr = apiPostVK.getClientPost(clientId);

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    ex.fillInStackTrace();
                    Thread.currentThread().interrupt();
                }
            } else {
                userFioDr.setFirst_name("Модератор");
            }

            return userFioDr;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getCaption(List<WallpostAttachment> at) {

        String strCaption = "";
        if (at != null) {
            for (int i = 0; i < at.size(); i++) {
                var ati = at.get(i);
                System.out.println("" + ati.getLink());
                var lk = ati.getLink();
                if (lk != null) {
                    System.out.println(lk.getCaption());
                    strCaption += lk.getCaption() + ",";
                }
            }
        }

        return strCaption;
    }

    public void exportCommentsToRep(ApiPostVK apiPostVK, String group, int commentId, SetQueryFields insertQuery, ResultSetToTxt commToTxt) {

        GetCommentsResponse commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            ex.fillInStackTrace();
            Thread.currentThread().interrupt();
        }
        if(commentsAll != null) {
            TableRecordsAll tbl = new TableRecordsAll();
            for (var cm : commentsAll.getItems()) {
                Records rsUser = new Records();

                rsUser.addCell(cm.getId());
                rsUser.addCell(group);

                rsUser.addCell(commentId);

                var comm = cm.getText();
                String dateL = getDateFormatter(cm.getDate());
                System.out.println(":" + cm.getFromId() + " data:" + dateL + " text:" + comm);
                rsUser.addCell(dateL);

                rsUser.addCell(comm);
                rsUser.addCell(cm.getFromId());

                if (cm.getFromId() > 0) {
                    //Данные о клиенте
                    VkUserDul userFioDr = getUserDul(apiPostVK, "" + cm.getFromId());
                    System.out.println("Фам: " + userFioDr.getFullName() + " Имя: " + " Др:" + userFioDr.getBdate());

                    rsUser.addCell(userFioDr.getFullName());
                    rsUser.addCell(userFioDr.getBdate());

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.fillInStackTrace();
                        Thread.currentThread().interrupt();
                    }

                } else {

                    rsUser.addCell("Модератор");
                    rsUser.addCell(null);
                }
                tbl.addRecords(rsUser);
            }

            //sqLiteDAO.insertBatch(tbl, insertQuery, 1000);
            commToTxt.toFileTxtExport(tbl);
        }

    }

    public HashSet<String> importFileGroup(String inFile) {
        HashSet<String> hashSet = new HashSet<>();
        File file = new File(inFile);

        try
        {
            BufferedReader b = new BufferedReader(new FileReader(file));
            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                hashSet.add(readLine);
            }

            b.close();

            return hashSet;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getToIdGroupReport() {

        ApiPostVK apiPostVK = new ApiPostVK(setting);
        HashSet<String> hashSet = importFileGroup("bdnmgroup.txt");

        for(String gr : hashSet) {
            apiPostVK.getIDGroup(gr);

        }
    }

    public void getToIdUserReport() {

        ResultSetToTxt toTxtUser = new ResultSetToTxt("vkuser.csv");
        ResultSetToTxt toTxtFriends = new ResultSetToTxt("vkfriends.csv");
        ApiPostVK apiPostVK = new ApiPostVK(setting);
        HashSet<String> hashSet = importFileGroup("c:\\Sun\\IN\\dfiodr2.txt");
        System.out.println("Size:" + hashSet.size());

        for(String gr : hashSet) {
            //apiPostVK.getIDGroup(gr);
            //System.out.println("Id:" + gr);
            String[] nmUser = gr.split("\t");
            System.out.println("Id:" + nmUser[1] + " " + nmUser[0] + " DR:" + nmUser[2] + "." + nmUser[3]);
            getUsersToId(nmUser[1] + " " + nmUser[0], Integer.parseInt(nmUser[2]), Integer.parseInt(nmUser[3]), toTxtUser, toTxtFriends);
            //getUsersToId("Иван Пермяков", Integer.parseInt("8"), Integer.parseInt("9"), toTxtUser, toTxtFriends);

            //try {
            //    apiPostVK.getUserToId("Пермяков Иван", "8", "9");

            //} catch (IOException e) {
            //    e.printStackTrace();
            //} catch (ApiException e) {
            //    e.printStackTrace();
            //} catch (ClientException e) {
            //    e.printStackTrace();
            //}
        }
    }

    public String getDateFormatter(long vkDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateL = Instant.ofEpochSecond(vkDate)
                .atZone(ZoneId.of("GMT-4"))
                .format(formatter);
        return dateL;
    }

    public void getFriendsToId(int nmUser, ResultSetToTxt commToTxt) {
        ApiPostVK apiPostVK = new ApiPostVK(setting);

        com.vk.api.sdk.objects.friends.responses.GetResponse friendsAll = apiPostVK.getFriendsOffs10(nmUser, 0);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            ex.fillInStackTrace();
            Thread.currentThread().interrupt();
        }
        if(friendsAll != null) {
            TableRecordsAll tblFd = new TableRecordsAll();
            for (var fd : friendsAll.getItems()) {
                Records rsFriends = new Records();
                System.out.println("Id:" + fd.toString());
                VkUserDul userFioDr = getUserDul(apiPostVK, "" + fd.toString());
                System.out.println("Фам: " + userFioDr.getFullName() + " Имя: " + " Др:" + userFioDr.getBdate());

                rsFriends.addCell(fd.toString());
                rsFriends.addCell(nmUser);
                rsFriends.addCell(userFioDr.getFullName());
                rsFriends.addCell(userFioDr.getBdate());
                //В локал таблицу
                tblFd.addRecords(rsFriends);
            }
            commToTxt.toFileTxtExport(tblFd);
        }
    }

    public void getUsersToId(String nmUser, int nmDay, int nmMonth, ResultSetToTxt commToTxt, ResultSetToTxt commToFd) {
        ApiPostVK apiPostVK = new ApiPostVK(setting);

        SearchResponse usersAll = apiPostVK.getUsersIdOffs10(nmUser, nmDay, nmMonth);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            ex.fillInStackTrace();
            Thread.currentThread().interrupt();
        }
        if(usersAll != null) {
            TableRecordsAll tbl = new TableRecordsAll();
            for (var fd : usersAll.getItems()) {
                Records rsUser = new Records();
                System.out.println("Id:" + fd.toString());
                VkUserDul userFioDr = getUserDul(apiPostVK, "" + fd.getId());
                System.out.println("Фам: " + userFioDr.getFullName() + " Имя: " + " Др:" + userFioDr.getBdate());

                rsUser.addCell(fd.getId());
                rsUser.addCell(userFioDr.getFullName());
                rsUser.addCell(userFioDr.getBdate());

                //Друзья
                getFriendsToId(fd.getId(), commToFd);

                //В локал таблицу
                tbl.addRecords(rsUser);
            }
            commToTxt.toFileTxtExport(tbl);
        }
    }

}
