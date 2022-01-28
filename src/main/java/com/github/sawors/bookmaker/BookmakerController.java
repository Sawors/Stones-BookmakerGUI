package com.github.sawors.bookmaker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.rendering.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        BookMakerWorker.createBook(userdata.get(UserData.PREFIX), userdata.get(UserData.BOOK_NAME), new File(userdata.get(UserData.TARGET_DIR)), Integer.parseInt(userdata.get(UserData.PAGE_NUMBER)), buttonanotherbook, buttonexit, buttonopendir, progressbar, progressoutput, progresstext);
    }
    
    public void nameinput(ActionEvent actionEvent) {
        if(fieldbookname.getText().length() >= 1){
            userdata.put(UserData.BOOK_NAME, fieldbookname.getText());
            generatebutton.setDisable(false);
        } else {
            errortext.setText(errortext.getText()+"\nBook name is too short");
            generatebutton.setDisable(true);
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
            stage.setTitle("Stones Bookmaker v0.1");
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
        ArrayList<BufferedImage> singleimagelist = new ArrayList<>();
        int horizontalsize = 800;
        int verticalsize = (int) (1.5*horizontalsize);
        File inputpdf = new File("C:\\Users\\sosol\\IdeaProjects\\bookmaker\\test\\LTDT.pdf");
        File output = new File("");
        
    try{
        PDDocument test = PDDocument.load(inputpdf);
        PDFRenderer pr = new PDFRenderer (test);
        
        for(int i=0; i<test.getNumberOfPages(); i++){
            System.out.println(i);
            BufferedImage img0 = pr.renderImageWithDPI(i, 300);
            System.out.println("rendered : "+(i+1));
            int cropedheight = (int) (img0.getWidth()*1.5);
            int padding = (img0.getHeight()-cropedheight)/2;
    
            BufferedImage img = img0.getSubimage(0, padding, img0.getWidth(), img0.getHeight()-padding); //fill in the corners of the desired crop location here
            BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    
            Graphics2D g = copyOfImage.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
    
            ImageFilter filter = new RGBImageFilter() {
                public int filterRGB(int x, int y, int rgb) {
                    if(rgb >= 0xFFb3b3b3){
                        return 0x00000000;
                    } else {
                        return rgb;
                    }
                }
            };
    
            final ImageProducer imp = new FilteredImageSource(copyOfImage.getSource(), filter);
            Image tempimg = Toolkit.getDefaultToolkit().createImage(imp);
            BufferedImage buffimg = new BufferedImage(copyOfImage.getWidth(), copyOfImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgtobuff = buffimg.createGraphics();
            imgtobuff.scale(800f/copyOfImage.getWidth(), 1200f/copyOfImage.getHeight());
            imgtobuff.drawImage(tempimg,0,0,null);
            imgtobuff.dispose();
    
            BufferedImage outputimg = buffimg.getSubimage(0,0,800,1200);
            Graphics g2 = outputimg.createGraphics();
            g2.drawImage(outputimg, 0,0,null);
    
            singleimagelist.add(outputimg);
            
        }
        
        int count = 1;
        for(int i=0; i<singleimagelist.size(); i+=2){
            BufferedImage imgleft = singleimagelist.get(i);
            BufferedImage imgright;
            if(singleimagelist.size() > i+1){
                imgright = singleimagelist.get(i+1);
            } else {
                imgright = new BufferedImage(800, 1200, BufferedImage.TYPE_INT_ARGB);
            }
            
            BufferedImage finalimg = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D grph = finalimg.createGraphics();
            grph.drawImage(imgleft, 0,0,null);
            grph.drawImage(imgright, 800,0,null);
            
            ImageIO.write(finalimg, "png", new File("C:\\Users\\sosol\\IdeaProjects\\bookmaker\\test\\"+count+".png"));
            count++;
        }
        
        Platform.exit();
    } catch (IOException exception) {
        exception.printStackTrace();
    }
    
    }
}