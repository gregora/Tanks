////programmed by Gregor A.
////you can contact me by mail gregor372@gmail.com

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.net.*;
import java.io.*;

import java.awt.geom.*;

@SuppressWarnings("serial")
public class Server{

  public static double[][] cars = new double[10][7];
  ///0 = x coordinate, 1 = y coordinate, 2 = speed, 3 = angle, 4 = gunangle, 5 = state of player, 6 = time of death
  public static double[][] bullets = new double[10][3];
  //0 = x, 1 = y, 2 = angle
  public static double[][] kills = new double[10][2];
  //0 = kills, 1 = deaths

  public static double maxspeed = 140;

  public static double time;
  public static double lasttime = 0.0;
  public static double difference = 0;

  public static int i;

  public static Vector ips = new Vector();


	public static void main(String[] args) throws InterruptedException {

      //bot
      double[] car1 = {-50, -100, 20, 0, 0, 1, 0};
      Server.cars[0]=car1;

      ////////start networking

      try {

      ServerSocket ss=new ServerSocket(6666);

      Calc cal = new Calc();
      Thread cthread = new Thread(cal);

      cthread.start();


      while (true) {

        Socket s = null;

        try {
            // socket object to receive incoming client requests
            s = ss.accept();

            //check if this ip is already connected
            if(checkIp(s.getInetAddress().toString())){

              System.out.println("A new client is connected : " + s.getInetAddress());

              // obtaining input and out streams
              ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
              ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
              DataOutputStream dos=new DataOutputStream(s.getOutputStream());

              // create a new thread object for the client
              NewClient nc = new NewClient(s, oos, ois, dos);

              Thread t = new Thread(nc);

              // Invoking the start() method
              t.start();

            }else{

              System.out.println("A socket from " + s.getInetAddress() + " tried to connect, but was blocked");
              s.close();

            }



        }catch (Exception e){
            s.close();
            e.printStackTrace();
        }

  		}

    }catch (Exception e){
      System.out.println(e);

    }



////end networking








	}


  //function to check if incoming ip is already connected
  public static boolean checkIp(String ip){

    Iterator i = ips.iterator();

    if(i.hasNext()){

      while(i.hasNext()){

        String checkthis = (String) i.next();

        if(checkthis.equals(ip)){

          return false;

        }

      }

    }

    ips.add(ip);
    return true;

  }

  ///remove ip if player disconnects
  public static void removeIp(String ip){

        Iterator i = ips.iterator();
        int index = 0;

        while(i.hasNext()){

          String checkthis = (String) i.next();

          if(checkthis.equals(ip)){

            ips.remove(index);

          }

          index =+ 1;

        }



  }


}





class Calc extends Thread implements Runnable {


  public void run(){


    while (true){

      Server.time = System.currentTimeMillis();

      Server.difference = Server.time - Server.lasttime;


      if(Server.difference > 300){

        Server.difference=0;

      }


      for (int i = 0; i<Server.cars.length; i++){

        if(Server.cars[i][5] == 1){

          for(int i2 = 0; i2<Server.cars.length; i2++){

            if(i2 != i){

              if(Server.cars[i2][5] == 1){

                if(checkCollision(Server.cars[i][0], Server.cars[i][1], Server.cars[i][3], Server.cars[i2][0], Server.cars[i2][1], Server.cars[i2][3])==true){

                  ///if collision  is detected, put cars randomly on the field

                  Server.cars[i][2] = 0;
                  Server.cars[i][0] = -1500 + Math.random()*3000;
                  Server.cars[i][1] = -1500 + Math.random()*3000;

                  Server.cars[i2][2] = 0;
                  Server.cars[i2][0] = -1500 + Math.random()*3000;
                  Server.cars[i2][1] = -1500 + Math.random()*3000;

                  //set state to dead
                  Server.cars[i][5] = 0;
                  Server.cars[i2][5] = 0;
                  Server.cars[i][6] = System.currentTimeMillis();
                  Server.cars[i2][6] = System.currentTimeMillis();

                  //add kills to the list
                  Server.kills[i][0] = Server.kills[i][0] + 1;
                  Server.kills[i2][0] = Server.kills[i2][0] + 1;

                  Server.kills[i][1] = Server.kills[i][1] + 1;
                  Server.kills[i2][1] = Server.kills[i2][1] + 1;


                }

              }

            }

          }

          Server.cars[i][0] = Server.cars[i][0] + Math.sin(Math.toRadians(Server.cars[i][3]))*Server.cars[i][2]*(Server.difference)/1000;
          Server.cars[i][1] = Server.cars[i][1] + Math.cos(Math.toRadians(Server.cars[i][3]))*Server.cars[i][2]*(Server.difference)/1000;

      }else{

        if(Server.cars[i][6] != 0){

          if(System.currentTimeMillis() - Server.cars[i][6]  > 5000){

            Server.cars[i][5] = 1;

          }

        }

      }

    }




    for (int i = 0; i<Server.bullets.length; i++){

      if(Server.bullets[i][0] != 0 || Server.bullets[i][1] != 0 || Server.bullets[i][2] != 0){

        Server.bullets[i][0] = Server.bullets[i][0] + Math.sin(Math.toRadians(Server.bullets[i][2]))*500*(Server.difference)/1000;
        Server.bullets[i][1] = Server.bullets[i][1] + Math.cos(Math.toRadians(Server.bullets[i][2]))*500*(Server.difference)/1000;

        for(int i2 = 0; i2<Server.cars.length; i2++){

          if(Server.cars[i2][5]==1 && i != i2){

            if(checkBulletCollision(Server.bullets[i][0], Server.bullets[i][1], Server.cars[i2][0], Server.cars[i2][1], Server.cars[i2][3]) == true){

              Server.cars[i2][2] = 0;
              Server.cars[i2][0] = -1500 + Math.random()*3000;
              Server.cars[i2][1] = -1500 + Math.random()*3000;

              //set state to dead
              Server.cars[i2][5] = 0;
              Server.cars[i2][6] = System.currentTimeMillis();

              Server.bullets[i][0] = 0;
              Server.bullets[i][1] = 0;
              Server.bullets[i][2] = 0;

              //add kills to the list
              Server.kills[i][0] = Server.kills[i][0] + 1;
              Server.kills[i2][1] = Server.kills[i2][1] + 1;


            }

          }

        }


      }

    }



      ///end actual calculations



      Server.lasttime = Server.time;

      try{

        Thread.sleep(10);

      }catch(Exception e){}

  }



  }




