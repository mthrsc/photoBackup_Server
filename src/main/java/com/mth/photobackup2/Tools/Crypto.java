
package com.mth.photobackup2.Tools;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 *
 * @author Matthieu Roscio
 */
public class Crypto {

    private LogTool logTool = new LogTool();

    public boolean verifyPassword(String receivedPassword, String userPassword) {
        boolean b = BCrypt.checkpw(userPassword, receivedPassword);
        return b;
    }

    public String encryptPassword(String password) {
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(4));

        return encryptedPassword;
    }

}
