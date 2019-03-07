import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MasterMind implements Runnable{

	final int WIDTH = 500;
	final int HEIGHT = 1000;

	public static final int numberOfPins = 4;
	public static final int numberOfColors = 6;
	public static final int LIGHT_GRAY = 0;
	public static final int BLACK = 1;
	public static final int RED = 2;
	public static final int BLUE = 3;
	public static final int ORANGE = 4;
	public static final int GREEN = 5;


	public final int pinSize = 40;
	public final int pinSizeSpacing_X = 50;
	public final int pinSizeSpacing_Y = 50;
	public final int resultPinSize = 15;
	public final int resultPinSizeSpacing_X = 20;
	public final int resultPinSizeSpacing_Y = 20;
	
	public static final int MAXTURNS = 15;

	public boolean gameOver;
	public boolean gameCompleted;
	
	public Font font;

	public List<int[]> pinList = new ArrayList<int[]>();
	public List<int[]> pinListResult = new ArrayList<int[]>();
	public List<Integer> guess = new ArrayList<Integer>(4);

	int turn;

	public Color[] colors;

	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;

	public MasterMind(){
		frame = new JFrame("MasterMind");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		canvas.addMouseListener(new mouseControl());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setFocusable(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();

		gameOver = false;
		gameCompleted = false;
		
		turn = 1;

		pinListResult.add(new int[] {0,0});
	
		resetGuesses(true);
		
		generateRandomPins(pinList);

		colors = new Color[] {
				Color.LIGHT_GRAY,Color.BLACK,Color.RED,Color.BLUE,Color.ORANGE,Color.GREEN
		};

	}

	private void generateRandomPins(List<int[]> pinList) {

		pinList.add(new int[] {
				(int)(Math.random() * (0+(numberOfColors - 0) + 0)),
				(int)(Math.random() * (0+(numberOfColors - 0) + 0)),
				(int)(Math.random() * (0+(numberOfColors - 0) + 0)),
				(int)(Math.random() * (0+(numberOfColors - 0) + 0))
		});
	}

	private void startNextTurn() {

		if ((guess.get(0) == -1) ||  (guess.get(1) == -1) || (guess.get(2) == -1) || (guess.get(3) == -1))
		{
			JOptionPane.showMessageDialog(null, "Take a guess before submitting.","Error",
                    JOptionPane.ERROR_MESSAGE);
			return;
		}

		//calculate result
		pinList.add(new int[] {guess.get(0), guess.get(1), guess.get(2), guess.get(3)});
		calculatePinResult();


		if (!gameCompleted)
		{
			resetGuesses(false);		
			turn++;
		}
		if (turn > MAXTURNS){
			gameOver = true;
		}
	}

	private void resetGuesses(boolean newGame){

		if (newGame){
			for (int x = 0; x < numberOfPins; x++)
				guess.add(x,-1);
		}
		else{
			for (int x = 0; x < guess.size(); x++)
				guess.set(x,-1);
		}
	}		

	private void calculatePinResult() {
		int correct = 0;
		int rightColor = 0;

		boolean[] answerPinsTested = new boolean[4];
		boolean[] guessPinsTested = new boolean[4];  

		for (int x = 0; x < numberOfPins; x++){
			if (pinList.get(0)[x] == guess.get(x)){
				correct +=1; 
				answerPinsTested[x] = true;
				guessPinsTested[x] = true;
			}
		}
		//all four are correct
		if (correct == 4){
			gameCompleted = true;
			gameOver = true;
		}

		//testing guesses against sequence
		for (int answerPin = 0; answerPin < numberOfPins; answerPin++){
			if (answerPinsTested[answerPin] == false){

				for (int guessPin = 0; guessPin < numberOfPins; guessPin++){

					if (( pinList.get(0)[answerPin] == guess.get(guessPin)) &&
							(guessPinsTested[guessPin] == false)) {
						rightColor += 1;
						answerPinsTested[answerPin] = true;
						guessPinsTested[guessPin] = true;
						break;
					}
				} 
			}
		}
		pinListResult.add(new int[] {correct,rightColor});
	}

	private class mouseControl extends MouseAdapter{

		public boolean mouseLeftPressed, mouseRightPressed;


		public void mousePressed(MouseEvent e){
			switch(e.getButton()) {
			case MouseEvent.BUTTON1:
				mouseLeftPressed = true;
				break;
			case MouseEvent.BUTTON3:
				mouseRightPressed = true;
				break;
			}
			super.mouseClicked(e);
			Point p = e.getPoint();
			mouseClick(mouseLeftPressed, mouseRightPressed, p);
		}

		public void mouseReleased(MouseEvent e){
			switch(e.getButton()) {
			case MouseEvent.BUTTON1:
				mouseLeftPressed = false;
				break;
			case MouseEvent.BUTTON2:
				mouseRightPressed = false;
				break;
			}
		}
	}

	public void mouseClick (boolean mouseLeftPressed, boolean mouseRightPressed, Point p) {
		//submit and new game button
		if ( (p.x > WIDTH/2-30) && (p.x < WIDTH/2+30) && (p.y > HEIGHT-60) && (p.y < HEIGHT-40)){
			if (gameOver){
				startNewGame();
			}
			else {
				startNextTurn();
			}
		}
		//changing colors on pins
		for (int j = 0; j < numberOfPins; j++){

			if (inDistance(p, 50 + pinSizeSpacing_X*j, 50+pinSizeSpacing_Y*turn, pinSize)){
				if (mouseLeftPressed)
					guess.set(j,guess.get(j) +1);
				else if (mouseRightPressed)
					guess.set(j,guess.get(j) -1);
				if (guess.get(j) == numberOfColors)
					guess.set(j,0);
				else if (guess.get(j) < 0)
					guess.set(j,5);
			}
		}
	}


	private void startNewGame() {
		turn = 1;
		
		pinList.clear();
		generateRandomPins(pinList);
		
		pinListResult.clear();
		pinListResult.add(new int[] {0,0});

		guess.clear();
		resetGuesses(true);
		
		gameCompleted = false;
		gameOver = false;
		
	}

	public boolean inDistance(Point p, int x, int y, int diameter){


		final float x_d = p.x - (x + pinSize/2);
		final float y_d = p.y - (y + pinSize/2);
		
		return diameter/2 > Math.sqrt(x_d * x_d + y_d * y_d);
	}

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;

	boolean running = true;

	public void run(){

		long beginLoopTime;
		long endLoopTime;
		long deltaLoop;

		while(running){
			beginLoopTime = System.nanoTime();

			render();
			
			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;


			if(deltaLoop > desiredDeltaLoop){
				//Do nothing. We are already late.
			}else{
				try{
					Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
				}catch(InterruptedException e){
					//Do nothing
				}
			}
		}
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}

	protected void render(Graphics2D g){

		Color rect = new Color(255, 255, 255, 125);
		font = new Font("Arial", Font.BOLD, 15);
		g.setColor(Color.BLACK);
		g.setFont(font);

		g.drawString("Turn: " + Integer.toString(turn), WIDTH - 60, 15);

		//DRAW PINS

		for (int i = 0; i < turn; i++){
			for (int j = 0; j < numberOfPins; j++){
				if (i == 0 && !gameOver){
					font = new Font("Arial", Font.BOLD, 25);
					g.setColor(Color.GRAY);
					g.fillOval(50 + pinSizeSpacing_X*j, 50+pinSizeSpacing_Y*i, pinSize, pinSize);
					g.setColor(Color.BLACK);
					g.drawOval(50 + pinSizeSpacing_X*j, 50+pinSizeSpacing_Y*i, pinSize, pinSize);
					g.drawString("?", 65 + pinSizeSpacing_X*j, 75+pinSizeSpacing_Y*i);
				}
				else
				{  
					Color c = colors[(pinList.get(i)[j])]; 
					g.setColor(c);
					g.fillOval(50 + pinSizeSpacing_X*j, 50+pinSizeSpacing_Y*i, pinSize, pinSize);
				}
			}
		}
		for (int j = 0; j < numberOfPins; j++){ 
			g.setColor(Color.BLACK);
			g.drawOval(50 + pinSizeSpacing_X*j, 50+pinSizeSpacing_Y*turn, pinSize, pinSize);

			//fill if there is a guess
			if (guess.get(j) != -1){
				Color c = colors[(guess.get(j))]; 
				g.setColor(c);
				g.fillOval(50 + pinSizeSpacing_X*j, 50+pinSizeSpacing_Y*turn, pinSize, pinSize);
			}
		}


		//DRAW RESULT
		for (int i = 0; i < turn; i++){
			int count = 0;
			//draw correct answers
			for (int j = 0; j <  (pinListResult.get(i)[0]); j++){
				g.setColor(Color.BLACK);
				if (count < 2){
					g.drawOval(300 + resultPinSizeSpacing_X*count, 55+pinSizeSpacing_Y*i, resultPinSize, resultPinSize);
					g.fillOval(300 + resultPinSizeSpacing_X*count, 55+pinSizeSpacing_Y*i, resultPinSize, resultPinSize);
				}
				else{
					g.drawOval(300 + resultPinSizeSpacing_X*(count-2), 55+pinSizeSpacing_Y*i+resultPinSizeSpacing_Y, resultPinSize, resultPinSize);
					g.fillOval(300 + resultPinSizeSpacing_X*(count-2), 55+pinSizeSpacing_Y*i+resultPinSizeSpacing_Y, resultPinSize, resultPinSize);
				}				
				count++;
			}
			//draw correct colors
			for (int j = 0; j <  (pinListResult.get(i)[1]); j++){
				if (count < 2){
					g.setColor(Color.BLACK);
					g.drawOval(300 + resultPinSizeSpacing_X*(count), 55+pinSizeSpacing_Y*i, resultPinSize, resultPinSize);
				}
				else
				{
					g.setColor(Color.BLACK);
					g.drawOval(300 + resultPinSizeSpacing_X*(count-2), 55+pinSizeSpacing_Y*i+resultPinSizeSpacing_Y, resultPinSize, resultPinSize);
				}
				count++;
			}
		}
		if (gameOver){
			int count = 0;
			
			for (int j = 0; j <  (pinListResult.get(turn-1)[0]); j++){
				g.setColor(Color.BLACK);
				if (count < 2){
					g.drawOval(300 + resultPinSizeSpacing_X*count, 55+pinSizeSpacing_Y*turn, resultPinSize, resultPinSize);
					g.fillOval(300 + resultPinSizeSpacing_X*count, 55+pinSizeSpacing_Y*turn, resultPinSize, resultPinSize);
				}
				else{
					g.drawOval(300 + resultPinSizeSpacing_X*(count-2), 55+pinSizeSpacing_Y*turn+resultPinSizeSpacing_Y, resultPinSize, resultPinSize);
					g.fillOval(300 + resultPinSizeSpacing_X*(count-2), 55+pinSizeSpacing_Y*turn+resultPinSizeSpacing_Y, resultPinSize, resultPinSize);
				}				
				count++;
			}
			//draw correct colors
			for (int j = 0; j <  (pinListResult.get(turn-1)[1]); j++){
				if (count < 2){
					g.setColor(Color.BLACK);
					g.drawOval(300 + resultPinSizeSpacing_X*(count), 55+pinSizeSpacing_Y*turn, resultPinSize, resultPinSize);
				}
				else
				{
					g.setColor(Color.BLACK);
					g.drawOval(300 + resultPinSizeSpacing_X*(count-2), 55+pinSizeSpacing_Y*turn+resultPinSizeSpacing_Y, resultPinSize, resultPinSize);
				}
				count++;
			}
		}
		if (gameCompleted){
			g.drawRect(WIDTH/2-40, HEIGHT-60, 100, 20);
			g.drawString("New game", WIDTH/2-25, HEIGHT-45);
		}
		else {
			g.setColor(Color.BLACK);
			g.drawRect(WIDTH/2-30, HEIGHT-60, 60, 20);
			g.drawString("Submit", WIDTH/2-25, HEIGHT-45);
		}
		if (gameOver){
			g.setColor(rect);
			g.fillRect(100, HEIGHT/2 - 25, WIDTH-200, 40);
			font = new Font("Arial", Font.BOLD, 25);
			g.setColor(Color.BLACK);
			g.setFont(font);
			g.drawString(gameCompleted ? "CONGRATULATIONS" : "You ran out of turns", 125, HEIGHT/2);
		}
	}

	public static void main(String [] args){
		MasterMind ex = new MasterMind();
		new Thread(ex).start();
	}

}
