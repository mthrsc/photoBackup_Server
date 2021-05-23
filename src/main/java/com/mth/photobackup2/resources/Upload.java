
package com.mth.photobackup2.resources;

import com.mth.photobackup2.Objects.PhotoObject;
import com.mth.photobackup2.Services.Authentication;
import com.mth.photobackup2.Services.DiskOperation;
import com.mth.photobackup2.Services.ProbeDatabase;
import com.mth.photobackup2.Tools.JsonManipulation;
import com.mth.photobackup2.Tools.LogTool;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Matthieu Roscio
 *
 */
@Path("up")
public class Upload {

    private LogTool logTool = new LogTool();
    private Authentication auth = new Authentication();
    private DiskOperation diskOp = new DiskOperation();
    private ProbeDatabase pd = new ProbeDatabase();
    private JsonManipulation jsonMan = new JsonManipulation();

    @POST
    @Path("file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPicture(
            @FormDataParam("part") InputStream s,
            @FormDataParam("part") FormDataContentDisposition d,
            @Context HttpHeaders httpheaders, @Context HttpServletRequest request) {

        logTool.logToFile("uploadPicture file: " + d.getFileName() + " - from: " + request.getRemoteAddr());

        String authorizationHeader = httpheaders.getHeaderString("Authorization");
        System.out.println("-----------receivedPassword: " + authorizationHeader);
        PhotoObject po = null;

        if (auth.isPasswordValid(authorizationHeader)) {
            po = diskOp.writeFile(s, d.getFileName());
            String json = jsonMan.convertToJson(po);
            Response response = Response.ok(json).build();
            logTool.logToFile("uploadPicture success returns po: " + po.toString());
            return response;
        } else {
            logTool.logToFile("uploadPicture returns wpw");
            Response response = Response.ok("wpw").build();
            return response;
        }
    }

    @POST
    @Path("probe")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ProbeDB(@Context HttpHeaders httpheaders,
            PhotoObject incomingPo, @Context HttpServletRequest request) {

        logTool.logToFile("ProbeDB po: " + incomingPo.toString() + " - from: " + request.getRemoteAddr());

        String authorizationHeader = httpheaders.getHeaderString("Authorization");

        if (auth.isPasswordValid(authorizationHeader)) {
            boolean result = pd.isEntryInDatabase(incomingPo);

            if (!result) {
                //If object is not in the database 
                logTool.logToFile("ProbeDB returns: " + result);
                return Response.status(200).entity(result).build();
            } else {
                //If object is in the database
                logTool.logToFile("ProbeDB returns: " + result);
                return Response.status(418).entity(result).build();
            }
        } else {
            logTool.logToFile("ProbeDB returns: wpw");
            return Response.status(401).entity("wpw").build();
        }
    }
}
