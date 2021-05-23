
package com.mth.photobackup2;

import com.mth.photobackup2.GUI.GUI;
import com.mth.photobackup2.Tools.LogTool;

/**
 *
 * @author Matthieu Roscio
 */
public class Main {
    
    public static void main(String[] args) {

        LogTool logTool = new LogTool();
        logTool.logToFile("Server app started");
        
        GUI gui = new GUI();
        gui.startGUI();
        
    }
}
