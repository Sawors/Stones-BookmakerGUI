package com.github.sawors.bookmaker.controllers;

import com.github.sawors.bookmaker.BookMakerWorker;
import com.github.sawors.bookmaker.BookmakerApplication;
import com.github.sawors.bookmaker.UserData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BookmakerController {
    public TextField fieldbookname;
    public Button buttonbrowse;
    public TextField fieldtarget;
    public Button generatebutton;
    public HashMap<UserData, String> userdata = new HashMap<>();
    public Spinner<Integer> fieldnumber = new Spinner<>();
    public TextField fieldbooknameprefix;
    public Text errortext;
    public Pane layer_generating;
    public ProgressBar progressbar;
    public TextArea progressoutput;
    public Text progresstext;
    public Button buttonanotherbook;
    public Button buttonexit;
    public AnchorPane layer_base;
    public Label title;
    public Button buttonopendir;
    public Button fromPDF;
    public CheckBox createtextures;
    Paint warn = Paint.valueOf("#ffa500");
    Paint error = Paint.valueOf("#b22222");
    
    public void initialize(){
        
        fieldbookname.requestFocus();
        
        layer_generating.setDisable(true);
        layer_generating.setVisible(false);
        
        layer_base.setVisible(true);
        layer_base.setDisable(false);
        
        fieldnumber.setPromptText("Input your page number");
        fieldnumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4096, 1));
        
        fieldbooknameprefix.setText("book_");
        
        
        
        userdata.put(UserData.BOOK_NAME, "NOT-A-NAME");
        userdata.put(UserData.PAGE_NUMBER, fieldnumber.getValue().toString());
        userdata.put(UserData.PREFIX, fieldbooknameprefix.getText());
        userdata.put(UserData.TARGET_DIR, new File(".").getAbsoluteFile().toString());
    }
    
    public void generate(){
    
        userdata.put(UserData.BOOK_NAME, fieldbookname.getText());
        userdata.put(UserData.PAGE_NUMBER, fieldnumber.getValue().toString());
        userdata.put(UserData.PREFIX, fieldbooknameprefix.getText());
        userdata.put(UserData.TARGET_DIR, new File(fieldtarget.getText()).getPath());
        
        if(userdata.get(UserData.BOOK_NAME).length() < 1 || userdata.get(UserData.BOOK_NAME).contains("NOT-A-NAME")){
            if(fieldbookname.getText().length()>1){
                userdata.put(UserData.BOOK_NAME, fieldbookname.getText());
                errortext.setText("");
            } else {
                errortext.setFill(error);
                errortext.setText("error : name too short");
                return;
            }
        }
        if(Integer.parseInt(userdata.get(UserData.PAGE_NUMBER)) < 1){
            errortext.setFill(error);
            errortext.setText(errortext.getText()+"\nerror : you must have at least one page");
            return;
        }
        if(!new File(userdata.get(UserData.TARGET_DIR)).exists()){
            new File(userdata.get(UserData.TARGET_DIR)).mkdirs();
        }
    
        userdata.put(UserData.PAGE_NUMBER, fieldnumber.getValue().toString());
        
        // GENERATING BOOK
        
        layer_base.setDisable(true);
        layer_generating.setVisible(true);
        layer_generating.setDisable(false);
        
        System.out.println("Name : "+userdata.get(UserData.PREFIX)+userdata.get(UserData.BOOK_NAME)+"\nDirectory : "+userdata.get(UserData.TARGET_DIR)+"\nPages : "+userdata.get(UserData.PAGE_NUMBER));
        boolean dotextures = false;
        if(createtextures.isSelected()){
            dotextures = true;
        }
        BookMakerWorker.createBook(userdata.get(UserData.PREFIX), userdata.get(UserData.BOOK_NAME), new File(userdata.get(UserData.TARGET_DIR)), Integer.parseInt(userdata.get(UserData.PAGE_NUMBER)), buttonanotherbook, buttonexit, buttonopendir, progressbar, progressoutput, progresstext, dotextures);
    }
    
    public void nameinput(ActionEvent actionEvent) {
        if(fieldbookname.getText().length() >= 1){
            userdata.put(UserData.BOOK_NAME, fieldbookname.getText());
            generatebutton.setDisable(false);
        } else {
            errortext.setText(errortext.getText()+"\nBook name is too short");
        }
    }
    
    public void browseclick(ActionEvent actionEvent) {
        DirectoryChooser directorychooser = new DirectoryChooser();
        File basedir = new File(".").getAbsoluteFile();
        if(basedir.exists()) directorychooser.setInitialDirectory(basedir);
    
        String directory = directorychooser.showDialog(buttonbrowse.getScene().getWindow()).toString();
        userdata.put(UserData.TARGET_DIR, directory);
        fieldtarget.setText(userdata.get(UserData.TARGET_DIR));
        fieldtarget.setAlignment(Pos.CENTER_LEFT);
    
    }
    
    public void directoryinput(ActionEvent actionEvent) {
        if(new File(fieldtarget.getText()).exists()){
            userdata.put(UserData.TARGET_DIR, fieldtarget.getText());
        } else {
            new File(fieldtarget.getText()).mkdirs();
            errortext.setText("directory selected could not be found and has been created");
            errortext.setFill(warn);
            userdata.put(UserData.TARGET_DIR, fieldtarget.getText());
        }
    }
    
    public void prefixinput(ActionEvent actionEvent) {
        userdata.put(UserData.PREFIX, fieldbooknameprefix.getText());
    }
    
    public void continuePress(ActionEvent actionEvent) {
        ((Stage) buttonanotherbook.getScene().getWindow()).close();
        try{
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(BookmakerApplication.class.getResource("main-menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 400);
            stage.setTitle("Stones Bookmaker v1.0");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    
        
    }
    
    public void exitPress(ActionEvent actionEvent) {
        Platform.exit();
    }
    
    public void openDirPress(ActionEvent actionEvent) {
        try{
            Desktop.getDesktop().open(new File(userdata.get(UserData.TARGET_DIR)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    
    }
    
    public void testPress(ActionEvent actionEvent) {
    
    }
    
    public void gotoPDF(ActionEvent actionEvent) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(BookmakerApplication.class.getResource("from-pdf.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 400);
            Stage stage = (Stage) layer_base.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}