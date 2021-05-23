
package com.mth.photobackup2.Tools;

import com.mth.photobackup2.Config.ServerParameters;
import com.mth.photobackup2.Objects.PhotoObject;
import com.mth.photobackup2.PhotoObjectBuilder.PhotoBuilder;
import java.io.File;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JLabel;

/**
 *
 * @author Matthieu Roscio
 */
public class DatabaseTool extends Thread {

    private LogTool logTool = new LogTool();
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
    private boolean duplicate;
    private ServerParametersManipulation spm = new ServerParametersManipulation();
    private ServerParameters sp = spm.getParameters();
    private File photoDirectory;
    private Thread t;
    private String threadName;
    private JLabel serverStatusText;

    public DatabaseTool(String threadName, JLabel serverStatusText) {
        this.threadName = threadName;
        this.serverStatusText = serverStatusText;
        t = new Thread(this, this.threadName);
        duplicate = false;
        photoDirectory = new File(sp.getFolder() + "\\Pictures\\");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        logTool.logToFile("Database consolidation starting");

        t.start();
    }

    @Override
    public void run() {
        serverStatusText.setText("DatabaseTool consolidation started - Server stopped");

        removeFromDB();
        addToDB();
        if (duplicate) {
            removeDuplicate();
        }

        serverStatusText.setText("DatabaseTool consolidation Finished - Server stopped");
        logTool.logToFile("Database consolidation finished");
    }

    public void removeFromDB() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();

            tx.begin();

            int firstID = 0;
            int lastID = 0;

            try {
                firstID = (Integer) em.createQuery("SELECT MIN(id) FROM PhotoObject").getSingleResult();
                lastID = (Integer) em.createQuery("SELECT MAX(id) FROM PhotoObject").getSingleResult();
            } catch (Exception e) {

            } finally {
                tx.commit();
                em.close();
            }

            if (firstID != lastID) {
                System.out.println("----- firstID id is: " + firstID);
                System.out.println("----- lastID id is: " + lastID);

                for (int i = firstID; i < lastID; i++) {
                    EntityManager emRemove = emf.createEntityManager();
                    EntityTransaction txRemove = emRemove.getTransaction();
                    try {
                        PhotoObject po;

                        if ((po = emRemove.find(PhotoObject.class, i)) != null) {
                            System.out.println("-----po.id = " + po.getId());

                            if (!fileExists(po.getLocationOnDisk())) {
                                txRemove.begin();

                                System.out.println("-----File not present on disk, removing: " + po.getId());
                                emRemove.remove(po);
                                txRemove.commit();
                                emRemove.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("-----Error from removeFromDB");
                        e.printStackTrace();
                    } finally {
                        emRemove.close();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("-----Error from removeFromDB");
            e.printStackTrace();
        } finally {
            em.close();
        }
        System.out.println("-----removeFromDB FINISHED");
    }

    public void addToDB() {
        EntityManager em0 = null;
        PhotoBuilder pob = new PhotoBuilder();

        for (File f : photoDirectory.listFiles()) {
            try {
                em0 = emf.createEntityManager();
                String path = f.getAbsolutePath();

                String queryText = "SELECT po FROM PhotoObject po WHERE po.locationOnDisk = :path";
                Query query = em0.createQuery(queryText);
                query.setParameter("path", path);

                int count = query.getResultList().size();
                em0.close();
                System.out.println("----- count: " + count + " for file: " + path + "\n\n");

                if (count == 0) {
                    //add to DB
                    EntityManager emPersist = emf.createEntityManager();
                    EntityTransaction txPersist = emPersist.getTransaction();
                    txPersist.begin();

                    PhotoObject po = pob.buildDbRebuild(f);
                    emPersist.persist(po);

                    txPersist.commit();
                    emPersist.close();
                } else if (count > 1) {
                    //Enable duplicate fix
                    duplicate = true;
                }
            } catch (Exception e) {
                System.out.println("-----Error from addToDB");
                e.printStackTrace();
            } finally {
                em0.close();
            }
        }
    }

    public void removeDuplicate() {
        EntityManager em1 = emf.createEntityManager();
        EntityTransaction tx1 = em1.getTransaction();
        ArrayList<PhotoObject> poToRemove = new ArrayList();

        try {
            tx1.begin();

            int startID = (Integer) em1.createQuery("SELECT MIN(id) FROM PhotoObject").getSingleResult();
            int lastID = (Integer) em1.createQuery("SELECT MAX(id) FROM PhotoObject").getSingleResult();

            do {
                PhotoObject po;

                if ((po = em1.find(PhotoObject.class, startID)) != null) {
                    for (int i = startID + 1; i <= lastID; i++) {
                        PhotoObject po2;
                        if ((po2 = em1.find(PhotoObject.class, i)) != null) {
                            if (po.getLocationOnDisk().equals(po2.getLocationOnDisk())) {
                                if (!poToRemove.contains(po2)) {
                                    poToRemove.add(po2);
                                }
                            }
                        }
                    }
                }
                startID++;
            } while (startID <= lastID);
        } catch (Exception e) {
            System.out.println("-----Error from removeDuplicate");
            e.printStackTrace();
        } finally {
            tx1.commit();
            em1.close();
        }
        System.out.println("---- There is " + poToRemove.size() + " objects to remove");

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }

        for (int j = 0; j < poToRemove.size(); j++) {
            EntityManager em2 = emf.createEntityManager();
            EntityTransaction tx2 = em2.getTransaction();
            try {
                tx2.begin();

                em2.remove(em2.find(PhotoObject.class, poToRemove.get(j).getId()));

                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("-----Error removing duplicate");
            } finally {
                tx2.commit();
                em2.close();
            }
        }
        System.out.println("-----FINISHED removing duplicate");
    }

    public void cleanThumnail() {

    }

    public boolean fileExists(String path) {
        boolean b = true;
        File f = new File(path);

        if (!f.exists()) {
            b = false;
        }

        return b;
    }
}
