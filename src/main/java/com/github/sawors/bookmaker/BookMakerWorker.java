package com.github.sawors.bookmaker;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.FutureTask;

public class BookMakerWorker {
    
    
    
    public static void createBook(String bookprefix, String bookname, File target, Integer pagenumber, Button continuebutton, Button exitbutton, Button dirbutton, ProgressBar progressbar, TextArea progressoutput, Text progresstext){
    
        Paint basepaint = Paint.valueOf("#666666");
        Paint donepaint = Paint.valueOf("#008000");
        
        exitbutton.setDisable(false);
        continuebutton.setDisable(true);
        dirbutton.setDisable(true);
        
        progresstext.setFill(basepaint);
        
        BookMakerWorker instance = new BookMakerWorker();
        
        System.out.println('\n'+ "Starting the creation of book \"" + bookname + "\" with " + pagenumber + " pages...");
        //progressoutput.setText();
        
        String modelpath = target.getPath()+"/assets/minecraft/models/item/books/"+bookname+"/";
        String citpath = target.getPath()+"/assets/minecraft/optifine/cit/books/"+bookname+"/";
        String texturespath = target.getPath()+"/assets/minecraft/textures/item/books/"+bookname+"/";
        
        new File(modelpath).mkdirs();
        new File(citpath).mkdirs();
        new File(texturespath).mkdirs();
        
        progresstext.setText("creating directories...");
        
        new Thread(() -> {
    
            int totaltasks = 3+(pagenumber*3);
            final int[] donetasks = {0};
            
            //StringBuilder outputtext = new StringBuilder("\"Starting the creation of book \\\"\" + bookname + \"\\\" with \" + pagenumber + \" pages...\"");
            String uwu = "Starting the creation of book "+ bookname + " with " + pagenumber + " pages...";
            //progressoutput.setText("uwu");
            
            try {
        
                File closemodelfile = new File(modelpath + "_close.json");
                File closepropertiesfile = new File(citpath + "_close.properties");
                File coverimgfile = new File(texturespath + "_cover.png");
        
                closemodelfile.createNewFile();
                closepropertiesfile.createNewFile();
                coverimgfile.createNewFile();
        
                try (OutputStream output = new FileOutputStream(closemodelfile)) {
                    System.out.println("- creating _close.json");
    
                    Platform.runLater(() -> {
                        progresstext.setText("creating cover model...");
                        progressoutput.appendText("creating cover model...");
                    });
                    
                    output.write(new String(instance.getFileStream("_close.json").readAllBytes()).replaceAll("<BOOKNAME>", bookname).getBytes(StandardCharsets.UTF_8));
                    donetasks[0]++;
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                }
                try (OutputStream output = new FileOutputStream(closepropertiesfile)) {
                    System.out.println("- creating _close.properties");
                    ////progressoutput.appendText("\n- creating _close.properties");
                    
                    output.write(new String(instance.getFileStream("_close.properties").readAllBytes()).replaceAll("<BOOKNAME>", bookname).replaceAll("<PREFIX>", bookprefix).getBytes(StandardCharsets.UTF_8));
                    donetasks[0]++;
                    Platform.runLater(() -> {
                        progresstext.setText("creating cover CIT...");
                        progressoutput.appendText("\ncreating cover CIT...");
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                }
                try (OutputStream output = new FileOutputStream(coverimgfile)) {
                    System.out.println("- creating _cover.png");
                    ////progressoutput.appendText("\n- creating _cover.png");
                    
                    output.write(instance.getFileStream("_cover.png").readAllBytes());
                    donetasks[0]++;
                    
                    Platform.runLater(() -> {
                        progresstext.setText("creating cover template texture...");
                        progressoutput.appendText("\ncreating cover template texture...");
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                }
        
                for (int i = 1; i <= pagenumber; i++) {
                    File pagemodel = new File(modelpath + i + ".json");
                    File pagecit = new File(citpath + i + ".properties");
                    File pagetexture = new File(texturespath + i + ".png");
            
                    try (OutputStream output = new FileOutputStream(pagemodel)) {
                        System.out.println("- creating " + i + ".json");
                        ////progressoutput.appendText("\n- creating " + i + ".json");
                        
                        String content = new String(instance.getFileStream("PAGENUMBER.json").readAllBytes());
                        content = content.replaceAll("<BOOKNAME>", bookname);
                        content = content.replaceAll("<PAGENUMBER>", String.valueOf(i));
                        output.write(content.getBytes(StandardCharsets.UTF_8));
                        donetasks[0]++;
                        progressbar.setProgress((float) donetasks[0] / totaltasks);
    
                        int finalI = i;
                        Platform.runLater(() -> {
                            progresstext.setText("creating " + finalI + ".json");
                            progressoutput.appendText("\ncreating " + finalI + ".json");
                        });
                    }
                    try (OutputStream output = new FileOutputStream(pagecit)) {
                        System.out.println("- creating " + i + ".properties");
                        ////progressoutput.appendText("\n- creating " + i + ".properties");
                        
                        String content = new String(instance.getFileStream("PAGENUMBER.properties").readAllBytes());
                        content = content.replaceAll("<PREFIX>", bookprefix);
                        content = content.replaceAll("<BOOKNAME>", bookname);
                        content = content.replaceAll("<PAGENUMBER>", String.valueOf(i));
                        output.write(content.getBytes(StandardCharsets.UTF_8));
                        donetasks[0]++;
                        progressbar.setProgress((float) donetasks[0] / totaltasks);
    
                        int finalI = i;
                        Platform.runLater(() -> {
                            progresstext.setText("creating " + finalI + ".properties");
                            progressoutput.appendText("\ncreating " + finalI + ".properties");
                        });
                    }
                    try (OutputStream output = new FileOutputStream(pagetexture)) {
                        System.out.println("- creating " + i + ".png");
                        ////progressoutput.appendText("\n- creating " + i + ".png");
                    
                        output.write(instance.getFileStream("PAGENUMBER.png").readAllBytes());
                        donetasks[0]++;
                        progressbar.setProgress((float) donetasks[0] / totaltasks);
                        int finalI = i;
                        Platform.runLater(() -> {
                            progresstext.setText("creating " + finalI + ".png");
                            progressoutput.appendText("\ncreating " + finalI + ".png");
                        });
                        
                    }
                }
                
                
                Platform.runLater(() -> {
                    progresstext.setFill(donepaint);
                    progresstext.setText("Generation done");
                    progressoutput.appendText("\n\nGeneration done");
                });
                
                
                continuebutton.setDisable(false);
                exitbutton.setDisable(false);
                dirbutton.setDisable(false);
                
                
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();
        
        
        
        
    }
    
    private InputStream getFileStream(final String file){
        return this.getClass().getClassLoader().getResourceAsStream(file);
    }
}
