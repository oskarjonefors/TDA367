package edu.chalmers.Blockster.core;

import javax.vecmath.*;
import edu.chalmers.Blockster.core.util.Calculations;
import edu.chalmers.Blockster.core.util.Direction;
import static edu.chalmers.Blockster.core.util.Calculations.*;

/**
 * The model representing a player in the game Blockster
 * @author Emilia Nilsson and Eric Bjuhr
 *
 */
public class Player extends BlocksterObject{
	
	private float posX;
	private float posY;
	private float height;
	private float width;
	private AnimationState anim;
	private Vector2f velocity;
	private Vector2f defaultVelocity = new Vector2f(700, 700);
	private float totalTime = 0;
	private BlockLayer blockLayer;
	private Block processedBlock;
	private boolean isGrabbingBlock;
	private boolean isLiftingBlock;
	private InteractionState state = InteractionState.NONE;
	
	public Player(float startX, float startY, BlockLayer blockLayer) {
		super(startX, startY, blockLayer);
		this.posX = startX;
		this.posY = startY;
		this.blockLayer = blockLayer;
		anim = AnimationState.NONE;
		velocity = new Vector2f(0, 0);
	}
	
	public void setX(float x) {
		posX = x;
	}

	public void setY(float y) {
		posY = y;
	}
	
	@Override
	public float getX() {
		//If there is an animation currently running then
		//we want to return the relative position
		if (anim != AnimationState.NONE) {
			return posX + anim.getRelativePosition().x;
		}
		return posX;
	}
	
	@Override
	public float getY() {
		//If there is an animation currently running then
		//we want to return the relative position
		if (anim != AnimationState.NONE) {
			return posY + anim.getRelativePosition().y;
		}
		return posY;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setAnimationState(AnimationState anim) {
		this.anim = anim;
	}
	
	public AnimationState getAnimationState() {
		return this.anim;
	}
	
	public Vector2f getVelocity() {
		return velocity;
	}
	
	public void setVelocityX(float velocityX) {
		if(Math.abs(velocityX) > defaultVelocity.x) {
			velocity.x = Math.signum(velocityX) * defaultVelocity.x;
		} else {
			velocity.x = velocityX;
		}
	}
	
	public void setVelocityY(float velocityY) {
		if(Math.abs(velocityY) > defaultVelocity.y) {
			velocity.y = Math.signum(velocityY) * defaultVelocity.y;
		} else {
			velocity.y = velocityY;
		}
	}
	
	public void setDefaultVelocity(Direction dir) {
		setVelocityX(getVelocity().x + dir.deltaX * defaultVelocity.x);
		setVelocityY(getVelocity().y + dir.deltaY * defaultVelocity.y);
	}
	
	public void increaseGravity(float deltaTime) {
		totalTime += deltaTime;
		setVelocityY(-9.82F * totalTime * blockLayer.getBlockHeight());
	}
	
	public void resetGravity() {
		totalTime = 0;
	}
	
	public Block getProcessedBlock() {
		return processedBlock;
	}
	
	public void grabBlock(Block block) {
		if (canGrabBlock(block)) {
			processedBlock = block;
			isGrabbingBlock = true;
		}
	}
	
	private boolean canGrabBlock(Block block) {
		return block != null && !isInteracting() && isNextToBlock(block) &&
				(block.isMovable() || block.isLiftable());
	}
	
	public boolean isGrabbingBlock() {
		return isGrabbingBlock;
	}
	
	public void liftBlock() {
		if (canLiftBlock(getProcessedBlock())) {
			//Lift process
			float relativePositionSignum = getProcessedBlock().getX()
								- getX() / blockLayer.getBlockWidth();
			AnimationState anim = relativePositionSignum > 0 ?
						new AnimationState(Movement.LIFT_LEFT) :
						new AnimationState(Movement.LIFT_RIGHT);
			getProcessedBlock().setAnimationState(anim);
			
			isLiftingBlock = true;
			isGrabbingBlock = false;
		}
	}
	
	private boolean canLiftBlock(Block block) {
		return block != null && !isInteracting() &&
				isNextToBlock(block) && block.isLiftable();
	}
	
	public boolean isLiftingBlock() {
		return isLiftingBlock;
	}
	
	public Block getAdjacentBlock(Direction dir) {
		return Calculations.getAdjacentBlock(dir, this, blockLayer);
	}
	
	public boolean isNextToBlock(Block block) {
		return block != null &&
		Math.abs(block.getX() - getX()) < 0.5f &&
		Math.abs(block.getY() - getY()) < 0.5f;
	}
	
	public void interact(Direction dir) {
		if (isInteracting()) {
			state.getInteraction().interact(dir);
		} else {
			setDefaultVelocity(dir);
		}
	}
	
	public boolean isInteracting() {
		return (state != InteractionState.NONE);
	}
	
	public void endInteraction() {
		state.getInteraction().endInteraction();
		state = InteractionState.NONE;
	}
	
	public boolean move(Vector2f distance) {
		float[] previousPosition = { getX(), getY() };
		boolean collision = false;

		setX(getX() + distance.x);
		if (collisionEitherCorner(this, blockLayer)) {
			setX(previousPosition[0]);
			collision = true;
		}

		setY(getY() + distance.y);
		if (collisionEitherCorner(this, blockLayer)) {
			setY(previousPosition[1]);
			if (distance.y < 0) {
				setY(((int)getY()/blockLayer.getBlockHeight())
							* blockLayer.getBlockHeight());
			}
			collision = true;
		}
		
		return !collision;
	}
	
	public void moveToNextPosition() {
		posX += anim.getMovement().getDirection().deltaX 
					* blockLayer.getBlockWidth();
		posY += anim.getMovement().getDirection().deltaY 
					* blockLayer.getBlockHeight();
	}

	public boolean updatePosition(float deltaTime) {
		if (anim != AnimationState.NONE) {
			anim.updatePosition(deltaTime);
			return false;
		} else {
			Vector2f distance = new Vector2f();
			distance.x = velocity.x * deltaTime;
			distance.y = velocity.y * deltaTime;
			
			return move(distance);
		}
	}
	
	/**
	 * This method is used when pulling a block and checks if the player
	 * can continue to pull it or if there is something blocking the way
	 * (this is usually taken care of by the collision avoidance, but when
	 * moving a block then this isn't available).
	 * 
	 * @param movement
	 * @return true if nothing is blocking the way behind player, else false.
	 */
	public boolean canMove(Movement movement) {
		int checkX = (int) (getX() / blockLayer.getBlockWidth());
		int checkY = (int) (getY() / blockLayer.getBlockWidth());
			
		return checkX >= 1 && checkX < blockLayer.getWidth() && !blockLayer.
				hasBlock(checkX + movement.getDirection().deltaX, checkY);
			
	}
	
}
