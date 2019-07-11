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

import javax.swing.Timer;

@SuppressWarnings("serial")
public class Multiplayer extends JPanel implements MouseListener {
	public static double x = 0;
	public static double y = 0;
  public static double speedall;
	public static double showspeed = 0;
  public static double angle = 0;
	public static double gunangle = 0;

  public static int xint = 0;
  public static int yint = 0;

  public static double[][] cars = new double[10][7]; ///0 = x coordinate, 1 = y coordinate, 2 = speed, 3 = angle, 4 = gunangle, 5 = state of player, 6 = time of death
	public static double[][] bullets = new double[10][3];
	public static double[][] obstacles = new double[100][2];

	public static int id;

  public static double time;
  public static double lasttime = 0.0;
  public static double difference;

	public static double time2;
  public static double lasttime2 = 0.0;
  public static double difference2;

	public static double kills = 0;
	public static double deaths = 0;

  public static int[] fpsdata = new int[100];
  int fps=0;
	long ping=0;
	long pingtime;
	long pingprint;

	public static boolean collision = false;

	public static double[] sendData = new double[6];

  public static int s_width;
  public static int s_height;
	public static double s_diag;

	public static String ip = "localhost";

	public static String outputstring;
	public static volatile boolean connected = false;

	private static BufferedImage background;

	public static boolean fire = false;
	public static double firetime = 0;

	public static double maxspeed = 140;

  public Multiplayer(){

    addMouseListener(this);
  }


  ///vsi teli morjo bit kle
  ///mouse listeners
  public void mouseClicked(MouseEvent e){

		if(System.currentTimeMillis() - firetime > 5000){

					fire = true;
					firetime = System.currentTimeMillis();

		}

	}

	public void mousePressed(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}


  ///end listeners


