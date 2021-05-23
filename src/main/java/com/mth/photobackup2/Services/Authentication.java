/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.Services;

import com.mth.photobackup2.Config.ServerParameters;
import com.mth.photobackup2.Tools.Crypto;
import com.mth.photobackup2.Tools.LogTool;
import com.mth.photobackup2.Tools.ServerParametersManipulation;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author mth
 */
public class Authentication {

    private LogTool logTool = new LogTool();
    private final Crypto crypto = new Crypto();
    //Getting a filled up ServerParamsObject
    private final ServerParametersManipulation spm = new ServerParametersManipulation();
    private final ServerParameters sp = spm.getParameters();

    public Authentication() {
    }

    public boolean isPasswordValid(String authorizationHeader) {
        logTool.logToFile("isPasswordValid starts");

        boolean b = false;
        if (authorizationHeader != null) {
            String userPassword = sp.getPassword();
            String receivedHashedPassword = new String();

            String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            byte[] headerBytes = Base64.getDecoder().decode(base64Credentials);
            String decodedHeader = new String(headerBytes, StandardCharsets.UTF_8);
            // credentials format is username:password
            final String[] values = decodedHeader.split(":", 2);
            receivedHashedPassword = values[1];
            //Removing all white spaces
            receivedHashedPassword = receivedHashedPassword.replaceAll("\\s", "");

            b = crypto.verifyPassword(receivedHashedPassword, userPassword);
            logTool.logToFile("isPasswordValid returns " + b);
            return b;
        } else {
            logTool.logToFile("isPasswordValid returns " + b);
            return b;
        }
    }
}
