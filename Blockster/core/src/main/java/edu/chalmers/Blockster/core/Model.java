package edu.chalmers.Blockster.core;

import static edu.chalmers.Blockster.core.util.Calculations.*;
import static edu.chalmers.Blockster.core.util.Direction.*;

import java.util.ArrayList;
import java.util.List;

import edu.chalmers.Blockster.core.util.Direction;

/**
 * A class to represent a stage.
 * @author Oskar Jönefors, Eric Bjuhr
 * 
 */
public class Model {


	private BlockMap map;
	private BlockLayer blockLayer;
	private Block processedBlock;
	private Player activePlayer;
	private List<Player> players;

	private boolean isGrabbingBlockAnimation = false; //for animations
	private boolean isMovingBlockAnimation = false;
	private boolean isLiftingBlockAnimation = false;

	private boolean isGrabbingBlock = false; 
	private boolean isLiftingBlock = false;
	
	private Factory factory;

	//TODO Change this
	private final static String PLAYER_IMAGE_ADDRESS = "Player/still2.png";


	public Model(BlockMap map, Factory factory) {
		this.map = map;
		this.blockLayer = this.map.getBlockLayer();
		this.factory = factory;
		players = new ArrayList<Player>();
		setStartPositions();

		activePlayer = players.get(0);
	}
	
	private void setStartPositions() {
		for (float[] startPosition : getPlayerStartingPositions(map)) {
			Player player = factory.createPlayer(PLAYER_IMAGE_ADDRESS);
			player.setX(startPosition[0]);
			player.setY(startPosition[1]);
			players.add(player);
		}
	}
	
	public void resetStartPositions() {
		float[][] startPositions = getPlayerStartingPositions(map);
		for (int i = 0; i < startPositions.length; i++) {
			players.get(i).setX(startPositions[i][0]);
			players.get(i).setY(startPositions[i][1]);
			players.get(i).setVelocityX(0);
			players.get(i).setVelocityY(0);
			players.get(i).resetGravity();
		}
	}

	public boolean canGrabBlock(Block block) {
		//TODO: Add additional tests (type of block etc)

		return block != null && processedBlock == null 
				&& !isGrabbingBlockAnimation && !isLiftingBlockAnimation 
				&& !isMovingBlockAnimation && (block.isMovable() || block.isLiftable());
	}

	public boolean canLiftBlock(Block block) {
		//TODO: Add additional tests
		return block != null && isGrabbingBlock && !isGrabbingBlockAnimation &&
				!isLiftingBlockAnimation && !isMovingBlockAnimation;
	}

	public boolean canMoveBlock(Direction dir) {
		//TODO: Add checks for collision etc
		return processedBlock != null && !isMovingBlockAnimation 
				&& !isLiftingBlockAnimation && !isGrabbingBlockAnimation;
	}

	@SuppressWarnings("unused")
	private boolean collisionAbove(Player player) {
		try {
			return collisionUpperLeft(player, blockLayer) ||
					collisionUpperRight(player, blockLayer);
		} catch (NullPointerException e) {
			return false;
		}
	}

	private boolean collisionBelow(Player player) {
		try {
			return collisionLowerLeft(player, blockLayer) ||
					collisionLowerRight(player, blockLayer);
		} catch (NullPointerException e) {
			return false;
		}
	}

	private boolean collisionHorisontally(Player player) {
		if (player.getVelocity().x < 0) {
			return collisionLeft(player);
		} else if (player.getVelocity().x > 0) {
			return collisionRight(player);
		} else {
			return false;
		}
	}

	private boolean collisionLeft(Player player) {
		try {
			return collisionUpperLeft(player, blockLayer) 
					|| collisionLowerLeft(player, blockLayer);
		} catch (NullPointerException e) {
			return false;
		}
	}

	private boolean collisionRight(Player player) {
		try {
			return collisionUpperRight(player, blockLayer)
					|| collisionLowerRight(player, blockLayer);
		} catch (NullPointerException e) {
			return false;
		}
	}