	@Override
	public void paint(Graphics g) {

		double useangle  = angle;

		///calculate difference between frames
		time2 = System.currentTimeMillis();
		difference2 = time2 - lasttime2;
		lasttime2 = time2;

		x = cars[id][0];
		y = cars[id][1];

		xint = (int) Math.round(x);
		yint = (int) Math.round(y);

		//more smooth transition of speed
		if(showspeed + 2 > speedall && showspeed - 2 < speedall){
			showspeed = showspeed;
		}else if(showspeed < speedall){
			showspeed = showspeed + (speedall - showspeed)/35;
		}else{
			showspeed = showspeed - (showspeed - speedall)/35;
		}

		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setFont(new Font("Georgia", Font.PLAIN, 12));



		///color screen white if collision accures
		if(cars[id][5]!=1 || collision){
			g2d.setColor(new Color(255, 255, 255));
			g2d.fillRect(0, 0, s_width, s_height);

			g2d.setColor(new Color(0, 0, 0));

			g2d.fill(new Arc2D.Float(s_width/2 - 100, s_height/2 - 100, 200, 200, 180, -(float)((System.currentTimeMillis() - cars[id][6])*360/5000), Arc2D.PIE));
			g2d.setColor(new Color(255, 255, 255));
			g2d.fill(new Ellipse2D.Double(s_width/2 - 50, s_height/2 - 50, 100, 100));

			g2d.setColor(new Color(0, 0, 0));
			g2d.drawString("Respawning", s_width/2 - 35, s_height/2 + 6);

		}else{

			//background
	    g2d.setColor(new Color(18, 155, 66));
	    g2d.fillRect(0, 0, s_width, s_height);

	    //stuff
	    g2d.rotate(Math.toRadians(-useangle), s_width/2, s_height/2);
			g2d.drawImage(background,-1500 - xint + s_width/2, -1500 + yint +s_height/2, 3000, 3000, null);
			g2d.setColor(new Color(0, 0, 0));

			//render other cars

	    for(int i=0; i<cars.length; i++){


				if(i!=id && cars[i][5] == 1){

		      int cx = (int) Math.round(cars[i][0]);
		      int cy = (int) Math.round(cars[i][1]);

					double carang = cars[i][3];
					double cargunang = cars[i][4];


		      g2d.rotate(Math.toRadians(carang), cx - xint + s_width/2, -cy + yint + s_height/2);
					g2d.setColor(new Color (0, 70, 0));
			    g2d.fillRoundRect(cx -15 - xint + s_width/2, -cy -25 + yint + s_height/2, 30, 50, 7, 7);
					g2d.setColor(new Color (0, 38, 10));
					g2d.fill(new Ellipse2D.Double(cx -12 - xint + s_width/2, -cy -12 + yint + s_height/2, 24, 24));
					g2d.setStroke(new BasicStroke(5));
					g2d.draw(new Line2D.Double(cx - xint + s_width/2, -cy + yint + s_height/2, cx - xint + s_width/2 + Math.sin(Math.toRadians(cargunang - carang))*30, -cy + yint + s_height/2 - Math.cos(Math.toRadians(cargunang - carang))*30));

					g2d.rotate(Math.toRadians(-carang), cx - xint + s_width/2, -cy + yint + s_height/2);

				}

	    }


	    ///stop renedring other cars


			//render bullets

			for(int i=0; i<bullets.length; i++){

				int bx = (int) Math.round(bullets[i][0]);
				int by = (int) Math.round(bullets[i][1]);

				if(bx != 0 || by != 0){

					g2d.setColor(new Color(0, 0, 0));
					g2d.fill(new Ellipse2D.Double(bx - xint + s_width/2 - 3, -by -3 + yint + s_height/2, 6, 6));

				}

			}

			///render obstacles
			g2d.setColor(new Color(140, 54, 0));
			for(int i=0; i<obstacles.length; i++){

				int ox = (int) Math.round(obstacles[i][0]);
				int oy = (int) Math.round(obstacles[i][1]);

				if(Math.abs(xint - ox) < s_diag/2 + 40 && Math.abs(yint - oy) < s_diag/2 + 40){

					g2d.fill(new Ellipse2D.Double(ox - xint + s_width/2 - 40, -oy -40 + yint + s_height/2, 80, 80));

				}
			}


	    //render player
	    g2d.rotate(Math.toRadians(useangle), s_width/2, s_height/2);
	    g2d.setColor(new Color (0, 70, 0));
	    g2d.fillRoundRect(s_width/2 - 15, s_height/2 - 25, 30, 50, 7, 7);

			g2d.setColor(new Color (0, 38, 10));
			g2d.fill(new Ellipse2D.Double(s_width/2 - 12, s_height/2 - 12, 24, 24));
			g2d.setStroke(new BasicStroke(5));
			g2d.draw(new Line2D.Double(s_width/2, s_height/2, s_width/2 + Math.sin(Math.toRadians(gunangle))*30, s_height/2 - Math.cos(Math.toRadians(gunangle))*30));


			///render speedometer
			Shape speedometer = new Arc2D.Float(30, s_height - 130, 200, 200, 0, 180, Arc2D.CHORD);
			Shape spcolor1 = new Arc2D.Float(30, s_height - 130, 200, 200, 180, -120, Arc2D.PIE);
			Shape spcolor2 = new Arc2D.Float(30, s_height - 130, 200, 200, 60, -30, Arc2D.PIE);
			Shape spcolor3 = new Arc2D.Float(30, s_height - 130, 200, 200, 30, -30, Arc2D.PIE);
			Shape spcolor4 = new Arc2D.Float(80, s_height - 80, 100, 100, 0, 180, Arc2D.PIE);
			Shape spcolor5 = new Arc2D.Float(120, s_height - 40, 20, 20, 0, 360, Arc2D.PIE);


			g2d.setColor(new Color(58, 163, 2));
			g2d.fill(spcolor1);
			g2d.setColor(new Color(188, 204, 14));
			g2d.fill(spcolor2);
			g2d.setColor(new Color(204, 96, 14));
			g2d.fill(spcolor3);
			g2d.setColor(new Color(255, 255, 255));
			g2d.fill(spcolor4);
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(new Color(0, 0, 0));
			g2d.draw(speedometer);
			g2d.setColor(new Color(184, 4, 4));
			g2d.setStroke(new BasicStroke(5));

			//draw speedometer pointer
			g2d.draw(new Line2D.Double(130, s_height - 30, 130 + Math.sin(-Math.toRadians(90 + 180 * (showspeed/maxspeed))) * 80, s_height - 30 + Math.cos(-Math.toRadians(90 + 180 * (showspeed/maxspeed)))*80));
			g2d.setColor(new Color(0, 0, 0));
			g2d.fill(spcolor5);


			//draw reload time
			g2d.setColor(new Color(8, 85, 209));
			if(System.currentTimeMillis() - firetime < 5000){

				g2d.fillRoundRect(s_width - 100, s_height/2 -50 - (int)((System.currentTimeMillis() - firetime) * 0.04), 30, (int)((System.currentTimeMillis() - firetime) * 0.04), 10, 10);

			}else{

				g2d.fillRoundRect(s_width - 100, s_height/2 - 250, 30, 200, 10, 10);


			}

			//draw reload time outline
			g2d.setColor(new Color(0, 0, 0));
			g2d.setStroke(new BasicStroke(2));
			g2d.drawRoundRect(s_width - 100, s_height/2 - 250, 30, 200, 10, 10);


			///minimap
			g2d.drawRect(s_width - 300, s_height - 300, 300, 300);
			g2d.drawImage(background, s_width - 300, s_height - 300, 300, 300, null);

			//put cars on minimap
	    for(int i=0; i<cars.length; i++){

				if(cars[i][5]==1 && cars[i][0] > -1500 && cars[i][1] < 1500){


		      int cx = (int) Math.round(cars[i][0]);
		      int cy = (int) Math.round(cars[i][1]);
					g2d.setColor(new Color (255, 0, 0));

					if(i==id){

						g2d.setColor(new Color (0, 0, 255));

					}

					g2d.fill(new Ellipse2D.Double(s_width + cx/10 - 152, s_height -152 - cy/10, 4, 4));

				}

	    }

		}


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

      if(difference2 == 0){
        difference2=1;
      }

      for (int i=0; i<100; i++) {
          if(fpsdata[i]==0){
            fpsdata[i]=(int) difference2;
            break;
          }

      }
    }

