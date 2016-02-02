/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John
 */
public class LudoClient {

    /**
     * @param args the command line arguments
     */
    int port = 5050;
    String address = "localhost";
    BufferedReader serverReader;
    PrintStream serverWriter;
    
    public LudoClient(){
        initClient();
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        new LudoClient();
    }
    
    
    
    public void initClient(){
        try {
            Socket sock = new Socket(address,port);
            serverReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            serverWriter = new PrintStream(sock.getOutputStream());
            
            new Graph(serverReader,serverWriter);
            
        } catch (IOException ex) {
            Logger.getLogger(LudoClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
        
    }
    
}
