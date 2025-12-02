package com.example;

import java.io.*;
import java.net.*;

public class MyThread extends Thread{
    Socket s;
    BufferedReader in;
    PrintWriter out;
    DataOutputStream outBinary;

    public MyThread(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            outBinary = new DataOutputStream(s.getOutputStream());
            String[] request = in.readLine().split(" ", 3);
            String method = request[0];
            String path = request[1];

            //System.out.println(path);
            if(!method.equals("GET")){
                out.println("HTTP/1.1 405 Method not allowed");
                out.println(path);
                out.flush();
               
            }
            else{
                
                if(path.endsWith("/")){
                    path += "index.html";
                }

                File file = new File("htdocs" + path);

                //System.out.println(file.getAbsolutePath());
                if(file.exists()) {
                    
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Length: " + file.length() + "");
                    out.println("Content-Type: " + getContentType(file) + "");
                    out.println("");
                    InputStream input = new FileInputStream(file);
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = input.read(buf)) != -1) {
                        outBinary.write(buf, 0, n);
                    }
                    input.close();
                    
                }
                else  out.println("HTTP/1.1 404 not found");
            }
                
            System.out.println(path);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
        
    private static String getContentType(File f) {
        switch (f.getName().substring(f.getName().lastIndexOf("."))) {
            case "html":
                return "text/html";
            case "css":

                return "text/css";
            case "jpeg":
                return "image/jpeg";
        
            default:
                break;
        }
        return "";
    }

}
