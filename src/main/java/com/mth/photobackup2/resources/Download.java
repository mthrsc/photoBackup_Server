/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.resources;

import com.mth.photobackup2.Objects.PhotoObject;
import com.mth.photobackup2.Services.Authentication;
import com.mth.photobackup2.Services.ProbeDatabase;
import com.mth.photobackup2.Services.ThumbnailService;
import com.mth.photobackup2.Tools.JsonManipulation;
import com.mth.photobackup2.Tools.LogTool;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author mth
 */
@Path("down")
public class Download {

    private LogTool logTool = new LogTool();
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("pers2");
    private EntityManager em = emf.createEntityManager();
    private EntityTransaction tx = em.getTransaction();
    private Authentication auth = new Authentication();
    private JsonManipulation jsonMan = new JsonManipulation();
    private ProbeDatabase probeDatabase = new ProbeDatabase();
    private ThumbnailService thumbnailService = new ThumbnailService();

    @GET
    @Path("file/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFileWithGet(@Context HttpHeaders httpheaders,
            @Context HttpServletRequest request, @PathParam("id") int id) {

        logTool.logToFile("downloadFileWithGet - id: " + id + " from: " + request.getRemoteAddr());
        int pictureId = id;
        String authorizationHeader = httpheaders.getHeaderString("Authorization");
        System.out.println("-----------receivedPassword: " + authorizationHeader);

        if (auth.isPasswordValid(authorizationHeader)) {

            PhotoObject po = em.find(PhotoObject.class, pictureId);
            File file = new File(po.getLocationOnDisk());
            if (po == null) {
                logTool.logToFile("downloadFileWithGet - No photo Object with that ID: " + id);
                System.out.println("--------No photo Object with that ID");
            }

            ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition", "attachment;filename=" + po.getFileName());
            logTool.logToFile("downloadFileWithGet - returning picture");
            return response.build();
        } else {
            logTool.logToFile("downloadFileWithGet - returning wpw");
            return Response.status(401).entity("wpw").build();
        }
    }

    @GET
    @Path("list")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getThirtyFromServerList(@Context HttpHeaders httpheaders, @Context HttpServletRequest request) {

        logTool.logToFile("getThirtyFromServerList from: " + request.getRemoteAddr());

        System.out.println("-----------getThirtyFromServerList");

        String authorizationHeader = httpheaders.getHeaderString("Authorization");

        List<PhotoObject> list = null;

        if (auth.isPasswordValid(authorizationHeader)) {
            list = probeDatabase.getThirtyFromServerList();
            String json = jsonMan.convertToJson(list);
            Response response = Response.ok(json).build();
            logTool.logToFile("getThirtyFromServerList success returns " + list.size() + " objects");
            return response;
        }

        logTool.logToFile("getThirtyFromServerList returns ");
        Response response = Response.ok("wpw").build();
        return response;
    }

    //Get thumbnails
    @GET
    @Path("thumb/{id}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getThumbnail(@Context HttpHeaders httpheaders, @Context HttpServletRequest request,
            @PathParam("id") int id) {

        logTool.logToFile("getThumbnail starts - id: " + id + " from: " + request.getRemoteAddr());

        System.out.println("-----------getThumbnail");

        int pictureId = id;
        String authorizationHeader = httpheaders.getHeaderString("Authorization");

        if (auth.isPasswordValid(authorizationHeader)) {
            File thumbnail = thumbnailService.getThumbnail(pictureId);
            ResponseBuilder response = Response.ok((Object) thumbnail);
            response.header("Content-Disposition", "attachment;filename=" + thumbnail.getName());
            logTool.logToFile("getThumbnail success response");
            return response.build();
        }

        logTool.logToFile("getThumbnail response wpw");
        return Response.status(401).entity("wpw").build();
    }

    @POST
    @Path("request")
    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response requestPhotoObject(@Context HttpHeaders httpheaders, @Context HttpServletRequest request,
            PhotoObject po) {

        logTool.logToFile("requestPhotoObject starts - po: " + po.toString() + " from: " + request.getRemoteAddr());

        System.out.println("-----------requestPhotoObject");

        PhotoObject incomingPo = po;
        System.out.println("-----incomingPo: " + incomingPo.toString());

        String authorizationHeader = httpheaders.getHeaderString("Authorization");
        PhotoObject returnPo = null;

        if (auth.isPasswordValid(authorizationHeader)) {
            returnPo = probeDatabase.getUpdatedPhotoObject(incomingPo);
            String json = jsonMan.convertToJson(returnPo);
            Response response = Response.ok(json).build();
            logTool.logToFile("getThumbnail returnPo: " + returnPo.toString());
            return response;
        }
        logTool.logToFile("getThumbnail returns wpw");
        Response response = Response.ok("wpw").build();
        return response;
    }
}
