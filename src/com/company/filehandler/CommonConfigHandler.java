package com.company.filehandler;

import com.company.pojo.ProjectConfiguration;

import java.io.*;

public class CommonConfigHandler {
    public static final String CONFIG_PATH = "";
    ProjectConfiguration projectConfiguration;
    public CommonConfigHandler(){
        initialize();
        setUpFile();


    }

    private void setUpFile() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(CONFIG_PATH);
            BufferedReader br = new BufferedReader( new InputStreamReader(fileInputStream));
            //NumberOfPreferredNeighbors
            String string = br.readLine();
            String[] values =  string.split("\\s+");
            projectConfiguration.setNumberOfPreferredNeighbors(Integer.parseInt(values[1]));
            //UnchokingInterval
             string = br.readLine();
             values =  string.split("\\s+");
             projectConfiguration.setUnchokingInterval(Integer.parseInt(values[1]));
             //OptimisticUnchokingInterval
            string = br.readLine();
            values =  string.split("\\s+");
            projectConfiguration.setOptimisticUnchokingInterval(Integer.parseInt(values[1]));
            //FileName
            string = br.readLine();
            values =  string.split("\\s+");
            projectConfiguration.setFileName(values[1]);
            //FileSize
            string = br.readLine();
            values =  string.split("\\s+");
            projectConfiguration.setFileSize(Integer.parseInt(values[1]));
            //PieceSize
            string = br.readLine();
            values =  string.split("\\s+");
            projectConfiguration.setPieceSize(Integer.parseInt(values[1]));




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public ProjectConfiguration getProjectConfiguration() {
        return projectConfiguration;
    }

    private void initialize() {

    }

}
