/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.Tools;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 *
 * @author mth
 */
public class Crypto {

    private LogTool logTool = new LogTool();

    public boolean verifyPassword(String receivedPassword, String userPassword) {
//        logTool.logToFile("verifyPassword starts");
        boolean b = BCrypt.checkpw(userPassword, receivedPassword);
//        logTool.logToFile("verifyPassword ends - return: " + b);
        return b;
    }

    public String encryptPassword(String password) {
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(4));

        System.out.println("clearPassword: " + password + " - encrypted: " + encryptedPassword);

        return encryptedPassword;
    }

}
