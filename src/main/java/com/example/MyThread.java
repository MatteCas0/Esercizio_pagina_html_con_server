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
            String version = request[2];
            int contentLenght = 0;

            System.out.println("sono entrato nel thread");

            switch (method) {
                case "GET":
                    System.out.println("sono nel get");
                    pathVerify(path);
                    contentLenght = contentLenghtFinder(in);

                    File file = new File("htdocs" + path);

                    if(!path.endsWith("/")){
                        System.out.println("path finisce con /");
                        out.println("HTTP/1.1 301 Moved Permanently");
                        out.println("Content-Length: " + file.length() + "");
                        out.println("Content-Type: " + getContentType(file) + "");
                        out.println("location: " + file + "");
                        out.println("");
                    }
            
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

                    break;
                case "POST":
                    pathVerify(path);
                    contentLenght = contentLenghtFinder(in);
                    break;
                case "HEAD":
                    pathVerify(path);
                    contentLenght = contentLenghtFinder(in);
                    break;
                case "PUT":
                case "PATCH":
                case "DELETE":
                case "OPTIONS":
                case "CONNECT":
                case "TRACE":
                    out.println("HTTP/1.1 405 Method not allowed");
                    out.println(path);
                    out.flush();
                    break;

                default:
                    out.println("HTTP/1.1 400 Bad Request");
                    out.println(path);
                    out.flush();
                    break;
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
    
    private static String readBody(BufferedReader in, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] buf = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int n = in.read(buf, read, contentLength - read);
            if (n == -1) {
                break;
            }
            read += n;
        }
        return new String(buf, 0, read);
    }

    private static boolean pathVerify(String path) {
        if(path.endsWith("/")){
            path += "index.html";
        }

        File file = new File("htdocs" + path);

        if(file.exists()) return true;
        
        return false;
    }

    private int contentLenghtFinder(BufferedReader in) throws IOException{
        boolean flag = false;
        int cl = 0;

        while (!flag) {
            String str = in.readLine();

            if(str.startsWith("content-length:")){
                String[] cls = str.split(" ", 2);
                cl = Integer.parseInt(cls[1]);
            }


            if(str == "") flag = true;
        }

        return cl;
    }
}
/*
        
*/