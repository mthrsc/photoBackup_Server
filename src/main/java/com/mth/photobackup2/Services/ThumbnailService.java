package com.mth.photobackup2.Services;

import com.mth.photobackup2.Config.ServerParameters;
import com.mth.photobackup2.Objects.PhotoObject;
import com.mth.photobackup2.Tools.LogTool;
import com.mth.photobackup2.Tools.ServerParametersManipulation;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author Matthieu Roscio
 */
public class ThumbnailService {

    private LogTool logTool = new LogTool();
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
    private ServerParametersManipulation spm = new ServerParametersManipulation();
    private ServerParameters sp = spm.getParameters();
    private final String thumbnailDir = sp.getFolder() + "\\Thumbnails\\";

    public ThumbnailService() {
    }

    public File getThumbnail(int id) {
        logTool.logToFile("getThumbnail_service starts - id: " + id);

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        PhotoObject po = em.find(PhotoObject.class, id);

        File thumbnail = new File(thumbnailDir + po.getFileName());

        if (thumbnail.exists()) {
            logTool.logToFile("getThumbnail - thumbnail found");
            return thumbnail;
        } else {
            logTool.logToFile("getThumbnail - thumbnail not found - generating a new one");
            thumbnail = generateThumbnail(po);
            logTool.logToFile("getThumbnail - returning new thumbnail");
            return thumbnail;
        }
    }

    private File generateThumbnail(PhotoObject po) {
        File originalPicture = new File(po.getLocationOnDisk());
        File thumbnail = new File(thumbnailDir + po.getFileName());

        try {
            Thumbnails.of(originalPicture)
                    .size(256, 256)
                    .toFile(thumbnail);
        } catch (IOException ex) {
            logTool.logToFile("getThumbnail - Error generating new thumbnail: " + ex.getMessage());

            Logger.getLogger(ThumbnailService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return thumbnail;
    }
}
