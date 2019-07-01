////programmed by Gregor A.
////you can contact me by mail gregor372@gmail.com



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.geom.*;
import java.util.*;


import java.net.*;
import java.io.*;

@SuppressWarnings("serial")
public class Multiplayer extends JPanel implements MouseListener {
	public static double x = 0;
	public static double y = 0;
  public static double speedall;
  public static double angle = 0;
	public static double gunangle = 0;

  public static int xint = 0;
  public static int yint = 0;

  public static double[][] cars = new double[10][5];
	public static double[][] bullets = new double[10][3];

	public static int id;

  public static double time;
  public static double lasttime = 0.0;
  public static double difference;

  public static int[] fpsdata = new int[100];
  int fps=0;
	long ping=0;
	long pingtime;
	long pingprint;

	public static boolean collision = false;

	public static double[] sendData = new double[5];

  public static int s_width;
  public static int s_height;

	public static String ip = "localhost";

	public static String outputstring;
	public static volatile boolean connected = false;

	private static BufferedImage background;

	public static boolean fire = false;

  public Multiplayer(){

    addMouseListener(this);
  }


  ///vsi teli morjo bit kle
  ///mouse listeners
  public void mouseClicked(MouseEvent e){

		fire = true;

	}

	public void mousePressed(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}


  ///end listeners


	@Override
	public void paint(Graphics g) {

		double useangle  = angle;

		x = cars[id][0];
		y = cars[id][1];

		xint = (int) Math.round(x);
		yint = (int) Math.round(y);


		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //background
    g2d.setColor(new Color(18, 155, 66));
    g2d.fillRect(0, 0, s_width, s_height);

    //stuff
    g2d.rotate(Math.toRadians(-useangle), s_width/2, s_height/2);
		g2d.drawImage(background,-1500 - xint + s_width/2, -1500 + yint +s_height/2, 3000, 3000, null);
		g2d.setColor(new Color(0, 0, 0));

		//render other cars

    for(int i=0; i<cars.length; i++){


			if(i!=id && (cars[i][0]!=0 || cars[i][1] != 0)){

	      int cx = (int) Math.round(cars[i][0]);
	      int cy = (int) Math.round(cars[i][1]);

				double carang = cars[i][3];
				double cargunang = cars[i][4];


	      g2d.rotate(Math.toRadians(carang), cx - xint + s_width/2, -cy + yint + s_height/2);
				g2d.setColor(new Color (0, 70, 0));
		    g2d.fillRect(cx -15 - xint + s_width/2, -cy -35 + yint + s_height/2, 30, 70);
				g2d.setColor(new Color (0, 38, 10));
				g2d.fill(new Ellipse2D.Double(cx -15 - xint + s_width/2, -cy -15 + yint + s_height/2, 30, 30));
				g2d.setColor(new Color (0, 0, 0));
				g2d.setStroke(new BasicStroke(5));
				g2d.draw(new Line2D.Double(cx - xint + s_width/2, -cy + yint + s_height/2, cx - xint + s_width/2 + Math.sin(Math.toRadians(cargunang - carang))*20, -cy + yint + s_height/2 - Math.cos(Math.toRadians(cargunang - carang))*20));

				g2d.rotate(Math.toRadians(-carang), cx - xint + s_width/2, -cy + yint + s_height/2);

			}

    }


    ///stop renedring other cars


		//render bullets

		for(int i=0; i<bullets.length; i++){

			int bx = (int) Math.round(bullets[i][0]);
			int by = (int) Math.round(bullets[i][1]);

			if(bx != 0 || by != 0){

				g2d.fill(new Ellipse2D.Double(bx - xint + s_width/2 - 3, -by -3 + yint + s_height/2, 6, 6));

			}

		}


    //player
    g2d.rotate(Math.toRadians(useangle), s_width/2, s_height/2);
    g2d.setColor(new Color (0, 70, 0));
    g2d.fillRect(s_width/2 - 15, s_height/2 - 35, 30, 70);
		g2d.setColor(new Color (0, 38, 10));
		g2d.fill(new Ellipse2D.Double(s_width/2 - 15, s_height/2 - 15, 30, 30));
		g2d.setColor(new Color (0, 0, 0));
		g2d.setStroke(new BasicStroke(5));
		g2d.draw(new Line2D.Double(s_width/2, s_height/2, s_width/2 + Math.sin(Math.toRadians(gunangle))*20, s_height/2 - Math.cos(Math.toRadians(gunangle))*20));




    //fps

		g2d.setColor(new Color(0, 0, 0));

    if(fpsdata[99]!=0){

      for (int i=0; i<100; i++) {
          fps = fps + fpsdata[i];

      }

      fps = 100000/fps;

      fpsdata = new int[100];

			pingprint = ping;



    }else{

      if(difference == 0){
        difference=1;
      }

      for (int i=0; i<100; i++) {
          if(fpsdata[i]==0){
            fpsdata[i]=(int) difference;
            break;
          }

      }
    }

    if(fps > 0 && fps!=1000){

      g2d.drawString("FPS: "+fps, s_width - 200, 15);

			//this is here, so the ping doesnt get updated so frequently
			if(ping > 0){

				g2d.drawString("Ping: " + pingprint + "ms", s_width - 200, 30);

			}

    }





		g2d.drawString(outputstring, 10, 15);
		g2d.drawString("Speed: " + String.valueOf((int)(Multiplayer.speedall*(3.6/10)))+"km/h", 10, s_height - 20);

    repaint();


	}


