package com.github.sawors.bookmaker.controllers;

import com.github.sawors.bookmaker.BookMakerWorker;
import com.github.sawors.bookmaker.BookmakerApplication;
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
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FromPDFController {
    
    
    public AnchorPane layer_base;
    public Pane layer_generating;
    public ProgressBar progressbar;
    public Button buttonanotherbook;
    public Button buttonexit;
    public Button buttonopendir;
    public Text progresstext;
    public TextArea progressoutput;
    public TextField fieldbookname;
    public Button buttonbrowse;
    public TextField fieldtarget;
    public Label title;
    public TextField fieldbooknameprefix;
    public Button buttonpdfgenerate;
    public Button buttonbrowsesource;
    public TextField fieldsource;
    public CheckBox checkboxonlyselected;
    public TextField fieldfrom;
    public TextField fieldto;
    public Pane advancedpane;
    public CheckBox checkboxscale;
    public TextField fieldscale;
    public CheckBox advancedgen;
    public Text errortext;
    public CheckBox checkboxtolerance;
    public Slider slidertolerance;
    public TextField fieldtolerance;
    public CheckBox checkboxonlytextures;
    javafx.scene.paint.Paint warn = javafx.scene.paint.Paint.valueOf("#ffa500");
    javafx.scene.paint.Paint error = javafx.scene.paint.Paint.valueOf("#b22222");
    
    public void initialize(){
        advancedpane.setVisible(false);
        errortext.setText("");
        errortext.setFill(error);
        fieldbooknameprefix.setText("book_");
        fieldfrom.setDisable(true);
        fieldto.setDisable(true);
        fieldscale.setDisable(true);
        fieldtolerance.setDisable(true);
        fieldtolerance.setText("0.2");
    }
    
    public void gotomenu(ActionEvent actionEvent) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(BookmakerApplication.class.getResource("main-menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 400);
            Stage stage = (Stage) layer_base.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    
    public void startPDFGeneration(ActionEvent actionEvent) {
        errortext.setText("");
        errortext.setFill(error);
        if(fieldbookname.getText().length() < 1){
            errortext.setText("please input a book name");
            fieldbookname.requestFocus();
            return;
        }
        if(fieldtarget.getText().length() < 1){
            errortext.setText("please specify an output directory");
            fieldtarget.requestFocus();
            return;
        }
        if(fieldsource.getText().length() < 1){
            errortext.setText("please specify a valid pdf file");
            fieldsource.requestFocus();
            return;
        }
        String bookname = fieldbookname.getText();
        String prefix = fieldbooknameprefix.getText();
        File target = new File(fieldtarget.getText());
        File source = new File(fieldsource.getText());
        boolean advanced = advancedgen.isSelected();
        boolean doselection = checkboxonlyselected.isSelected();
        boolean doscale = checkboxscale.isSelected();
        boolean dotolerance = checkboxtolerance.isSelected();
        float tolerance = 0.3f;
        double scale = 1;
        int from = -1;
        int to = -1;
        
        if(!target.exists()){
            target.mkdirs();
        }
        if(!source.exists() || !source.getPath().endsWith(".pdf")){
            fieldsource.setText("");
            errortext.setText("please specify a valid pdf file (file has not been found)");
            fieldsource.requestFocus();
            return;
        }
        if(advanced){
            if(doselection){
                if(fieldfrom.getText().length() > 0){
                    try{
                        from = Integer.parseInt(fieldfrom.getText());
                    } catch (IllegalArgumentException exc){
                        fieldfrom.setText("");
                        fieldfrom.requestFocus();
                        errortext.setText("your FROM input is not a valid number (Integer)");
                        return;
                    }
                }
                if(fieldto.getText().length() > 0){
                    try{
                        to = Integer.parseInt(fieldto.getText());
                    } catch (IllegalArgumentException exc){
                        fieldto.setText("");
                        fieldto.requestFocus();
                        errortext.setText("your TO input is not a valid number (Integer)");
                        return;
                    }
                }
            }
            if(doscale){
                if(fieldscale.getText().length() > 0){
                    try{
                        scale = Double.parseDouble(fieldscale.getText());
                        if(scale <= 0){
                            fieldscale.setText("");
                            fieldscale.requestFocus();
                            errortext.setText("scale must be a positive non-null number");
                            return;
                        }
                    } catch (IllegalArgumentException exc){
                        fieldscale.setText("");
                        fieldscale.requestFocus();
                        errortext.setText("your SCALE input is not a valid number");
                        return;
                    }
                }
            }
            
            if(dotolerance){
                if(fieldtolerance.getText().length() > 0){
                    try{
                        tolerance = Float.parseFloat(fieldtolerance.getText());
                        if(tolerance > 1){
                            fieldtolerance.setText("");
                            fieldtolerance.requestFocus();
                            errortext.setText("tolerance must be between 0 and 1 ("+(int)tolerance+" is to big)");
                            return;
                        } if(tolerance < 0){
                            fieldtolerance.setText("");
                            fieldtolerance.requestFocus();
                            errortext.setText("tolerance must be between 0 and 1 ("+(int)tolerance+" is to small)");
                        }
                    } catch (IllegalArgumentException exc){
                        fieldscale.setText("");
                        fieldscale.requestFocus();
                        errortext.setText("your TOLERANCE input is not a valid number");
                        return;
                    }
                }
            }
        }
        
        Color tolerancecolor = Color.getHSBColor(0,0, 1-tolerance);
    
        System.out.println("Tolerance : "+tolerance);
        System.out.println("Color : "+tolerancecolor);
    
    
        layer_base.setDisable(true);
        layer_generating.setVisible(true);
        layer_generating.setDisable(false);
        BookMakerWorker.createBookFromPDF(checkboxonlytextures.isSelected(),prefix,bookname,source,target,from,to,scale, tolerancecolor,buttonanotherbook,buttonexit, buttonopendir, progressbar, progressoutput, progresstext);
        //generateFromPDF(source, target, from,to,scale,new Color(0xFFb3b3b3));
    }
    
    public void exitPress(ActionEvent actionEvent) {
        Platform.exit();
    }
    
    public void continuePress(ActionEvent actionEvent) {
        ((Stage) buttonanotherbook.getScene().getWindow()).close();
        try{
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(BookmakerApplication.class.getResource("from-pdf.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 400);
            stage.setTitle("Stones Bookmaker v0.1");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    
    public void openDirPress(ActionEvent actionEvent) {
        try{
            Desktop.getDesktop().open(new File(fieldtarget.getText()));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    
    public void nameinput(ActionEvent actionEvent) {
    }
    
    public void browseclick(ActionEvent actionEvent) {
        DirectoryChooser directorychooser = new DirectoryChooser();
        File basedir = new File(".").getAbsoluteFile();
        if(basedir.exists()) directorychooser.setInitialDirectory(basedir);
    
        File directory = directorychooser.showDialog(buttonbrowse.getScene().getWindow());
        if(directory != null){
            fieldtarget.setAlignment(Pos.CENTER_LEFT);
            fieldtarget.setText(directory.getPath());
        }
    }
    
    public void directoryinput(ActionEvent actionEvent) {
    }
    
    public void prefixinput(ActionEvent actionEvent) {
    }
    
    public void browsesourceclick(ActionEvent actionEvent) {
        FileChooser filechooser = new FileChooser();
        File basedir = new File(".").getAbsoluteFile();
        if(basedir.exists()) filechooser.setInitialDirectory(basedir);
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
    
        File file = filechooser.showOpenDialog(buttonbrowsesource.getScene().getWindow());
        if(file != null && file.exists()){
            fieldsource.setAlignment(Pos.CENTER_LEFT);
            fieldsource.setText(file.getPath());
        } else if(file != null && !file.exists()){
            errortext.setFill(error);
            errortext.setText("please provide a source PDF file");
        }
        
    }
    
    public void checkOnlySelection(ActionEvent actionEvent) {
        if(checkboxonlyselected.isSelected()){
            fieldfrom.setDisable(false);
            fieldto.setDisable(false);
        } else {
            fieldfrom.setDisable(true);
            fieldto.setDisable(true);
        }
    }
    
    public void checkScale(ActionEvent actionEvent) {
        fieldscale.setDisable(!checkboxscale.isSelected());
    }
    
    public void checkAdvancedGen(ActionEvent actionEvent) {
        advancedpane.setVisible(advancedgen.isSelected());
    }
    
    public void checkTolerance(ActionEvent actionEvent) {
        fieldtolerance.setDisable(!checkboxtolerance.isSelected());
    }
    
    
    
    
}





