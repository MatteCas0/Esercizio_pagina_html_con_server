package com.example;

import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        try (ServerSocket ss = new ServerSocket(8080)) {
            do {
                Socket s = ss.accept();
                System.out.println("ciccio");

                MyThread thread = new MyThread(s);
                thread.start();
            } while (true);
        }
    }
}