	private boolean collisionVertically(Player player) {
		if (player.getVelocity().y < 0) {
			return collisionBelow(player);
		}

		//TODO: What if blocks are above the character??
		return false;
	}
	
	public float getActivePlayerVelocity() {
		return activePlayer.getMaximumMovementSpeed();
	}

	public Block getAdjacentBlock(Direction dir) {
		float blockWidth = blockLayer.getBlockWidth();
		float blockHeight = blockLayer.getBlockHeight();
		Block block = null;

		try {
			if (dir == LEFT) {
				Block adjacentBlockLeft = blockLayer.getBlock(
						(int) (activePlayer.getX() / blockWidth) - 1,
						(int) ((2 * activePlayer.getY() + activePlayer.getHeight()) / 2 / blockHeight));
				block = (Block) adjacentBlockLeft;
			}

			if (dir == RIGHT) {
				Block adjacentBlockRight = blockLayer.getBlock(
						(int) ((activePlayer.getX() + activePlayer.getWidth()) / blockWidth) + 1,
						(int) ((2 * activePlayer.getY() + activePlayer.getHeight()) / 2 / blockHeight));

				block = (Block) adjacentBlockRight;
			}
		} catch (NullPointerException e) {
			block = null;
		}
		return block;
	}

	public BlockMap getMap() {
		return map;
	}

	public List<Player> getPlayers() {
		return players;
	}

	private float[][] getPlayerStartingPositions(BlockMap map) {
		//TODO
		return new float[][] {{600, 500}};
	}

	public Block getProcessedBlock() {
		return processedBlock;
	}

	public void grabBlock(Block block) {

		if (canGrabBlock(block)) {

			processedBlock = block;


			isGrabbingBlockAnimation = true;
			isGrabbingBlock = true;

			//TODO: Start grab animation
		}
	}

	public boolean isGrabbingBlock() {
		return isGrabbingBlock;
	}

	public boolean isLiftingBlock() {
		return isLiftingBlock;
	}

	public void liftBlock() {
		if (canLiftBlock(processedBlock)) {
			//If we are not already lifting a block, do so.
			isLiftingBlockAnimation = true;
			isLiftingBlock = true;
			isGrabbingBlock = false;
			//TODO: Start lift animation
		}
	}

	public void moveActivePlayer(Direction dir, float distance) {
		//Wrapper method
		movePlayer(dir, activePlayer, distance);
	}

	public boolean moveBlock(Direction dir) {
		if (canMoveBlock(dir)) {
			isMovingBlockAnimation = true;
			//TODO: move block in grid, animation, etc
			return true;
		}
		return false;
	}

	private void movePlayer(Direction dir, Player player, float distance) {
		player.move(dir, distance);
	}


	public void nextPlayer() {

	}

	public void stopProcessingBlock() {
		//TODO put down block animation, etc
		processedBlock = null;
	}

	public void update(float deltaTime) {
		//Set animation state etc		

		if (processedBlock != null && processedBlock.getAnimation() != Block.Animation.NONE) {
			
		}

		for (Player player : players) {
			if (!collisionVertically(player)) {
				player.increaseGravity(deltaTime);
				player.move(FALL, player.getGravity().y);
			}

			float[] previousPosition = { player.getX(), player.getY() };

			player.setX(player.getX() + player.getVelocity().x);
			if (collisionHorisontally(player)) {
				player.setX(previousPosition[0]);
			}

			player.setY(player.getY() + player.getVelocity().y);
			if (collisionVertically(player)) {
				player.setY(previousPosition[1]);
				player.resetGravity();

				float y2 = player.getY();
				float blockHeight = blockLayer.getBlockHeight();

				player.setVelocityY(0);
				player.move(FALL,  + Math.abs(y2 - ((int) (y2 / blockHeight)) * blockHeight));
				player.setY(player.getY() + player.getVelocity().y);
			}


			player.setVelocityY(0);
			player.setVelocityX(0);

		}
	}
}
