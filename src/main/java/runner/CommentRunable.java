package main.java.runner;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import main.java.apivk.ApiPostVK;
import main.java.apivk.VkUserDul;
import main.java.exporttxt.Records;
import main.java.exporttxt.TableRecordsAll;

import java.io.IOException;

public class CommentRunable implements Runnable {
    ApiPostVK apiPostVK;
    GetCommentsResponse commentsAll;
    String group;
    int commentId;
    Thread t;
    String name;

    public CommentRunable(ApiPostVK apiPostVK, String group, int commentId) {
        this.apiPostVK = apiPostVK;
        this.group = group;
        this.commentId = commentId;
        this.name = "Gr" + group;
        t = new Thread(this, name);
        t.start();
    }

    @Override
    public void run() {

        commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);
        synchronized (commentsAll) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
            //commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);


            if (commentsAll != null) {
                TableRecordsAll tbl = new TableRecordsAll();
                for (var cm : commentsAll.getItems()) {
                    Records rsUser = new Records();

                    rsUser.addCell(cm.getId());
                    rsUser.addCell(group);
                    rsUser.addCell(commentId);

                    var comm = cm.getText();
                    System.out.println(":" + cm.getFromId() + " text:" + comm);

                    rsUser.addCell(comm);
                    rsUser.addCell(cm.getFromId());

                    if (cm.getFromId() > 0) {
                        //Данные о клиенте
                        VkUserDul userFioDr = getUserDul(apiPostVK, "" + cm.getFromId());
                        System.out.println("Фам: " + userFioDr.getFullName() + " Имя: " + " Др:" + userFioDr.getBdate());

                        rsUser.addCell(userFioDr.getFullName());
                        rsUser.addCell(userFioDr.getBdate());

                        try {
                            Thread.sleep(1000);
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
                //commToTxt.toFileTxtExport(tbl);
            }
        }

    }

    public VkUserDul getUserDul(ApiPostVK apiPostVK, String clientId) {

        VkUserDul userFioDr = new VkUserDul();
        try {
            if (clientId.indexOf('-') == -1) {
                userFioDr = apiPostVK.getClientPost(clientId);

                try {
                    Thread.sleep(1000);
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

    // Фабричный метод, создающий и сразу же запускающий
    // поток на исполнение
    public static CommentRunable createAndStart(ApiPostVK apiPostVK, String group, int commentId) {
        CommentRunable myThrd = new CommentRunable(apiPostVK, group, commentId);
        myThrd.t.start();
        return myThrd;
    }
}
