package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Simulator {

    public static void main(String[] args) {
        Node node1 = new Node(1001);
        node1.start();

        Node node2 = new Node(1002);
        node2.start();


//
//
//        Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Socket socket1 = new Socket("127.0.0.1",6001 );
//                    InputStream inputStream = socket1.getInputStream();
//                    ObjectOutputStream out = new ObjectOutputStream(socket1.getOutputStream());
//
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ServerSocket socket = new ServerSocket(6000);
//                    Socket client = socket.accept();
//                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
//                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());
//
//
//                    thread2.start();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });




//
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        t1.run();
//


//        Node node3 = new Node(1003);
//        node3.start();
//
//        Node node4 = new Node(1004);
//        node4.start();
//
//        Node node5 = new Node(1005);
//        node5.start();
//        Node node6 = new Node(1006);
//        node6.start();


    }
}
