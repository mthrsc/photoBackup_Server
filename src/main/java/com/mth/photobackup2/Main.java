/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2;

import com.mth.photobackup2.GUI.GUI;
import com.mth.photobackup2.Tools.LogTool;

/**
 *
 * @author mth
 */
public class Main {
    
    public static void main(String[] args) {
//        //Create settings folder and file
//        ServerParametersManipulation spm = new ServerParametersManipulation();
//        spm.createSaveLocation();
//        //Either finds previous setting file, or create a default one
//        ServerParameters sp = spm.getParameters();
//        System.out.println("--------------" + sp.toString());

        LogTool logTool = new LogTool();
        logTool.logToFile("Server app started");
        
        GUI gui = new GUI();
        gui.startGUI();
        
    }
}