	public static void main(String[] args) throws InterruptedException {


				/////////start graphics

		    JFrame frame = new JFrame("Game");


				Multiplayer Multiplayer = new Multiplayer();

				Object[] options = {"Singleplayer (Create server)", "Multiplayer", "Only create a server"};

				int x = JOptionPane.showOptionDialog(null, "What game mode would you like to play?", "Game mode", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

				if(x==0){

					Multiplayer.ip = "localhost";


					//run server on another thread
					RunServer rs = new RunServer();
					Thread trs = new Thread(rs);

					// Invoking the start() method
					trs.start();

				}else if(x==1){

					Multiplayer.ip = JOptionPane.showInputDialog(frame, "Server's IP:", "Enter server's IP", JOptionPane.PLAIN_MESSAGE);

					if(Multiplayer.ip==null){

						System.exit(0);

					}


				}else if(x==2){

					String[] arguments = new String[] {"123"};

					try{

						Server.main(arguments);

					}catch (Exception e){

						System.out.println(e);

					}

					System.exit(0);

				}else{

					System.exit(0);

				}

				frame.add(Multiplayer);

				try{

					background = ImageIO.read(Multiplayer.class.getResource("/img/background.png"));

				}catch(Exception e){

					System.out.println(e);

				}

		    //key listeners

		    frame.addKeyListener(new KeyListener(){
		      public void keyPressed(KeyEvent e) {
		        char key = e.getKeyChar();

						if(connected && !collision){

							if(key == 'w' && Multiplayer.speedall < 70){

			          Multiplayer.speedall = Multiplayer.speedall + 2;


			        }else if(key == 's'){

								if(Multiplayer.speedall>10){

			          	Multiplayer.speedall = Multiplayer.speedall - 10;

								}else{

									Multiplayer.speedall = 0;

								}

			        }

						}

						///in case ESC  is pressed
						if(e.getKeyCode() == 27){

							System.exit(0);

						}
		      }
		      public void keyReleased(KeyEvent e) {}
		      public void keyTyped(KeyEvent e) {}
		    });


				boolean developement = false; //if in developement set this to true (this will resize the window)

				if(developement){

					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    Multiplayer.s_width = (int) screenSize.getWidth() /2;
			    Multiplayer.s_height = (int) screenSize.getHeight() /2;

				}else{

					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					Multiplayer.s_width = (int) screenSize.getWidth();
					Multiplayer.s_height = (int) screenSize.getHeight();

					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

				}

				frame.setSize(Multiplayer.s_width, Multiplayer.s_height);
				frame.setResizable(false);
				frame.setUndecorated(true);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


				/////////end graphics


				///start the main game loop on another thread
				GameLoop gl = new GameLoop();
				Thread t = new Thread(gl);
				t.start(); // Invoking the start() method


		///start networking

		while (true){

				try{

					Multiplayer.outputstring = "Connecting to " + ip;

					Socket s=new Socket(ip, 6666);

					ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
					DataInputStream dis=new DataInputStream(s.getInputStream());
		      ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());

					Multiplayer.id = dis.readInt();

					Multiplayer.connected = true;
					Multiplayer.outputstring = "Successfully connected to " + ip;

					double[][][] recData = new double[2][10][5];

					Multiplayer.angle = 0;

					while (true) {

						try {


							Multiplayer.sendData[0] = Multiplayer.id;
							Multiplayer.sendData[1] = Multiplayer.angle;
							Multiplayer.sendData[2] = Multiplayer.speedall;
							Multiplayer.sendData[3] = Multiplayer.gunangle + Multiplayer.angle;

							if(Multiplayer.fire){
								Multiplayer.sendData[4] = 1;
								Multiplayer.fire = false;
							}else{
								Multiplayer.sendData[4] = 0;
							}

							Multiplayer.pingtime = System.currentTimeMillis(); //get time necessary to exchange info with server

							oos.writeObject(Multiplayer.sendData);
							oos.reset();

							recData = (double[][][])ois.readObject();

							Multiplayer.ping = System.currentTimeMillis() - Multiplayer.pingtime; //calculate ping


							Multiplayer.bullets = recData[1];

							for(int i=0; i<Multiplayer.cars.length; i++){

								Multiplayer.cars[i][4] = recData[0][i][4];
								Multiplayer.cars[i][3] = recData[0][i][3];
								Multiplayer.cars[i][2] = recData[0][i][2];

								Multiplayer.cars[i][0] = recData[0][i][0];
								Multiplayer.cars[i][1] = recData[0][i][1];

							}


						}catch ( EOFException e) {
							Multiplayer.outputstring = "Error: Remote connection closed";
							Multiplayer.connected = false;
							break;
			    	}


					}

				}catch(Exception e){
					Multiplayer.connected = false;
					Multiplayer.outputstring = "Error: Server refused connection";

				}

				Thread.sleep(1000);

		}


		///stop networking

	}



//////////improves frame rate by predicting position of other objects

public static void calculate(){


		if(angle >= 360 || angle <= -360){

			angle = 0;

		}


		///calculate difference between frames
		Multiplayer.time = System.currentTimeMillis();
		difference = time - lasttime;
		lasttime = time;


		//predict positions of other cars and check for collisions

		for (int i = 0; i<cars.length; i++){

			cars[i][0] = cars[i][0] + Math.sin(Math.toRadians(cars[i][3]))*cars[i][2]*(difference)/1000;
			cars[i][1] = cars[i][1] + Math.cos(Math.toRadians(cars[i][3]))*cars[i][2]*(difference)/1000;

			if(i != id){

				if(cars[i][0] != 0 || cars[i][1] != 0 ||  cars[i][2] != 0 || cars[i][3] != 0){

					if(Calc.checkCollision(x, y, angle, cars[i][0], cars[i][1], cars[i][3])==true){

						speedall = 0;
						collision = true;

					}else{

						collision = false;

					}

				}

			}

	}

		///predict bullet positions

		for (int i = 0; i<bullets.length; i++){

      if(bullets[i][0] != 0 || bullets[i][1] != 0 || bullets[i][2] != 0){

        bullets[i][0] = bullets[i][0] + Math.sin(Math.toRadians(bullets[i][2]))*500*(difference)/1000;
        bullets[i][1] = bullets[i][1] + Math.cos(Math.toRadians(bullets[i][2]))*500*(difference)/1000;


        if(Calc.checkBulletCollision(bullets[i][0], bullets[i][1], x, y, angle) == true){

					collision = true;

        }

      }

    }


		////get angle and gun angle if connected

		if(connected && !collision){

			Point p = MouseInfo.getPointerInfo().getLocation();
			double mx = p.getX();
			double my = p.getY();

			angle = angle +((mx - s_width/2)/s_width)*difference*3/100;


			cars[id][3] = angle;

			if(my - s_height/2 < 0){

				gunangle = - Math.atan((mx - s_width/2)/(my - s_height/2))*180/3.14;

			}else{

				gunangle = 180 - Math.atan((mx - s_width/2)/(my - s_height/2))*180/3.14;

			}


		}

	try{
		Thread.sleep(5); //limit fps
	}catch (Exception e){}

}


}


class GameLoop extends Thread implements Runnable{



	public void run(){

		while (true){

			if(Multiplayer.connected){
				Multiplayer.calculate();
			}

		}

	}


}



class RunServer extends Thread implements Runnable{

	public void run(){

		String[] arguments = new String[] {"123"};

		try{
			///run server
			Server.main(arguments);
		}catch (Exception e){
			System.out.println(e);
		}

	}

}
