
package com.mth.photobackup2.Services;

import com.mth.photobackup2.Objects.PhotoObject;
import com.mth.photobackup2.Tools.LogTool;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Matthieu Roscio
 */
public class ProbeDatabase {

    private LogTool logTool = new LogTool();

    public ProbeDatabase() {
    }

    //The boolean returned answers the question
    //isEntryInDatabase: yes or no - true or false
    public boolean isEntryInDatabase(PhotoObject incomingPo) {
        logTool.logToFile("Searching database for: " + incomingPo.getFileName());
        System.out.println("\n\n-----Checking database for file " + incomingPo.getFileName());
        boolean b = false;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        String queryText = "SELECT po FROM PhotoObject po WHERE po.fileName = :fileName";
        Query query = em.createQuery(queryText);
        query.setParameter("fileName", incomingPo.getFileName());

        try {
            ArrayList queryList = (ArrayList) query.getResultList();

            if (queryList.isEmpty()) {
                System.out.println("-----isEntryInDatabase Query result is empty\n\n");
                b = false;
                logTool.logToFile("Searching database for: " + incomingPo.getFileName() + " - Result: " + b);
                return b;
            } else {
                for (int i = 0; i < queryList.size(); i++) {
                    PhotoObject localPO = (PhotoObject) queryList.get(i);
                    if (localPO.toStringCompare().equals(incomingPo.toStringCompare())) {
                        //The 2 files are the same, decline upload
                        System.out.println("-----File already present: " + incomingPo.getFileName() + "\n\n");
                        b = true;
                        logTool.logToFile("Searching database for: " + incomingPo.getFileName() + " - Result: " + b);
                        return b;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("-----Error while checking database\n\n");
            e.printStackTrace();
            logTool.logToFile("Searching database Error: " + e.getMessage());

            //We play it safe. If the database cannot be contacted
            //we decline all upload until contact is re-established
            b = true;
            return b;
        }
        return b;
    }

    public ArrayList getThirtyFromServerList() {
        logTool.logToFile("getThirtyFromServerList starts");

        final int limit = 30;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        String queryText = "SELECT po FROM PhotoObject po ORDER BY po.dateUploaded DESC";
        Query query = em.createQuery(queryText);

        ArrayList<PhotoObject> queryList = (ArrayList<PhotoObject>) query.getResultList();
        ArrayList<PhotoObject> thirtyFromServerList = new ArrayList();

        for (int i = 0; i < queryList.size(); i++) {
            thirtyFromServerList.add(queryList.get(i));
            if (i >= limit) {
                return thirtyFromServerList;
            }
        }

        for (int i = 0; i < thirtyFromServerList.size(); i++) {
            System.out.println("Name: " + thirtyFromServerList.get(i).getFileName() + " -DateTaken: " + thirtyFromServerList.get(i).getDateTaken());
        }

        logTool.logToFile("getThirtyFromServerList ends - returning " + thirtyFromServerList.size() + " objects");

        return thirtyFromServerList;
    }

    public PhotoObject getUpdatedPhotoObject(PhotoObject incomingPo) {
        logTool.logToFile("getUpdatedPhotoObject Starts");

        PhotoObject photoObject = null;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        String queryText = "SELECT po FROM PhotoObject po WHERE po.fileName = :fileName";
        Query query = em.createQuery(queryText);
        query.setParameter("fileName", incomingPo.getFileName());

        try {
            ArrayList queryList = (ArrayList) query.getResultList();

            if (queryList.isEmpty()) {
                System.out.println("-----isEntryInDatabase Query result is empty\n\n");
                logTool.logToFile("getUpdatedPhotoObject Ends - return null");
                return null;
            } else {
                for (int i = 0; i < queryList.size(); i++) {
                    PhotoObject localPO = (PhotoObject) queryList.get(i);
                    if (localPO.toStringCompare().equals(incomingPo.toStringCompare())) {
                        //The 2 files are the same, return updated object
                        System.out.println("-----Found updated match: " + localPO.toString() + "\n\n");
                        logTool.logToFile("getUpdatedPhotoObject Ends - return " + localPO.toString());
                        return localPO;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("-----Error while checking database\n\n");
            logTool.logToFile("getUpdatedPhotoObject Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        logTool.logToFile("getUpdatedPhotoObject Ends - return null");
        return photoObject;
    }
}
