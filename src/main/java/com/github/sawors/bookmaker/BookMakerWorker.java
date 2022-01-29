package com.github.sawors.bookmaker;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BookMakerWorker {
    
    
    
    public static void createBook(String bookprefix, String bookname, File target, Integer pagenumber, Button continuebutton, Button exitbutton, Button dirbutton, ProgressBar progressbar, TextArea progressoutput, Text progresstext, boolean doimages){
    
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
    
            int totaltasks = 2+(pagenumber*2);
            if(doimages){
                totaltasks = 3+(pagenumber*3);
            }
            final int[] donetasks = {0};
            
            //StringBuilder outputtext = new StringBuilder("\"Starting the creation of book \\\"\" + bookname + \"\\\" with \" + pagenumber + \" pages...\"");
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
                if(doimages){
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
                    if(doimages){
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
    
    public static void createBookFromPDF(boolean onlytextures,String bookprefix, String bookname, File source, File target, int from, int to, double scale, Color tolerance,  Button continuebutton, Button exitbutton, Button dirbutton, ProgressBar progressbar, TextArea progressoutput, Text progresstext){
    
        Paint basepaint = Paint.valueOf("#666666");
        Paint donepaint = Paint.valueOf("#008000");
        ArrayList<BufferedImage> singleimagelist = new ArrayList<>();
        final int horizontalsize = (int) (800*scale);
        final int verticalsize = (int) (1.5*horizontalsize);
        PDDocument test = new PDDocument();
        try{
            test = PDDocument.load(source);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        PDFRenderer pr = new PDFRenderer (test);
        if(from < 0){
            from = 1;
        }
        if(to < 0){
            to = test.getNumberOfPages();
        }
    
        final int finalfrom = from;
        final int finalto = to;
        final int singlepagenumber = test.getNumberOfPages();
        final int doublepagenumber = test.getNumberOfPages()/2;
        String modelpath = target.getPath()+"/assets/minecraft/models/item/books/"+bookname+"/";
        String citpath = target.getPath()+"/assets/minecraft/optifine/cit/books/"+bookname+"/";
        String texturespath = target.getPath()+"/assets/minecraft/textures/item/books/"+bookname+"/";
        BookMakerWorker instance = new BookMakerWorker();
        int totaltasks = 3 // _cover.cit + _cover.properties + _cover.png
                        +doublepagenumber*2 // doublepages n.cit + n.properties
                        +(finalto+1-finalfrom)*2 // singlepage render + singlepage formatting
                        +(finalto+1-finalfrom)/2 // composing double pages
                        +(finalto+1-finalfrom)/2 // formatting double pages
                        +(finalto+1-finalfrom)/2 // writing double pages
                            ;
        final int[] donetasks = {0};
        //FILES
    
        exitbutton.setDisable(false);
        continuebutton.setDisable(true);
        dirbutton.setDisable(true);
    
        progresstext.setFill(basepaint);
    
        
    
        System.out.println('\n'+ "Starting the creation of book \"" + bookname + "\" with " + doublepagenumber + "double pages...");
        //progressoutput.setText();
        
        new File(modelpath).mkdirs();
        new File(citpath).mkdirs();
        new File(texturespath).mkdirs();
    
        progresstext.setText("creating directories...");
    
        new Thread(() -> {
            
            
        
            //StringBuilder outputtext = new StringBuilder("\"Starting the creation of book \\\"\" + bookname + \"\\\" with \" + pagenumber + \" pages...\"");
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
                        progressbar.setProgress((float) donetasks[0] / totaltasks);
                        Platform.runLater(() -> {
                            progresstext.setText("creating cover template texture...");
                            progressoutput.appendText("\ncreating cover template texture...");
                        });
                        
                    }
            
                for (int i = 1; i <= doublepagenumber; i++) {
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
                }
    
                
                //  STARTING IMAGES RENDERS
                
                //      SINGLE IMAGE RENDERS
                for (int i = (finalfrom - 1); i < finalto; i++) {
                    int finalI = i;
                    System.out.println(i);
                    BufferedImage img0 = null;
    
                    donetasks[0]++;
                    Platform.runLater(() -> {
                        progresstext.setText("rendering page "+ finalI);
                        progressoutput.appendText("\nrendering page "+ finalI);
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                    
                    try {
                        img0 = pr.renderImageWithDPI(i, 300);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("rendered : " + (i + 1));
                    double ratio = ((double) img0.getHeight() / img0.getWidth());
                    System.out.println(ratio);
        
                    int vcrop = 1;
                    int hcrop = 1;
        
                    if (ratio > 1.5) {
                        System.out.println("to big");
            
                        vcrop = (int) ((img0.getHeight() - (img0.getWidth() * 1.5)) / 2);
                        hcrop = 0;
            
                    } else if (ratio < 1.5 && ratio > 0) {
                        System.out.println("too high");
            
                        vcrop = 0;
                        hcrop = (int) ((img0.getWidth() - (img0.getHeight() * (2f / 3))) / 2);
            
                    } else if (ratio == 1.5) {
                        System.out.println("just right");
            
                        vcrop = 0;
                        hcrop = 0;
            
                    }
                    System.out.println("Step 0");
                    BufferedImage img = img0.getSubimage(hcrop, vcrop, img0.getWidth() - hcrop, img0.getHeight() - vcrop);
                    BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    System.out.println("Step 1");
                    Graphics2D g = copyOfImage.createGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                    System.out.println("Step 2");
    
    
    
                    donetasks[0]++;
                    Platform.runLater(() -> {
                        progresstext.setText("formatting page "+ finalI);
                        progressoutput.appendText("\nformatting page "+ finalI);
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                    
                    ImageFilter filter = new RGBImageFilter() {
                        public int filterRGB(int x, int y, int rgb) {
                            //rgb >= tolerance.getRGB()
                            if (rgb >= tolerance.getRGB()) {
                                return 0xffffffff;
                            } else {
                                return rgb;
                            }
                        }
                    };
                    System.out.println("Step 3");
                    final ImageProducer imp = new FilteredImageSource(copyOfImage.getSource(), filter);
                    Image tempimg = Toolkit.getDefaultToolkit().createImage(imp);
                    BufferedImage buffimg = new BufferedImage(copyOfImage.getWidth(), copyOfImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D imgtobuff = buffimg.createGraphics();
                    imgtobuff.drawImage(tempimg, 0, 0, null);
                    imgtobuff.dispose();
                    AffineTransform at = new AffineTransform();
                    at.scale((float) horizontalsize / buffimg.getWidth(), (float) verticalsize / buffimg.getHeight());
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage outputimg = new BufferedImage(horizontalsize, verticalsize, BufferedImage.TYPE_INT_ARGB);
                    atop.filter(buffimg, outputimg);
                    singleimagelist.add(outputimg);
        
                }
                System.out.println("Step 6");
                int count = 1;
                
                //      COMPOSING DOUBLE PAGES
                for (int i = 0; i < singleimagelist.size(); i += 2) {
                    final int finalI = i;
                    donetasks[0]++;
                    Platform.runLater(() -> {
                        progresstext.setText("composing double page "+ finalI);
                        progressoutput.appendText("\ncomposing double page "+ finalI);
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                    BufferedImage imgleft = singleimagelist.get(i);
                    BufferedImage imgright;
                    if (singleimagelist.size() > i + 1) {
                        imgright = singleimagelist.get(i + 1);
                    } else {
                        imgright = new BufferedImage(horizontalsize, verticalsize, BufferedImage.TYPE_INT_ARGB);
                    }
                    System.out.println("Step 7");
                    BufferedImage finalimg = new BufferedImage(horizontalsize * 2, verticalsize, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D grph = finalimg.createGraphics();
                    grph.drawImage(imgleft, 0, 0, null);
                    grph.drawImage(imgright, horizontalsize, 0, null);
                    grph.dispose();
                    System.out.println("Step 8");
    
    
    
                    donetasks[0]++;
                    Platform.runLater(() -> {
                        progresstext.setText("formatting double page "+ finalI);
                        progressoutput.appendText("\nformatting double page "+ finalI);
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                    
                    int[] palette = { 0xffffffff, 0xffababab, 0xff808080, 0xff545454,0xff2b2b2b, 0xff000000};
                    IndexColorModel colorModel = new IndexColorModel(2,         // bits per pixel
                            6,         // size of color component array
                            palette,   // color map
                            0,         // offset in the map
                            true,      // has alpha
                            0,         // the pixel value that should be transparent
                            DataBuffer.TYPE_BYTE);
                    BufferedImage outputimg = new BufferedImage(finalimg.getWidth(), finalimg.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, colorModel);
                    Graphics2D imgtobuff = outputimg.createGraphics();
                    imgtobuff.drawImage(finalimg, 0, 0, null);
                    imgtobuff.dispose();
    
                    
                    donetasks[0]++;
                    Platform.runLater(() -> {
                        progresstext.setText("writing double page "+ finalI);
                        progressoutput.appendText("\nwriting double page "+ finalI);
                    });
                    progressbar.setProgress((float) donetasks[0] / totaltasks);
                    
                    try {
                        ImageIO.write(outputimg, "png", new File(texturespath + "\\" + count + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                    System.out.println("Step 9");
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
/*
    try{
        
        new Thread(() -> {
            for (int i = (finalfrom - 1); i < finalto; i++) {
                System.out.println(i);
                BufferedImage img0 = null;
                try {
                    img0 = pr.renderImageWithDPI(i, 300);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("rendered : " + (i + 1));
                double ratio = ((double) img0.getHeight() / img0.getWidth());
                System.out.println(ratio);
                
                int vcrop = 1;
                int hcrop = 1;
                
                if (ratio > 1.5) {
                    System.out.println("to big");
                    
                    vcrop = (int) ((img0.getHeight() - (img0.getWidth() * 1.5)) / 2);
                    hcrop = 0;
                    
                } else if (ratio < 1.5 && ratio > 0) {
                    System.out.println("too high");
                    
                    vcrop = 0;
                    hcrop = (int) ((img0.getWidth() - (img0.getHeight() * (2f / 3))) / 2);
                    
                } else if (ratio == 1.5) {
                    System.out.println("just right");
                    
                    vcrop = 0;
                    hcrop = 0;
                    
                }
                
                
                System.out.println("Step 0");
                BufferedImage img = img0.getSubimage(hcrop, vcrop, img0.getWidth() - hcrop, img0.getHeight() - vcrop);
                BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                System.out.println("Step 1");
                Graphics2D g = copyOfImage.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                System.out.println("Step 2");
                
                ImageFilter filter = new RGBImageFilter() {
                    public int filterRGB(int x, int y, int rgb) {
                        //rgb >= tolerance.getRGB()
                        if (rgb >= tolerance.getRGB()) {
                            return 0xffffffff;
                        } else {
                            return rgb;
                        }
                    }
                };
                System.out.println("Step 3");
                final ImageProducer imp = new FilteredImageSource(copyOfImage.getSource(), filter);
                Image tempimg = Toolkit.getDefaultToolkit().createImage(imp);
                BufferedImage buffimg = new BufferedImage(copyOfImage.getWidth(), copyOfImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D imgtobuff = buffimg.createGraphics();
                imgtobuff.drawImage(tempimg, 0, 0, null);
                imgtobuff.dispose();
                AffineTransform at = new AffineTransform();
                at.scale((float) horizontalsize / buffimg.getWidth(), (float) verticalsize / buffimg.getHeight());
                AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                BufferedImage outputimg = new BufferedImage(horizontalsize, verticalsize, BufferedImage.TYPE_INT_ARGB);
                atop.filter(buffimg, outputimg);
                *//*imgtobuff.scale(800f/copyOfImage.getWidth(), 1200f/copyOfImage.getHeight());
                imgtobuff.drawImage(tempimg,0,0,null);
                imgtobuff.dispose();
                System.out.println("Step 4");
                BufferedImage outputimg = buffimg.getSubimage(0,0,800,1200);
                Graphics g2 = outputimg.createGraphics();
                g2.drawImage(outputimg, 0,0,null);
                System.out.println("Step 5");*//*
                singleimagelist.add(outputimg);
                
            }
            System.out.println("Step 6");
            int count = 1;
            for (int i = 0; i < singleimagelist.size(); i += 2) {
                BufferedImage imgleft = singleimagelist.get(i);
                BufferedImage imgright;
                if (singleimagelist.size() > i + 1) {
                    imgright = singleimagelist.get(i + 1);
                } else {
                    imgright = new BufferedImage(horizontalsize, verticalsize, BufferedImage.TYPE_INT_ARGB);
                }
                System.out.println("Step 7");
                BufferedImage finalimg = new BufferedImage(horizontalsize * 2, verticalsize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D grph = finalimg.createGraphics();
                grph.drawImage(imgleft, 0, 0, null);
                grph.drawImage(imgright, horizontalsize, 0, null);
                grph.dispose();
                System.out.println("Step 8");
                
                int[] palette = { 0xffffffff, 0xffababab, 0xff808080, 0xff545454,0xff2b2b2b, 0xff000000};
                IndexColorModel colorModel = new IndexColorModel(2,         // bits per pixel
                        6,         // size of color component array
                        palette,   // color map
                        0,         // offset in the map
                        true,      // has alpha
                        0,         // the pixel value that should be transparent
                        DataBuffer.TYPE_BYTE);
                //ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                BufferedImage outputimg = new BufferedImage(finalimg.getWidth(), finalimg.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, colorModel);
                Graphics2D imgtobuff = outputimg.createGraphics();
                imgtobuff.drawImage(finalimg, 0, 0, null);
                imgtobuff.dispose();
                //op.filter(finalimg, outputimg);
                        
                        *//*ImageFilter filter = new GrayFilter(true, 50);
                        ImageProducer producer = new FilteredImageSource(finalimg.getSource(), filter);
                        Image mage = Toolkit.getDefaultToolkit().createImage(producer);
                        IndexColorModel model = new IndexColorModel();
                        BufferedImage outputimg = new BufferedImage(finalimg.getWidth(), finalimg.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
                        Graphics2D imgtobuff = outputimg.createGraphics();
                        imgtobuff.drawImage(mage, 0, 0, null);
                        imgtobuff.dispose();*//*
                
                try {
                    ImageIO.write(outputimg, "png", new File(target.getPath() + "\\" + count + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                count++;
                System.out.println("Step 9");
            }
        }).start();
        
    } catch (IOException exception) {
        exception.printStackTrace();
    }
}*/
}
