/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John
 */
public class LudoServer {

    /**
     * @param args the command line arguments
     */
    int port = 5050;
    BufferedReader clientReader1, clientReader2;
    PrintStream clientWriter1, clientWriter2;
    int dice;
    String command, str;
    int[] gutiStatus1 = new int[4];
    int[] gutiStatus2 = new int[4];
    int[] gutiPoint1 = new int[4];
    int[] gutiPoint2 = new int[4];
    int winPoint = 55;

    int gutiNum;

    public static void main(String[] args) {
        // TODO code application logic here
        new LudoServer();
    }

    public LudoServer() {
        initServer();
    }

    public void initServer() {
        try {
            ServerSocket serverSock = new ServerSocket(port);
            System.out.println("Server Started");

            Socket clientSock1 = serverSock.accept();   //client 1
            System.out.println("Client 1 Connected");
            Socket clientSock2 = serverSock.accept();   //client 2
            System.out.println("Client 2 Connected");

            clientReader1 = new BufferedReader(new InputStreamReader(clientSock1.getInputStream()));
            clientReader2 = new BufferedReader(new InputStreamReader(clientSock2.getInputStream()));

            clientWriter1 = new PrintStream(clientSock1.getOutputStream());
            clientWriter2 = new PrintStream(clientSock2.getOutputStream());

            //initialize gutiStatus variables
            //0=dead;   1=alive;    2=complete
            for (int i = 0; i < 4; i++) {
                gutiStatus1[i] = 0;
                gutiStatus2[i] = 0;

                gutiPoint1[i] = -1;
                gutiPoint2[i] = -1;
            }

            runGame();
        } catch (IOException ex) {
            Logger.getLogger(LudoServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void runGame() {
        try {
            sendAllValues();
            
            while (true) {
                /////////////////////////////////////////////////////////////
                clientWriter1.println("Your Turn");
                clientWriter2.println("Wait");
                
                //checkWin1();
                sendAllValues();
                
                command = clientReader1.readLine();
                if (command.equals("rollDice")) {
                    do {
                        sendClient1DiceValue();
                        str = clientReader1.readLine();
                        gutiNum = Integer.parseInt(str);

                        if (gutiStatus1[gutiNum] == 0 && dice == 6) {
                            gutiPoint1[gutiNum] = 0;
                            gutiStatus1[gutiNum] = 1;
                        } else if (gutiStatus1[gutiNum] == 1) {
                            gutiPoint1[gutiNum] += dice;
                            checkCut12(gutiNum);
                            if (gutiPoint1[gutiNum] > winPoint) {
                                gutiStatus1[gutiNum] = 2;

                                
                            }
                            
                        }
                        
                        checkWin1();
                        sendAllValues();
                        
                        if(dice==6){
                            command = clientReader1.readLine();
                        }

                    } while (dice == 6);
                }
                /////////////////////////////////////////////////////////////
                
                
                
                /////////////////////////////////////////////////////////////
                clientWriter2.println("Your Turn");
                clientWriter1.println("Wait");
                
                //checkWin1();
                sendAllValues();
                command = clientReader2.readLine();
                if (command.equals("rollDice")) {
                    do {
                        sendClient2DiceValue();
                        str = clientReader2.readLine();
                        gutiNum = Integer.parseInt(str);

                        if (gutiStatus2[gutiNum] == 0 && dice == 6) {
                            gutiPoint2[gutiNum] = 0;
                            gutiStatus2[gutiNum] = 1;
                        } else if (gutiStatus2[gutiNum] == 1) {
                            gutiPoint2[gutiNum] += dice;
                            checkCut21(gutiNum);
                            if (gutiPoint2[gutiNum] > winPoint) {
                                gutiStatus1[gutiNum] = 2;
                            }
                            
                        }
                        
                        checkWin2();
                        sendAllValues();
                            
                        if(dice==6){
                            command = clientReader2.readLine();
                        }
                    } while (dice == 6);
                }
                /////////////////////////////////////////////////////////////

            }
        } catch (IOException ex) {
            Logger.getLogger(LudoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkWin1() {
        boolean flag = true;
        for (int i = 0; i < 4; i++) {
            if (gutiStatus1[i] != 2) {
                flag = false;
            }
        }
        if (flag == true) {
            clientWriter1.println("You Won");
            clientWriter2.println("You Lost");
        } else {
            clientWriter1.println("running");
            clientWriter2.println("running");
        }
    }

    public void checkWin2() {
        boolean flag = true;
        for (int i = 0; i < 4; i++) {
            if (gutiStatus2[i] != 2) {
                flag = false;
            }
        }
        if (flag == true) {
            clientWriter2.println("won");
            clientWriter1.println("lost");
        } else {
            clientWriter1.println("running");
            clientWriter2.println("running");
        }
    }

    public void sendAllValues() {
        clientWriter1.println("allValues");
        clientWriter2.println("allValues");

        for (int i = 0; i < 4; i++) {

            clientWriter1.println(gutiStatus1[i]);
            clientWriter2.println(gutiStatus1[i]);

            clientWriter1.println(getRowOf1(gutiPoint1[i]));
            clientWriter1.println(getColOf1(gutiPoint1[i]));
            clientWriter2.println(getRowOf1(gutiPoint1[i]));
            clientWriter2.println(getColOf1(gutiPoint1[i]));

        }

        for (int i = 0; i < 4; i++) {

            clientWriter1.println(gutiStatus2[i]);
            clientWriter2.println(gutiStatus2[i]);

            clientWriter1.println(getRowOf2(gutiPoint2[i]));
            clientWriter1.println(getColOf2(gutiPoint2[i]));
            clientWriter2.println(getRowOf2(gutiPoint2[i]));
            clientWriter2.println(getColOf2(gutiPoint2[i]));

        }
    }

    public void checkCut12(int guti) {
        int point = gutiPoint1[guti];
        int gutiX = getRowOf1(point);
        int gutiY = getColOf1(point);

        for (int i = 0; i < 4; i++) {
            int x2 = getRowOf2(gutiPoint2[i]);
            int y2 = getColOf2(gutiPoint2[i]);

            if (gutiX == x2 && gutiY == y2) {
                gutiPoint2[i] = -1;
                gutiStatus2[i] = 0;
            }
        }
    }

    public void checkCut21(int guti) {
        int point = gutiPoint1[guti];
        int gutiX = getRowOf2(point);
        int gutiY = getColOf2(point);

        for (int i = 0; i < 4; i++) {
            int x1 = getRowOf1(gutiPoint2[i]);
            int y1 = getColOf1(gutiPoint2[i]);

            if (gutiX == x1 && gutiY == y1) {
                gutiPoint1[i] = -1;
                gutiStatus1[i] = 0;
            }
        }
    }

    public void sendClient1DiceValue() {
        dice = randInt(1, 6);
        System.out.println("Client 1 diceValue: " + dice);
        clientWriter1.println(dice);
    }

    public void sendClient2DiceValue() {
        dice = randInt(1, 6);
        System.out.println("Client 2 diceValue: " + dice);
        clientWriter2.println(dice);
    }

    public int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public int getRowOf1(int num) {
        int res = 0;
        switch (num) {
            case -1:
                res = 14;
                break;
            case 0:
                res = 13;
                break;

            case 1:
                res = 12;
                break;
            case 2:
                res = 11;
                break;
            case 3:
                res = 10;
                break;
            case 4:
                res = 9;
                break;
            //======================
            case 5:
                res = 8;
                break;
            case 6:
                res = 8;
                break;
            case 7:
                res = 8;
                break;
            case 8:
                res = 8;
                break;

            case 9:
                res = 8;
                break;
            case 10:
                res = 8;
                break;
            case 11:
                res = 7;
                break;

            case 12:
                res = 6;
                break;
            case 13:
                res = 6;
                break;
            //////////==================================

            case 14:
                res = 6;
                break;

            case 15:
                res = 6;
                break;

            case 16:
                res = 6;
                break;

            case 17:
                res = 6;
                break;
            //=================================
            case 18:  //
                res = 5;
                break;

            case 19:
                res = 4;
                break;

            case 20:
                res = 3;
                break;

            case 21:
                res = 2;
                break;

            case 22:
                res = 1;
                break;

            case 23:
                res = 0;
                break;

            case 24:
                res = 0;
                break;

            case 25:
                res = 0;
                break;

            case 26:
                res = 1;
                break;

            case 27:
                res = 2;
                break;

            case 28:
                res = 3;
                break;

            case 29:
                res = 4;
                break;

            case 30:
                res = 5;
                break;

            case 31:
                res = 6;
                break;

            case 32:
                res = 6;
                break;

            case 33:
                res = 6;
                break;

            case 34:
                res = 6;
                break;

            case 35:
                res = 6;
                break;

            case 36:
                res = 6;
                break;

            case 37:
                res = 7;
                break;

            case 38:
                res = 8;
                break;

            case 39:
                res = 8;
                break;

            case 40:
                res = 8;
                break;

            case 41:
                res = 8;
                break;

            case 42:
                res = 8;
                break;

            case 43:
                res = 8;
                break;

            ////=============
            case 44:
                res = 9;
                break;

            case 45:
                res = 10;
                break;

            case 46:
                res = 11;
                break;

            case 47:
                res = 12;
                break;

            case 48:
                res = 13;
                break;

            case 49:
                res = 14;
                break;

            case 50:
                res = 14;
                break;
            //====================

            case 51:
                res = 13;
                break;

            case 52:
                res = 12;
                break;

            case 53:
                res = 11;
                break;

            case 54:
                res = 10;
                break;

            case 55:
                res = 9;
                break;

        }
        return res;
    }

    public int getColOf1(int num) {
        int res = 0;
        switch (num) {
            case -1:
                res = 1;
                break;
            case 0:  //13
                res = 6;
                break;

            case 1:   //12
                res = 6;
                break;
            case 2:   //11
                res = 6;
                break;
            case 3:   //10
                res = 6;
                break;
            case 4:   //9
                res = 6;
                break;
            //====================
            case 5:
                res = 5; //8
                break;

            case 6:
                res = 4; //8
                break;

            case 7:
                res = 3; //8
                break;

            case 8:
                res = 2;  //8
                break;

            case 9:
                res = 1;  //8
                break;

            case 10:
                res = 0;   //8
                break;

            case 11:
                res = 0;   //7
                break;

            case 12:
                res = 0;   //6
                break;

            case 13:
                res = 1;   //6
                break;
            ////====================================
            case 14:
                res = 2;   //6
                break;

            case 15:
                res = 3;   //6
                break;

            case 16:
                res = 4;   //6
                break;

            case 17:
                res = 5;   //6
                break;

            //=========================
            //======================
            case 18:
                res = 6;   //5
                break;

            case 19:
                res = 6;   //4
                break;

            case 20:
                res = 6;   //3
                break;

            case 21:
                res = 6;   //2
                break;

            case 22:
                res = 6;   //1
                break;

            case 23:
                res = 6;   //0
                break;
            //============================

            case 24:
                res = 7;   //0
                break;

            case 25:
                res = 8;   //0
                break;

            case 26:
                res = 8;   //1
                break;

            case 27:
                res = 8;   //2
                break;

            case 28:
                res = 8;   //3
                break;

            case 29:
                res = 8;   //4
                break;

            case 30:
                res = 8;   //5
                break;

            case 31:
                res = 9;   //6
                break;

            case 32:
                res = 10;   //6
                break;

            case 33:
                res = 11;   //6
                break;

            case 34:
                res = 12;   //6
                break;

            case 35:
                res = 13;   //6
                break;

            case 36:
                res = 14;   //6
                break;

            case 37:
                res = 14;   //7
                break;

            case 38:
                res = 14;   //8
                break;

            case 39:
                res = 13;   //8
                break;

            case 40:
                res = 12;   //8
                break;

            case 41:
                res = 11;   //8
                break;

            case 42:
                res = 10;   //8
                break;

            case 43:
                res = 9;   //8
                break;
            //==============================
            case 44:
                res = 8;   //9
                break;

            case 45:
                res = 8;   //10
                break;

            case 46:
                res = 8;   //11
                break;

            case 47:
                res = 8;   //12
                break;

            case 48:
                res = 8;   //13
                break;

            case 49:
                res = 8;   //14
                break;

            //======================
            case 50:
                res = 7;   //14
                break;

            case 51:
                res = 7;   //13
                break;

            case 52:
                res = 7;   //12
                break;

            case 53:
                res = 7;   //11
                break;

            case 54:
                res = 7;   //10
                break;

            case 55:
                res = 7;   //9
                break;

        }
        return res;
    }

    public int getRowOf2(int num) {
        int res = 0;
        switch (num) {
            case -1:
                res = 1;
                break;
            case 0:
                res = 1;
                break;

            case 1:
                res = 2;
                break;
            case 2:
                res = 3;
                break;
            case 3:
                res = 4;
                break;
            case 4:
                res = 5;
                break;
            //======================
            case 5:
                res = 6;
                break;
            case 6:
                res = 6;
                break;
            case 7:
                res = 6;
                break;
            case 8:
                res = 6;
                break;

            case 9:
                res = 6;
                break;
            case 10:
                res = 6;
                break;
            case 11:
                res = 7;
                break;

            case 12:
                res = 8;
                break;
            case 13:
                res = 8;
                break;
            //////////==================================

            case 14:
                res = 8;
                break;

            case 15:
                res = 8;
                break;

            case 16:
                res = 8;
                break;

            case 17:
                res = 8;
                break;
            //=================================
            case 18:  //
                res = 9;
                break;

            case 19:
                res = 10;
                break;

            case 20:
                res = 11;
                break;

            case 21:
                res = 12;
                break;

            case 22:
                res = 13;
                break;

            case 23:
                res = 14;
                break;

            case 24:
                res = 14;
                break;

            case 25:
                res = 14;
                break;

            case 26:
                res = 13;
                break;

            case 27:
                res = 12;
                break;

            case 28:
                res = 11;
                break;

            case 29:
                res = 10;
                break;

            case 30:
                res = 9;
                break;

            case 31:
                res = 8;
                break;

            case 32:
                res = 8;
                break;

            case 33:
                res = 8;
                break;

            case 34:
                res = 8;
                break;

            case 35:
                res = 8;
                break;

            case 36:
                res = 8;
                break;

            case 37:
                res = 7;
                break;

            case 38:
                res = 6;
                break;

            case 39:
                res = 6;
                break;

            case 40:
                res = 6;
                break;

            case 41:
                res = 6;
                break;

            case 42:
                res = 6;
                break;

            case 43:
                res = 6;
                break;

            ////=============
            case 44:
                res = 5;
                break;

            case 45:
                res = 4;
                break;

            case 46:
                res = 3;
                break;

            case 47:
                res = 2;
                break;

            case 48:
                res = 1;
                break;

            case 49:
                res = 0;
                break;

            case 50:
                res = 0;
                break;
            //====================

            case 51:
                res = 1;
                break;

            case 52:
                res = 2;
                break;

            case 53:
                res = 3;
                break;

            case 54:
                res = 4;
                break;

            case 55:
                res = 5;
                break;

        }
        return res;
    }

    public int getColOf2(int num) {
        int res = 0;
        switch (num) {
            case -1:
                res = 14;
                break;

            case 0:   //1
                res = 8;
                break;

            case 1:   // 2
                res = 8;
                break;
            case 2:   //3
                res = 8;
                break;
            case 3:
                res = 8;
                break;
            case 4:
                res = 8;
                break;
            //====================
            case 5:
                res = 9;
                break;

            case 6:
                res = 10;
                break;

            case 7:
                res = 11;
                break;

            case 8:
                res = 12;
                break;

            case 9:
                res = 13;
                break;

            case 10:
                res = 14;
                break;

            case 11:
                res = 14;
                break;

            case 12:
                res = 14;
                break;

            case 13:
                res = 13;
                break;
            ////====================================
            case 14:
                res = 12;
                break;

            case 15:
                res = 11;
                break;

            case 16:
                res = 10;
                break;

            case 17:
                res = 9;
                break;

            //=========================
            //======================
            case 18:
                res = 8;
                break;

            case 19:
                res = 8;
                break;

            case 20:
                res = 8;
                break;

            case 21:
                res = 8;
                break;

            case 22:
                res = 8;
                break;

            case 23:
                res = 8;
                break;
            //============================

            case 24:
                res = 7;
                break;

            case 25:
                res = 6;
                break;

            case 26:
                res = 6;
                break;

            case 27:
                res = 6;
                break;

            case 28:
                res = 6;
                break;

            case 29:
                res = 6;
                break;

            case 30:
                res = 6;
                break;
            //=====================================
            case 31:
                res = 5;
                break;

            case 32:
                res = 4;
                break;

            case 33:
                res = 3;
                break;

            case 34:
                res = 2;
                break;

            case 35:
                res = 1;
                break;

            case 36:
                res = 0;
                break;

            case 37:
                res = 0;
                break;

            case 38:
                res = 0;
                break;

            case 39:
                res = 1;
                break;

            case 40:
                res = 2;
                break;

            case 41:
                res = 3;
                break;

            case 42:
                res = 4;
                break;

            case 43:
                res = 5;
                break;
            //==============================
            case 44:
                res = 6;
                break;

            case 45:
                res = 6;
                break;

            case 46:
                res = 6;
                break;

            case 47:
                res = 6;
                break;

            case 48:
                res = 6;
                break;

            case 49:
                res = 6;
                break;

            //======================
            case 50:
                res = 7;
                break;

            case 51:
                res = 7;
                break;

            case 52:
                res = 7;
                break;

            case 53:
                res = 7;
                break;

            case 54:
                res = 7;
                break;

            case 55:
                res = 7;
                break;

        }
        return res;
    }

}
