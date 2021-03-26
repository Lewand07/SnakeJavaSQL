package pkg1stgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/**
 *
 * @author lewan
 */
public class SnakeGame extends javax.swing.JPanel implements ActionListener {
        
        private static int dif=1;
        private static int res = 500;
	static int UNIT_SIZE = getRes()/10;
	static int GAME_UNITS = (getRes()*getRes())/(UNIT_SIZE*UNIT_SIZE);
	static int DELAY = 200/getDif();
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6/getDif();
	int applesEaten=0;
        private int score;
        int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
        Connection conn;
        String log;
    
        public SnakeGame() {
        random = new Random();
        this.conn = Main.getConn();
        this.setPreferredSize(new Dimension(getRes(), getRes()));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
        
    public SnakeGame(String log) {
        random = new Random();
        this.log = log;
        this.conn = Main.getConn();
        this.setPreferredSize(new Dimension(getRes(), getRes()));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
        public void startGame(){
            newApple();
            running = true;
            timer = new Timer(DELAY,this);
            timer.start();
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            draw(g);
        }
        public void draw(Graphics g){
            if(running){
                for(int i=0;i<getRes()/UNIT_SIZE;i++){
                    g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE, getRes());
                    g.drawLine(0,i*UNIT_SIZE, getRes(),i*UNIT_SIZE);
                }
                g.setColor(Color.red);
                g.fillOval(appleX,appleY,UNIT_SIZE, UNIT_SIZE);

                for(int i=0; i<bodyParts;i++){
                    if(i==0){
                        g.setColor(Color.green);
                        g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
                }
                    else{
                        g.setColor(new Color(45,180,0));
                        g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
                    }
            }
            score=applesEaten*getDif();
            g.setColor(Color.white);
            g.setFont(new Font("Tahoma", Font.BOLD, getRes()/15));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score "+getScore(), (getRes() - metrics.stringWidth("Score "+getScore()))/2,getRes()-10);
           }
            else{
                setScore(score);
                gameOver(g);
            }
        }
        public void newApple(){
            appleX = random.nextInt((int)(getRes()/UNIT_SIZE))*UNIT_SIZE;
            appleY = random.nextInt((int)(getRes()/UNIT_SIZE))*UNIT_SIZE;
        }
        
        public void move(){
            for(int i=bodyParts;i>0;i--){
                x[i] = x[i-1];
                y[i] = y[i-1];
            }
            switch(direction){
                case'U':
                    y[0] = y[0] - UNIT_SIZE;
                    break;
                case'D':
                    y[0] = y[0] + UNIT_SIZE;
                    break;
                case'L':
                    x[0] = x[0] - UNIT_SIZE;
                    break;
                case'R':
                    x[0] = x[0] + UNIT_SIZE;
                    break;
            }
        }
        public void checkApple(){
            if((x[0]==appleX) && (y[0]==appleY)){
                bodyParts++;
                applesEaten++;
                newApple();
            }
        }
        public void checkCollisions(){
            for(int i=bodyParts;i>0;i--){
                if((x[0]==x[i])&&(y[0]==y[i])){
                    running = false;
            }
            if(x[0]<0){
                running = false;
            }
            if(x[0]> getRes()){
                running = false;
            }
            if(y[0]<0){
                running = false;
            } 
            if(y[0]>getRes()){
                running = false;
            }
            if(running==false){
                timer.stop();
            }
        }
        }
        public void gameOver(Graphics g){
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, getRes()/10));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over", (getRes() - metrics.stringWidth("Game Over"))/2,getRes()/2);
            }
        
        public void setScore(int score){
            try {
                String sql = "INSERT OR IGNORE INTO scoreboard(login, score, date) VALUES(?,?,CURRENT_DATE)";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setString(1,log);
                st.setInt(2, score);
                st.execute();
            } catch (SQLException ex) {
                Logger.getLogger(SnakeGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        @Override
        public void actionPerformed(ActionEvent e){
            if(running){
                move();
                checkApple();
                checkCollisions();
            }
            repaint();
        }
        public class MyKeyAdapter extends KeyAdapter{
            @Override
            public void keyPressed(KeyEvent e){
                switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
            }
        }

    /**
     * @return the dif
     */
    public static int getDif() {
        return dif;
    }

    /**
     * @param aDif the dif to set
     */
    public static void setDif(int aDif) {
        dif = aDif;
    }

    /**
     * @return the res
     */
    public static int getRes() {
        return res;
    }

    /**
     * @param aRes the res to set
     */
    public static void setRes(int aRes) {
        res = aRes;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }
    }

