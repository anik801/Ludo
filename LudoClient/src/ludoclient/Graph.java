/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludoclient;

/**
 *
 * @author John
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.System.exit;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static javax.swing.JOptionPane.showMessageDialog;

public class Graph extends JFrame {
    JPanel jp;
    BufferedReader serverReader;
    PrintStream serverWriter;
    int diceValue;
    
    int[] gutiStatus1 = new int[4];
    int[] gutiStatus2 = new int[4];
    int[] gutiPoint1 = new int[4];
    int[] gutiPoint2 = new int[4];
    int winPoint = 55;
    
    int[] gutiRow1 = new int[4];
    int[] gutiCol1 = new int[4];
    int[] gutiRow2 = new int[4];
    int[] gutiCol2 = new int[4];
    
    public Graph() {
        super("Ludo");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        jp = new GPanel();
        add(jp);
        setVisible(true);
        
        
        //new MyThread();
    }
    
    public Graph(BufferedReader serverReader, PrintStream serverWriter) {
        super("Ludo");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        jp = new GPanel();
        add(jp);
        setVisible(true);
        
        
        this.serverReader = serverReader;
        this.serverWriter = serverWriter;
        
        new ServerReaderThread();
    }
    

    public static void main(String[] args) {
        Graph g1 = new Graph();
        //g1.setVisible(true);
    }

    class GPanel extends JPanel {
        int size =  15;
        int boxSize = 50;
        
        public GPanel() {
            setPreferredSize(new Dimension(300, 300));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            drawGrid(g);
            for(int i=0;i<4;i++){
                drawPieces(g,1,i,gutiRow1[i],gutiCol1[i]);
            }
            for(int i=0;i<4;i++){
                drawPieces(g,2,i,gutiRow2[i],gutiCol2[i]);
            }
        }
        
        public void drawGrid(Graphics g){
            
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    g.setColor(Color.black);
                    g.drawRect(row*boxSize, col*boxSize, boxSize, boxSize);
                                        
                    if(row<6 && col<6){
                        g.setColor(Color.green);
                        g.fillRect(row*boxSize, col*boxSize, boxSize, boxSize);
                        //cellPane[row][col].setBackground(Color.green);
                    }else if(row>8 && col<6){
                        g.setColor(Color.red);
                        g.fillRect(row*boxSize, col*boxSize, boxSize, boxSize);
                        //cellPane[row][col].setBackground(Color.green);
                    }else if(row<6 && col>8){
                        g.setColor(Color.blue);
                        g.fillRect(row*boxSize, col*boxSize, boxSize, boxSize);
                        //cellPane[row][col].setBackground(Color.green);
                    }else if(row>8 && col>8){
                        g.setColor(Color.MAGENTA);
                        g.fillRect(row*boxSize, col*boxSize, boxSize, boxSize);
                        //cellPane[row][col].setBackground(Color.green);
                    }else if(row>=6 && row<=8 && col>=6 && col<=8){
                        g.setColor(Color.orange);
                        g.fillRect(row*boxSize, col*boxSize, boxSize, boxSize);
                        //cellPane[row][col].setBackground(Color.red);
                    }
                    
                    
                }
            }
        }
        
        public void drawPieces(Graphics g,int playerNum, int pieceNum, int x, int y){
            try {
                BufferedImage piece = ImageIO.read(new File("images/p"+playerNum+""+pieceNum+".png"));
                g.drawImage(piece, y*boxSize, x*boxSize, this);
                
            } catch (IOException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        
    }
    
    public class ServerReaderThread extends Thread{
        public ServerReaderThread(){
            start();
        }
        
        public void run(){
            receiveAllValues();
            repaint();
            
            while(true){
                try{
                    String command = serverReader.readLine();
                    if(command.equals("Wait")){
                        
                    }else if(command.equals("Your Turn")){
                        receiveAllValues();
                        repaint();
            
                        do{
                            new DiceFrame(serverWriter);

                            String str = serverReader.readLine();
                            diceValue =  Integer.parseInt(str);
                            System.out.println(diceValue);
                            
                            new GutiChoiceFrame(serverWriter, diceValue);

                            command = serverReader.readLine();
                            if(command.equals("You Won")){
                                showMessageDialog(null, "You Won!");
                                //this.destroy();
                                exit(0);
                            }else if(command.equals("You Lost")){
                                showMessageDialog(null, "You Lost!");
                                //this.destroy();
                                exit(0);
                            }else if(command.equals("running")){
                                receiveAllValues();
                                repaint();
                            }
                        }while(diceValue==6);
                        
                    }
                
                    repaint();
                    sleep(30);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
        
        public void receiveAllValues(){
            
            String str;
            try {
                str = serverReader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            for (int i = 0; i < 4; i++) {
                try {                    
                    
                    str = serverReader.readLine();
                    int status = Integer.parseInt(str);
                    
                    str = serverReader.readLine();
                    int x = Integer.parseInt(str);
                    
                    str = serverReader.readLine();
                    int y = Integer.parseInt(str);
                    System.out.println(status+"\t"+x+"\t"+y);
                    
                    gutiStatus1[i]=status;
                    gutiRow1[i]=x;
                    gutiCol1[i]=y;
                    
                } catch (IOException ex) {
                    Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            
            for (int i = 0; i < 4; i++) {
                try {                    
                    
                    str = serverReader.readLine();
                    int status = Integer.parseInt(str);
                    
                    str = serverReader.readLine();
                    int x = Integer.parseInt(str);
                    
                    str = serverReader.readLine();
                    int y = Integer.parseInt(str);
                    
                    gutiStatus2[i]=status;
                    gutiRow2[i]=x;
                    gutiCol2[i]=y;
                    
                } catch (IOException ex) {
                    Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
    }

}