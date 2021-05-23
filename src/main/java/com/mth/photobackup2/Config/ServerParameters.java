
package com.mth.photobackup2.Config;

import java.io.Serializable;

/**
 *
 * @author Matthieu Roscio
 */
public class ServerParameters implements Serializable {

    int port;
    String folder;
    String password;

    public ServerParameters() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        System.out.println("-----Inputed folder: " + folder);
        StringBuilder sb = new StringBuilder();

        //Make String backend ready
        //Load String in SBuilder
        for (int i = 0; i < folder.length(); i++) {
            sb.append(folder.charAt(i));
        }

        //Remove all '\' and re-add the expected amount
        for (int j = 0; j < sb.length(); j++) {
            if (sb.charAt(j) == '\\' && sb.charAt(j + 1) == '\\') {
                sb.deleteCharAt(j);
            } else if (sb.charAt(j) == '\\' && sb.charAt(j + 1) != '\\') {
                sb.deleteCharAt(j);
                sb.insert(j, '\\');
            }
        }

        this.folder = sb.toString();
        System.out.println("-----Formatted folder: " + folder);

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ServerParameters{" + "port=" + port + ", folder=" + folder + ", password=" + password + '}';
    }

}
