import java.util.Arrays;

public class Tetromino {
		
	public int[][] shape;
	public int[] topLeft;
	
	boolean gameOver = false;

	public int[][] nextTetrominoShape;
	
	final int TYPE_I = 0;
	final int TYPE_J = 1;
	final int TYPE_L = 2;
	final int TYPE_O = 3;
	final int TYPE_S = 4;
	final int TYPE_T = 5;
	final int TYPE_Z = 6;
	
	int type;
	int rotation;
	
	int nextTetrominoType;
	int tetrominoCount = 0;

	int movementDirection;
	final int movementDirectionDown = 0;
	final int movementDirectionLeft = 1;
	final int movementDirectionUp = 2;
	final int movementDirectionRight = 3;	
		
	public final int numberOfTetromino = 7;

	public int[][][] iTetromino_rotations;
	public int[][][] jTetromino_rotations;
	public int[][][] lTetromino_rotations;
	public int[][][] oTetromino_rotations;
	public int[][][] sTetromino_rotations;
	public int[][][] tTetromino_rotations;
	public int[][][] zTetromino_rotations;

	public Tetromino(int[][] grid){
		
		int randomTetromino = (int)(Math.random() * (0+(numberOfTetromino -0) + 0));
				
		iTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {0,1,0,0},
				new int[] {0,1,0,0},
				new int[] {0,1,0,0},
				new int[] {0,1,0,0}
			},
			new int[][] {
				new int[] {0,0,0,0},
				new int[] {0,0,0,0},
				new int[] {1,1,1,1}
			}
		};

		jTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {0,2},
				new int[] {0,2},
				new int[] {2,2}
			},
			new int[][] {
				new int[] {2,0,0},
				new int[] {2,2,2}
			},
			new int[][] {
				new int[] {2,2},
				new int[] {2,0},
				new int[] {2,0}
			},
			new int[][] {
				new int[] {2,2,2},
				new int[] {0,0,2}
			}
		};

		lTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {3,0},
				new int[] {3,0},
				new int[] {3,3}
			},
			new int[][] {
				new int[] {3,3,3},
				new int[] {3,0,0}
			},
			new int[][] {
				new int[] {3,3},
				new int[] {0,3},
				new int[] {0,3}
			},
			new int[][] {
				new int[] {0,0,3},
				new int[] {3,3,3}
			}
		};
		
		oTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {4,4},
				new int[] {4,4}
			}
		};
		
		sTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {0,5,5},
				new int[] {5,5,0}
			},
			new int[][] {
				new int[] {5,0},
				new int[] {5,5},
				new int[] {0,5},
			}
		};
		
		tTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {6,6,6},
				new int[] {0,6,0}
			},
			new int[][] {
				new int[] {0,6},
				new int[] {6,6},
				new int[] {0,6},
			},
			new int[][] {
				new int[] {0,6,0},
				new int[] {6,6,6}
			},
			new int[][] {
				new int[] {0,6,0},
				new int[] {0,6,6},
				new int[] {0,6,0},
			}
		};
		
		zTetromino_rotations = new int[][][] {
			new int[][] {
				new int[] {7,7,0},
				new int[] {0,7,7}
			},
			new int[][] {
				new int[] {0,7},
				new int[] {7,7},
				new int[] {7,0},
			}
		};
		
		resetTetromino(randomTetromino, grid);
	}

	public void resetTetromino(int tetrominoType, int[][] grid)
	{
		//reset topleft
		tetrominoCount += 1;

		topLeft = new int[]{4,0};
		//downwards movement
		movementDirection = movementDirectionDown;

		//reset rotation
		rotation = 0;		
		//set next tetromino
		nextTetrominoType = (int)(Math.random() * (0+(numberOfTetromino -0) + 0));

		switch (tetrominoType) {
		case TYPE_I:
		{
			shape = iTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		case TYPE_J: 
		{
			shape = jTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		case TYPE_L:
		{
			shape = lTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		case TYPE_O: 
		{
			shape = oTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		case TYPE_S: 
		{
			shape = sTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		case TYPE_T: 
		{
			shape = tTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		case TYPE_Z:
		{
			shape = zTetromino_rotations[rotation];
			type = tetrominoType;
			break;
		}
		}

		int[][] tmpShape = Arrays.copyOf(shape, shape.length);

		//Check if the game is over
		for (int row = 0; row < shape.length; row++) {                                                                                                           
			for (int col = 0; col < shape[row].length; col++) {
				if ((tmpShape[row][col] != 0) && (grid[col+topLeft[0]][row+topLeft[1]] != 0))
					gameOver = true;
			}
		}

		//next tetromino
		switch (nextTetrominoType) {
		case TYPE_I:
		{
			nextTetrominoShape = iTetromino_rotations[rotation];
			break;
		}
		case TYPE_J: 
		{
			nextTetrominoShape = jTetromino_rotations[rotation];
			break;
		}
		case TYPE_L:
		{
			nextTetrominoShape = lTetromino_rotations[rotation];
			break;
		}
		case TYPE_O: 
		{
			nextTetrominoShape = oTetromino_rotations[rotation];
			break;
		}
		case TYPE_S: 
		{
			nextTetrominoShape = sTetromino_rotations[rotation];
			break;
		}
		case TYPE_T: 
		{
			nextTetrominoShape = tTetromino_rotations[rotation];
			break;
		}
		case TYPE_Z:
		{
			nextTetrominoShape = zTetromino_rotations[rotation];
			break;		
		}
		}
	}

		public int getType() {
			return type;
		}

		public int getTypeNext() {
		return nextTetrominoType;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRotation() {
		return rotation;
	}
	
	public int[][] returnNewShape(int tetrominoType, int[][] shape, boolean changeRotation) {
		int oldRotation = rotation;
		rotation += 1;

		switch (tetrominoType) {
		case TYPE_I:
		{
			if (rotation+1 > iTetromino_rotations.length)
				rotation = 0;
			shape = iTetromino_rotations[rotation];
			break;
		}
		case TYPE_J: 
		{
			if (rotation+1 > jTetromino_rotations.length)
				rotation = 0;
			shape = jTetromino_rotations[rotation];
			break;
		}
		case TYPE_L:
		{
			if (rotation+1 > lTetromino_rotations.length)
				rotation = 0;
			shape = lTetromino_rotations[rotation];

			break;
		}
		case TYPE_S: 
		{
			if (rotation+1> sTetromino_rotations.length)
				rotation = 0;
			shape = sTetromino_rotations[rotation];
			break;
		}
		case TYPE_T: 
		{
			if (rotation+1 > tTetromino_rotations.length)
				rotation = 0;
			shape = tTetromino_rotations[rotation];
			break;
		}
		case TYPE_Z:
		{
			if (rotation+1 > zTetromino_rotations.length)
				rotation = 0;				
			shape = zTetromino_rotations[rotation];
			break;
		}
		}
		if (!changeRotation)
			rotation = oldRotation;
		return shape;
	}

	public void setTopLeft(int x, int y) {
		topLeft[0] =  topLeft[0]+x;
		topLeft[1] =  topLeft[1]+y;

	}

	public boolean canMove(int i, int j, int rotate, int[][] grid, boolean tickMove) {

		int[] tmpTopLeft = Arrays.copyOf(topLeft, topLeft.length);
		int[][] tmpShape = Arrays.copyOf(shape, shape.length);
		
		tmpTopLeft[0] = topLeft[0]+i;
		tmpTopLeft[1] = topLeft[1]+j;
		
		if (rotate > 0)
		tmpShape = returnNewShape(type, tmpShape, false);
				
		//don't move left or right out of the grid
		if ((tmpTopLeft[0] + returnFirstOccupiedRowLeft(tmpShape) < 0) || (tmpTopLeft[0]+returnFirstOccupiedRowRight(tmpShape) >= Tetris.GRIDSIZE_X))
		{
			return false;
		}
				
		
		//first tetromino hitting the floor
		if ((tmpTopLeft[1]- returnFirstOccupiedRowTop(tmpShape) < 0) || (tmpTopLeft[1]+returnFirstOccupiedRowButton(tmpShape) >= Tetris.GRIDSIZE_Y))
		{
			return false;
		}

		//test for collision
		for (int row = 0; row < tmpShape.length; row++) {                                                                                                           
			for (int col = 0; col < tmpShape[row].length; col++) {

				//catch out of grid instances
				if (col + tmpTopLeft[0] > Tetris.GRIDSIZE_X)
					continue;

				if (row + tmpTopLeft[1] > Tetris.GRIDSIZE_Y)
					continue;

				//return false if grid is already occupied
				if ((tmpShape[row][col] != 0) && (grid[col+tmpTopLeft[0]][row+tmpTopLeft[1]] != 0))
					return false;										
			}
		}
		if (rotate > 0)
			shape = Arrays.copyOf(tmpShape, shape.length);
		return true;
	}

	

	//move tetromino at each tick
	public boolean tickMove(int[][] grid) {

		boolean canMove = false;

		//down
		if (movementDirection == 0){
			if (canMove(0,1,0, grid,true)){
				setTopLeft(0,1);
				canMove = true;
			}
		}
		//left
		else if (movementDirection == 1){		
			if (canMove(-1,0,0, grid,true)){
				setTopLeft(-1,0);
				canMove = true;
			}
		}
		//up
		else if (movementDirection == 2){		
			if (canMove(0,-1,0, grid,true)){
				setTopLeft(0,-1);
				canMove = true;
			}
		}
		//right
		else if (movementDirection == 3){		
			if (canMove(1,0,0, grid,true)){
				setTopLeft(1,0);
				canMove = true;
			}
		}
		//add current tetromino to grid if movement is hindered
		if (!canMove)
		{		
			for (int row = 0; row < shape.length; row++) {                                                                                                           
				for (int col = 0; col < shape[row].length; col++) {
					if (shape[row][col] != 0)
						grid[topLeft[0] + col][topLeft[1]+row] = shape[row][col];
					}
			}
		resetTetromino(nextTetrominoType, grid);
		return false;
		}
		return true;
	}


	//return tetromino borders
	public int returnFirstOccupiedRowLeft(int[][] tmpShape) {
		for (int col = 0; col < tmpShape[0].length; col++) {
			for (int row = 0; row < tmpShape.length; row++) { 
				if (tmpShape[row][col] != 0) {
					return col;
				}
			}
		}
		return 0;
	}
		
	public int returnFirstOccupiedRowRight(int[][] tmpShape) {

		for (int col = tmpShape[0].length -1; col >= 0; col--) {
			for (int row = 0; row < tmpShape.length; row++) { 
				if (tmpShape[row][col] != 0) {
					return col;
				}
			}
		}
		return 0;
	}
	
	public int returnFirstOccupiedRowTop(int[][] tmpShape) {
		for (int row = 0; row < tmpShape.length; row++) {
			for (int col = 0; col < tmpShape[0].length; col++) {
				if (tmpShape[row][col] != 0) {
					return row;
				}
			}
		}
		return 0;
	}
	
	public int returnFirstOccupiedRowButton(int[][] tmpShape) {
		for (int row = tmpShape.length-1; row >= 0; row--) {
			for (int col = 0; col < tmpShape[0].length; col++) {
				if (tmpShape[row][col] != 0) {
					return row;
				}
			}
		}
		return 0;
	}
}