  public static boolean checkCollision(double x1, double y1, double a1, double x2, double y2, double a2){

    Rectangle2D rect1 = new Rectangle2D.Double(x1 - 15, y1 - 25, 30, 50);
    AffineTransform af = new AffineTransform();
    af.rotate(Math.toRadians(-a1), x1, y1);
    Shape rrect1 = af.createTransformedShape(rect1);


    Rectangle2D rect2 = new Rectangle2D.Double(x2 - 15, y2 - 25, 30, 50);
    AffineTransform bf = new AffineTransform();
    bf.rotate(Math.toRadians(-a2), x2, y2);
    Shape rrect2 = bf.createTransformedShape(rect2);

    Area areaA = new Area(rrect1);
    Area areaB = new Area(rrect2);

    areaA.intersect(areaB);

    if(areaA.isEmpty() == false){

      return true;

    }
    return false;


  }

  public static boolean checkBulletCollision(double x1, double y1, double x2, double y2, double a){


    Rectangle2D rect1 = new Rectangle2D.Double(x2 - 15, y2 - 25, 30, 50);
    AffineTransform af = new AffineTransform();
    af.rotate(Math.toRadians(-a), x2, y2);
    Shape rrect1 = af.createTransformedShape(rect1);

    Shape circle = new Ellipse2D.Double(x1 - 3, y1 - 3, 6, 6);

    Area areaA = new Area(rrect1);
    Area areaB = new Area(circle);

    areaA.intersect(areaB);

    if(areaA.isEmpty() == false){

      return true;

    }
    return false;

  }


}





//////each client has a new class


class NewClient extends Thread implements Runnable {

  public static int i;

  final Socket s;
  final ObjectOutputStream oos;
  final ObjectInputStream ois;
  final DataOutputStream dos;

  int id;

  public NewClient(Socket s, ObjectOutputStream oos, ObjectInputStream ois, DataOutputStream dos){
      this.s = s;
      this.oos = oos;
      this.ois = ois;
      this.dos = dos;
  }

  @Override
  public void run(){

    double fireTime = 0;

    try{

      for(int c=0; c<Server.cars.length; c++){

        if(Server.cars[c][5]!=1){
          dos.writeInt(c);
          id = c;

          double[] newcar = {-1500 + Math.random()*3000, -1500 + Math.random()*3000, 0, 0, 0, 1, 1};
          Server.cars[id] = newcar;

          break;
        }

      }


      while(true){

        try {

          double [] recData =  (double[])ois.readObject();



          if(recData[2] < Server.maxspeed && recData[2] >= 0){

            Server.cars[id][3] = recData[1];
            Server.cars[id][2] = recData[2];
            Server.cars[id][4] = recData[3];

          }



          if(recData[4] == 1 && fireTime + 5000 < System.currentTimeMillis()){

            fireTime = System.currentTimeMillis();

            Server.bullets[id][0] = Server.cars[id][0] + Math.sin(Math.toRadians(Server.cars[id][4]))*30;
            Server.bullets[id][1] = Server.cars[id][1] + Math.cos(Math.toRadians(Server.cars[id][4]))*30;
            Server.bullets[id][2] = Server.cars[id][4];

          }

          double [][][] sendData = {Server.cars, Server.bullets, Server.kills};



          oos.writeObject(sendData);
          oos.reset();


        }catch (Exception e){
          double[] change = {0, 0, 0, 0, 0, 0};
          Server.cars[id] = change;
          try{
            Thread.sleep(10000);
          }catch(Exception ex){}
          Server.removeIp(s.getInetAddress().toString());
          s.close();
          System.out.println(e);
          break;
        }

        Thread.sleep(10);

      }


    }catch(Exception e){
          double[] change = {0, 0, 0, 0, 0, 0};
          Server.cars[id] = change;
          try{
            Thread.sleep(10000);
          }catch(Exception ex){}
          Server.removeIp(s.getInetAddress().toString());
          try{
            s.close();
          }catch(Exception ex){}

          System.out.println(e);
    }

  }

}