    if(fps > 0 && fps!=1000){

      g2d.drawString("FPS: "+fps, s_width - 100, 15);

			//this is here, so the ping doesnt get updated so frequently
			g2d.drawString("Ping: " + pingprint + "ms", s_width - 100, 30);


    }

		///render message
		g2d.drawString(outputstring, 10, 15);

		//show kills and deaths
		g2d.setColor(new Color(255, 255, 255));
		g2d.fillRoundRect(10, 44, 100, 58, 7, 7);
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(new Color(0, 0, 0));
		g2d.drawRoundRect(10, 44, 100, 58, 7, 7);
		g.setFont(new Font("Georgia", Font.PLAIN, 17));
		g2d.drawString((int)kills + " kills", 20, 70);
		g2d.drawString((int)deaths + " deaths", 20, 92);


		repaint();

	}


	public static void main(String[] args) throws InterruptedException {


				/////////start graphics

				System.setProperty("sun.java2d.opengl", "true");

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

					Multiplayer.ip = JOptionPane.showInputDialog(null, "Server's IP:", "Enter server's IP", JOptionPane.PLAIN_MESSAGE);

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

							if(key == 'w' && Multiplayer.speedall < maxspeed - 12){

			          Multiplayer.speedall = Multiplayer.speedall + 10;

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

				s_diag = Math.sqrt(Math.pow(s_width, 2)+Math.pow(s_height, 2));

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


							//send all player's data
							Multiplayer.sendData[0] = Multiplayer.id;
							Multiplayer.sendData[1] = Multiplayer.angle;
							Multiplayer.sendData[2] = Multiplayer.speedall;
							Multiplayer.sendData[3] = Multiplayer.gunangle + Multiplayer.angle;


							//send data if player wants to fire a bullet
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

							Multiplayer.kills = recData[2][Multiplayer.id][0];
							Multiplayer.deaths = recData[2][Multiplayer.id][1];
							Multiplayer.obstacles = recData[3];

							Multiplayer.bullets = recData[1];

							for(int i=0; i<Multiplayer.cars.length; i++){

								Multiplayer.cars[i][4] = recData[0][i][4];
								Multiplayer.cars[i][3] = recData[0][i][3];
								Multiplayer.cars[i][2] = recData[0][i][2];

								//only change position if it is not within 15 pixels
								if((Math.abs(recData[0][i][0] - Multiplayer.cars[i][0])>15 || Math.abs(recData[0][i][1] - Multiplayer.cars[i][1])>15) && Multiplayer.cars[i][5] == 1){

											Multiplayer.cars[i][0] = recData[0][i][0];
											Multiplayer.cars[i][1] = recData[0][i][1];

								}

								Multiplayer.cars[i][5] = recData[0][i][5];
								Multiplayer.cars[i][6] = recData[0][i][6];

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


		///calculate difference between calculations
		time = System.currentTimeMillis();
		difference = time - lasttime;
		lasttime = time;


		//predict positions of other cars and check for collisions

		for (int i = 0; i<cars.length; i++){

			cars[i][0] = cars[i][0] + Math.sin(Math.toRadians(cars[i][3]))*cars[i][2]*(difference)/1000;
			cars[i][1] = cars[i][1] + Math.cos(Math.toRadians(cars[i][3]))*cars[i][2]*(difference)/1000;

			x=cars[id][0];
			y=cars[id][1];

			if(i != id){

				if(cars[i][5] == 1){

					if(Calc.checkCollision(x, y, angle, cars[i][0], cars[i][1], cars[i][3])==true){

						speedall = 0;
						collision = true;
						cars[id][5] = 0;

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

      }

    }


		////get angle and gun angle if connected

		if(connected && !collision && cars[id][5] == 1){

			Point p = MouseInfo.getPointerInfo().getLocation();
			double mx = p.getX();
			double my = p.getY();

			angle = angle +((mx - s_width/2)/s_width)*difference*9/100;

			cars[id][3] = angle;

			if(my - s_height/2 < 0){

				gunangle = - Math.toDegrees(Math.atan((mx - s_width/2)/(my - s_height/2)));

			}else{

				gunangle = 180 - Math.toDegrees(Math.atan((mx - s_width/2)/(my - s_height/2)));

			}


		}

	}

}


class GameLoop extends Thread implements Runnable{

	public void run(){

		while (true){

			if(Multiplayer.connected){
				Multiplayer.calculate();
			}
			try{
				Thread.sleep(10);
			}catch (Exception e){}

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
