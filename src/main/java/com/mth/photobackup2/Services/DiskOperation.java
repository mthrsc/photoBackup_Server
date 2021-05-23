
package com.mth.photobackup2.Services;

import com.mth.photobackup2.Config.ServerParameters;
import com.mth.photobackup2.Objects.PhotoObject;
import com.mth.photobackup2.PhotoObjectBuilder.PhotoBuilder;
import com.mth.photobackup2.Tools.LogTool;
import com.mth.photobackup2.Tools.ServerParametersManipulation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Matthieu Roscio
 */
public class DiskOperation {

    private LogTool logTool = new LogTool();
    private PhotoBuilder pb = new PhotoBuilder();
    private ServerParametersManipulation spm = new ServerParametersManipulation();
    private ServerParameters sp = spm.getParameters();
    private String picturesFolder;
    private String tempFolder;
    private final ArrayList<String> imageFileExtensions = new ArrayList();

    public DiskOperation() {
    }

    public PhotoObject writeFile(InputStream incomingInputStream, String incomingFileName) {

        logTool.logToFile("writeFile - file name: " + incomingFileName);

        picturesFolder = sp.getFolder() + "\\Pictures\\";
        tempFolder = sp.getFolder() + "\\temp\\";

        imageFileExtensions.add("jpg");
        imageFileExtensions.add("jpeg");
        imageFileExtensions.add("bmp");
        imageFileExtensions.add("png");
        imageFileExtensions.add("gif");

        String extension = extractExt(incomingFileName);

        //-2 did not write / -1 already exists / >0 ID provided by the code
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            if (!imageFileExtensions.contains(extension)) {
                PhotoObject po = new PhotoObject();
                po.setId(-2);
                logTool.logToFile("writeFile - file name: " + incomingFileName + "is not a valid picture");
                return po;
            }

            File file = new File(picturesFolder + incomingFileName);

            //If file do not exist we write to disk and DB
            if (!file.exists()) {
                Files.copy(incomingInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                PhotoObject po = pb.buildAfterUpload(file);

                //Database call to verify if file is already present
                tx.begin();
                em.persist(po);
                tx.commit();
                em.close();

                System.out.println("-----Accepted File: " + po.getFileName() + "\n\n");
                logTool.logToFile("writeFile - file name: " + incomingFileName + "is ACCEPTED");

                return po;

                //Room for improvement here: What if file exists but is not in DB
                //What if file is in DB but not on disk
                //If file DO exist
            } else if (file.exists()) {
                logTool.logToFile("writeFile - file name: " + incomingFileName + " - starts decision algo.");

                //Pull existing object(s) from DB
                String queryText = "SELECT po FROM PhotoObject po WHERE po.fileName = :name";
                Query query = em.createQuery(queryText);
                query.setParameter("name", String.valueOf(file.getName() + "%"));

                ArrayList<PhotoObject> resultList = (ArrayList<PhotoObject>) query.getResultList();

                File incomingPoFile = new File(tempFolder + incomingFileName);
                Files.copy(incomingInputStream, incomingPoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                //FileUtils.contentEquals compare files content. It is interesting in our case since
                //Exif metadata are part of the file content.
                //If two files have the same name but different metadata FileUtils will see it.
                //However if two files are the same picture, but with different metadata
                //FileUtils will only see 2 different files
                //Room for improvement: compare images
                System.out.println("-----resultList size: " + resultList.size());

                for (int i = 0; i < resultList.size(); i++) {
                    File poFileOnDisk = new File(resultList.get(i).getLocationOnDisk());
                    boolean identical = FileUtils.contentEquals(incomingPoFile, poFileOnDisk);
                    if (identical) {
                        System.out.println("-----" + incomingPoFile.getName() + " already exists it is file " + poFileOnDisk.getName());
                        PhotoObject po = resultList.get(i);
                        logTool.logToFile("writeFile - file name: " + incomingFileName + "is already on diskl");
                        return po;
                    }
                }

                //Create PhotoObject of image in temp file
                PhotoObject incomingPo = pb.buildAfterUpload(incomingPoFile);

                //If they are different, incoming file must be written to disk
                //Windows do not allow 2 files with the same name, so we need to rename the incoming file
                String newFileName = createNewName(incomingPo);
                //We write the incoming file with the new name in the library
                File fileIncomingToLibrary = new File(picturesFolder + newFileName);
                Files.move(incomingPoFile.toPath(), fileIncomingToLibrary.toPath(), StandardCopyOption.REPLACE_EXISTING);

                incomingPo.setLocationOnDisk(fileIncomingToLibrary.toString());
                incomingPo.setFileName(newFileName);

                //We rename the file for Windows File system,  but we keep its original name in
                //the database.
                //If we were to change its name in the database, ALL requests to reupload it would be 
                //granted since the filename would always be different making the program
                //consider it as 2 different files and bloating the Library with 
                //several copies of the same picture.
                System.out.println("-----Setting poToWrite name to: " + incomingPo.getFileName());

                //Persist new file in DB
                tx.begin();
                em.persist(incomingPo);
                tx.commit();
                em.close();

                System.out.println("-----Accepted File: " + incomingPo.getFileName());
                logTool.logToFile("writeFile - file name: " + incomingFileName + " Accepted - New name: " + newFileName);

                return incomingPo;
            } else {
                System.out.println("-----Declined, File Error L135");
                PhotoObject po = new PhotoObject();
                po.setId(-1);
                logTool.logToFile("writeFile - file name: " + incomingFileName + " - file declined with no error");

                return new PhotoObject();
            }
        } catch (IOException ioe) {
            System.out.println("-----Declined, File Error L140");
            ioe.printStackTrace();
            PhotoObject po = new PhotoObject();
            po.setId(-1);
            logTool.logToFile("writeFile - file name: " + incomingFileName + " - Error: " + ioe.getMessage());
            return new PhotoObject();
        }
    }

    private String createNewName(PhotoObject poIncoming) {
        boolean nameFound = false;
        String currentFileName = extractName(poIncoming.getLocationOnDisk());
        String currentExtension = extractExt(poIncoming.getLocationOnDisk());
        String newFileName = new String();
        int i = 0;
        do {
            i++;
            newFileName = String.valueOf(currentFileName + i + "." + currentExtension);
            if (!new File(picturesFolder + newFileName).exists()) {
                System.out.println("----- Trying name: " + newFileName);
                nameFound = true;
            }
        } while (!nameFound);
        return newFileName;
    }

    private String extractName(String path) {
        String name = new String();
        name = path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
        return name;
    }

    private String extractExt(String path) {
        String ext = new String();
        ext = path.substring(path.lastIndexOf(".") + 1);
        ext = ext.toLowerCase();
        return ext;
    }
}
