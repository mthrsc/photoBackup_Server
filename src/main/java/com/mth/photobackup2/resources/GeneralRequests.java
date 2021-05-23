/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.resources;

import com.mth.photobackup2.Services.Authentication;
import com.mth.photobackup2.Tools.LogTool;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mth
 */
@Path("hi")
public class GeneralRequests {

    private LogTool logTool = new LogTool();
    private Authentication auth = new Authentication();

    @GET
    @Path("alive")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String alive(@Context HttpHeaders httpheaders,
            @Context HttpServletRequest request) {

        logTool.logToFile("alive ? - from: " + request.getRemoteAddr());

        String authorizationHeader = httpheaders.getHeaderString("Authorization");
        System.out.println("-----------ALIVE ?");

        if (auth.isPasswordValid(authorizationHeader)) {
            String s = "true";
            logTool.logToFile("alive ? - returns: " + s);
            return s;
        } else {
            logTool.logToFile("alive ? - returns: " + "wpw");
            return "wpw";
        }
    }
}
