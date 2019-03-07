import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris implements Runnable{

	final int WIDTH = 800;
	final int HEIGHT = 1000;

	final static int GRIDSIZE_X = 10;
	final static int GRIDSIZE_Y = 16;
	final static int CELL_SIZE = 40;
	final int GRID_OFFSET = 20;

	public int[][] grid; 

	protected boolean left;
	protected boolean right;
	protected boolean down;
	protected boolean rotation;
	
	public boolean gameOver;
	
	long startTime;
	long lastUpdate;
	
	int score;
	int level;
	public Font font;

	private Tetromino tetromino;
	
	public final int numberOfTetromino = 7;


	public Color[] colors;

	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;

	public Tetris(){

		frame = new JFrame("Tetris");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		canvas.addMouseListener(new MouseControl());
		canvas.addKeyListener(new KeyControl());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLocation(500, 100);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();
		
		grid = new int[GRIDSIZE_X][GRIDSIZE_Y];
		newTetromino();	
		
		level = 1;
		score = 0;
		gameOver = false;
		
		colors = new Color[] {
				Color.WHITE, Color.CYAN,Color.BLUE,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.PINK, Color.RED
		};
	}
	private class KeyControl extends KeyAdapter{

	
	public void keyPressed(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_LEFT) setLeft(true);
		if(key.getKeyCode() == KeyEvent.VK_RIGHT) setRight(true);
		if(key.getKeyCode() == KeyEvent.VK_DOWN) setDown(true);
		if(key.getKeyCode() == KeyEvent.VK_SPACE) setRotation(true);

	}	
	
	public void keyReleased(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_LEFT) setLeft(false);
		if(key.getKeyCode() == KeyEvent.VK_RIGHT) setRight(false);
		if(key.getKeyCode() == KeyEvent.VK_DOWN) setDown(false);
		if(key.getKeyCode() == KeyEvent.VK_SPACE) setRotation(false);	
	}

	@Override
	public void keyTyped(KeyEvent key) {}
	}
	
	private class MouseControl extends MouseAdapter{
	}

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;

	boolean running = true;

	public void run(){

		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;
		startTime = System.nanoTime();
		lastUpdate= System.nanoTime();

		while(running){
			beginLoopTime = System.nanoTime();
			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();

			// we are still in business
			if (tetromino.gameOver == false){

				tick((int) ((currentUpdateTime - lastUpdateTime)/(1000*1000)));

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
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}


	protected void tick(int deltaTime){
		
		if (left){
			if (tetromino.canMove(-1,0,0, grid,false)){
			  tetromino.setTopLeft(-1,0); 
			}
		}
		else if (right){
			if (tetromino.canMove(1,0, 0,grid,false)){
			  tetromino.setTopLeft(1,0);
			}
		}
		else if (down){
			if (tetromino.canMove(0,1, 0,grid,false)){
			 tetromino.setTopLeft(0,1);
			}
		}
		else if (rotation){
			if (tetromino.canMove(0,0,1, grid,false)){
			tetromino.shape = tetromino.returnNewShape(tetromino.getType(), tetromino.shape, true);
			setRotation(false);
			}
		}
		resetKeyPress();

		//level
		updateLevel((System.nanoTime() - startTime)/1000000000);
		//automove

		if (((System.nanoTime()-lastUpdate)/1000000000) > updateInterval(level)){
			if (!tetromino.tickMove(grid))
				checkLayer();
			lastUpdate = System.nanoTime();
		}
	}
	//Starts at level 1, increases every 30 sec
	private void updateLevel(long timeGoneBy)
	{
		level = (int) (timeGoneBy/30+1);
	}	
	
	private int updateInterval(int level)
	{
		double updateIntervalPerLevel[] = { 
				1, 1, 1, 1, 1, 
				0.9, 0.9, 0.9, 0.9, 0.9,
				0.75, 0.75, 0.75, 0.75, 0.75, 
				0.6, 0.6, 0.6, 0.6, 0.6, 
				0.5, 0.5, 0.5, 0.5, 0.5,
		};
		
		if (level < 25)
			return (int) updateIntervalPerLevel[level];
		else
			return (int) updateIntervalPerLevel[24];
	}

	private void checkLayer() {
		boolean fullLayer;
		int layers =0;
	
		for (int row = GRIDSIZE_Y-1; row >= 0; row--) {
			fullLayer = true;
			for (int col = 0; col < GRIDSIZE_X; col++) {
				if (grid[col][row] == 0){
					fullLayer = false;
					break;
				}				
			}
			//we have a full layer
			if (fullLayer)
			{
				//lets start at the row with the full layer, adjust and work backwards
				for (int fixRow = row; fixRow >= 0; fixRow--) {
					for (int fixCol = 0; fixCol < GRIDSIZE_X; fixCol++) {
						if (fixRow != 0)
							grid[fixCol][fixRow] = grid[fixCol][fixRow-1];
						//top row is 0
						else
							grid[fixCol][fixRow] = 0;
					}				
				}
				layers++;
				//since we moved rows down 1, we need to row++ in order to not miss the row-1
				row++;
			}	
		}
		score += returnScore(layers, level);
	}
	
	private int returnScore(int numberOfLayers, int level){
		
		if (numberOfLayers == 0)
			return 0;
		else if (numberOfLayers == 1)
			return 40 * (level +1);
		else if (numberOfLayers == 2)
			return 100 * (level +1);
		else if (numberOfLayers == 3)
			return 300 * (level +1);
		else 
			return 1200 * (level +1);
	}

	private void resetKeyPress() {
		setLeft(false);
		setRight(false);
		setDown(false);
		setRotation(false);	
	}
	
	protected void newTetromino() {
		tetromino = new Tetromino(grid);
	}

	protected void render(Graphics2D g){

		//draw frame
		int thickness = 2;

		g.setColor(Color.RED);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new java.awt.BasicStroke(thickness)); 
		g.drawRect(GRID_OFFSET-2, GRID_OFFSET-2, GRIDSIZE_X * CELL_SIZE + 5, GRIDSIZE_Y * CELL_SIZE + 5);
		g.setStroke(oldStroke);
		//draw grid
		for (int i = 0; i < GRIDSIZE_X; i++){
			for (int j = 0; j < GRIDSIZE_Y*2; j++){

				// draw black border around cells
				if (j%2 == 0){
					g.setColor(Color.BLACK);
					g.drawRect(GRID_OFFSET + i * CELL_SIZE, GRID_OFFSET + j/2 * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				}	

				if (j%2 == 0)
					g.setColor(Color.GRAY);
				else 
					g.setColor(Color.DARK_GRAY);

				g.fillRect(GRID_OFFSET + i * CELL_SIZE, GRID_OFFSET + j * CELL_SIZE/2, CELL_SIZE, CELL_SIZE/2+1);	
			}

		}
		//draw grid
		for (int row = 0; row < GRIDSIZE_Y; row++) {
			for (int col = 0; col < GRIDSIZE_X; col++) {
				if (grid[col][row] != 0) {
					Color c = colors[grid[col][row]];
					g.setColor(c);
					g.fillRect(col * CELL_SIZE + GRID_OFFSET, row * CELL_SIZE + GRID_OFFSET , CELL_SIZE, CELL_SIZE);
				}
			}
		}
		
		//draw tetromino;

		//since getType 0 is white
		Color c = colors[tetromino.getType()+1];
		g.setColor(c);
		for (int row = 0; row < tetromino.shape.length; row++) {                                                                                                           
			for (int col = 0; col < tetromino.shape[row].length; col++) {
				if (tetromino.shape[row][col] != 0) {
					g.fillRect(((tetromino.topLeft[0] + col) * CELL_SIZE) + GRID_OFFSET, ((tetromino.topLeft[1] + row) * CELL_SIZE) + GRID_OFFSET , CELL_SIZE, CELL_SIZE);
				}
			}
		}
		
		//draw score, level and nextTetromino
		font = new Font("Arial", Font.BOLD, 25);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString("Level: " + level, WIDTH-185, 45);
		g.drawString("Score: " + score, WIDTH-185, 65);
		
		if (tetromino.gameOver)
		{
		font = new Font("Arial", Font.BOLD, 55);
		g.setFont(font);
		g.drawString("GAME OVER", WIDTH/2-150, HEIGHT/2-150);
		font = new Font("Arial", Font.BOLD, 35);
		g.setFont(font);
		g.drawString("Score: " + score, WIDTH/2-150, HEIGHT/2-50);
		}
		
		if (tetromino.gameOver == false)
		{
			g.drawString("Next Tetromino ", WIDTH-225, 125);

			//draw next tetromino
			Color c1 = colors[tetromino.getTypeNext()+1];
			g.setColor(c1);
			for (int row = 0; row < tetromino.nextTetrominoShape.length; row++) { 
				for (int col = 0; col < tetromino.nextTetrominoShape[row].length; col++) {
					if (tetromino.nextTetrominoShape[row][col] != 0) {
						g.fillRect(row*CELL_SIZE + WIDTH-165, (col *CELL_SIZE+ 165) , CELL_SIZE, CELL_SIZE);
					}

				}
			}
		}	
	}

	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setDown(boolean b) { down = b; }
	public void setRotation(boolean b) { rotation = b; }

	public static void main(String [] args){
		Tetris game = new Tetris();
		new Thread(game).start();
	}
}