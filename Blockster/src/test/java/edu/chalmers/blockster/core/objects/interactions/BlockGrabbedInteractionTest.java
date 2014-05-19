package edu.chalmers.blockster.core.objects.interactions;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.chalmers.blockster.core.objects.Block;
import edu.chalmers.blockster.core.objects.BlockMap;
import edu.chalmers.blockster.core.objects.Player;
import edu.chalmers.blockster.core.objects.movement.AnimationState;
import edu.chalmers.blockster.core.objects.movement.Direction;
import edu.chalmers.blockster.core.objects.movement.Movement;

public class BlockGrabbedInteractionTest {
	
	private Player player;
	private Block block1;
	private Block block2;
	private BlockMap blockMap;
	private BlockGrabbedInteraction interaction;
	
	private void checkDown() {
		Movement none = player.getDirection() == Direction.LEFT ? Movement.GRAB_LEFT : Movement.GRAB_RIGHT;
		
		/*
		 * Check down
		 */
		setVerticallyOutOfBoundsDown();
		
		/*
		 * Test the far lower end in the left direction
		 */
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		
		if (player.getAnimationState().getMovement() != none){
			fail("Horisontal test failed on the left side");
		}
		
		/*
		 * Test the far lower end in the right direction
		 */
		player.setDirection(Direction.RIGHT);
		interaction.interact(Direction.RIGHT);
		
		if (player.getAnimationState().getMovement() != none){
			fail("Horisontal test failed on the right side");
		}
	}
	
	private void checkNoOutOfBoundsHorisontally(Direction dir) {
		player.setDirection(dir);
		interaction.interact(dir);
		
		if (player.getAnimationState().getMovement() == Movement.GRAB_RIGHT) {
			fail("Horisontal test (No OOB) failed with a "
					+dir.toString().toLowerCase()+"wards direction");
		}
	}
	
	private void checkOutOfBoundsHorisontally(Direction dir) {
		player.setDirection(dir);
		interaction.interact(dir);
		
		if (player.getAnimationState().getMovement() !=Movement.GRAB_RIGHT){
			fail("Horisontal test (OOB) failed with a "
					+dir.toString().toLowerCase()+"wards direction");
		}
	}
	
	private void checkUp() {
		Movement none = Movement.GRAB_RIGHT;
		
		/*
		 * Check up
		 */
		setVerticallyOutOfBoundsUp();
		
		/*
		 * Test the far upper end in right direction.
		 */
		player.setDirection(Direction.RIGHT);
		interaction.interact(Direction.RIGHT);
		
		if (player.getAnimationState().getMovement() != none){
			fail("Horisontal test failed on the right side");
		}
		
		/*
		 * Test the far upper end in left direction.
		 */
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		
		if (player.getAnimationState().getMovement() != none){
			fail("Horisontal test failed on the right side");
		}
	}
	
	private void positionAtLeftBorder() {
		setUp();
		interaction.endInteraction();
		block1.removeFromGrid();
		block2.removeFromGrid();
		block1.setX(1);
		block2.setX(0);
		blockMap.insertBlock(block1);
		blockMap.insertBlock(block2);
		player.setX(2*player.getScaleX());
		interaction.startInteraction();
	}
	
	private void positionAtRightBorder() {
		setUp();
		interaction.endInteraction();
		block1.removeFromGrid();
		block2.removeFromGrid();
		block1.setX(8);
		block2.setX(9);
		blockMap.insertBlock(block1);
		blockMap.insertBlock(block2);
		player.setX(7*player.getScaleX());
		interaction.startInteraction();
	}
	
	@Before
	public void setUp() {
		blockMap = new BlockMap(10, 10, 128, 128, new int[][] {{1, 3}});
		player = new Player(1, 3, blockMap);
		block1 = new Block(2, 3, blockMap);
		block2 = new Block(3, 3, blockMap);
		interaction = new BlockGrabbedInteraction(player, block1, blockMap);
		block1.setProperty("movable");
		block2.setProperty("movable");
		blockMap.insertBlock(new Block(0, 2, blockMap));
		blockMap.insertBlock(new Block(1, 2, blockMap));
		blockMap.insertBlock(new Block(2, 2, blockMap));
		blockMap.insertBlock(new Block(3, 2, blockMap));
		blockMap.insertBlock(new Block(4, 2, blockMap));
		blockMap.insertBlock(new Block(5, 2, blockMap));
		blockMap.insertBlock(new Block(6, 2, blockMap));
		blockMap.insertBlock(new Block(7, 2, blockMap));
		blockMap.insertBlock(new Block(8, 2, blockMap));
		blockMap.insertBlock(new Block(9, 2, blockMap));
		blockMap.insertBlock(block1);
		blockMap.insertBlock(block2);
		player.setWidth(100);
		startInteraction();
		player.getAnimationState().updatePosition(player.getAnimationState().getMovement().getDuration());
	}
	
	private void setVerticallyOutOfBoundsDown() {
		interaction.endInteraction();
		block1.setY(-1);
		player.setY(-1*player.getScaleX());
		interaction.startInteraction();
	}
	
	private void setVerticallyOutOfBoundsUp() {
		interaction.endInteraction();
		block1.setY(10);
		player.setY(10*player.getScaleX());
		interaction.startInteraction();
	}
	
	private void startInteraction() {
		player.setDirection(Direction.RIGHT);
		interaction.startInteraction();
	}
	
	@Test
	public void testBlockAbovePushedBlock() {
		Block blockAbove = new Block(2, 4, blockMap);
		blockAbove.setProperty("movable");
		blockAbove.setProperty("weight");
		blockMap.insertBlock(blockAbove);
		
		interaction.interact(Direction.RIGHT);
		
		if (player.getAnimationState().getMovement() != Movement.GRAB_RIGHT) {
			fail("Could move with a weighted block above");
		}
		
		setUp();
		blockAbove = new Block(2, 4, blockMap);
		blockAbove.setProperty("movable");
		blockMap.insertBlock(blockAbove);
		interaction.interact(Direction.RIGHT);
		
		if (player.getAnimationState().getMovement() == Movement.GRAB_RIGHT) {
			fail("Could not move with a weightless block above");
		}
	}

	@Test
	public void testEndInteraction() {
		boolean success = (player.getAnimationState() 
						!= AnimationState.NONE);
		
		interaction.endInteraction();
		
		success &= player.getAnimationState()
				== AnimationState.NONE;
		
		assertTrue(success);
	}
	
	@Test
	public void testInteractionOutOfBounds() {

		
		blockMap.removeBlock(block1);
		checkUp();
		checkDown();

		positionAtLeftBorder();
		checkUp();
		
		positionAtLeftBorder();
		checkDown();

		positionAtRightBorder();
		checkUp();
		
		positionAtRightBorder();
		checkDown();
		
		positionAtLeftBorder();
		checkOutOfBoundsHorisontally(Direction.LEFT);
		
		positionAtLeftBorder();
		checkNoOutOfBoundsHorisontally(Direction.RIGHT);
		
		positionAtRightBorder();
		checkOutOfBoundsHorisontally(Direction.RIGHT);
		
		positionAtRightBorder();
		checkNoOutOfBoundsHorisontally(Direction.LEFT);
		
		
	}
	
	
	
	@Test
	public void testInteractLeft() {
		boolean success = true;
		blockMap.removeBlock(blockMap.getBlock(0, 2));
		
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		
		
		success &= player.getAnimationState().getMovement() == Movement.GRAB_RIGHT;
		
		
		blockMap.insertBlock(new Block(0, 2, blockMap));
		interaction.interact(Direction.LEFT);
		
		success &= player.getAnimationState()
				.getMovement().isPullMovement();
		
		assertTrue(success);
	}
	
	@Test
	public void testInteractRight() {
		boolean success = true;
		block2.removeProperty("movable");
		interaction.interact(Direction.RIGHT);
		success &= player.getAnimationState().getMovement() == Movement.GRAB_RIGHT;


		block2.setProperty("movable");
		interaction.interact(Direction.RIGHT);
		success &= (player.getAnimationState()
				.getMovement() != Movement.NONE);

		assertTrue(success);
	}
	
	@Test
	public void testOutOfRange() {
		player.setX(0);
		interaction.interact(Direction.RIGHT);
		if (player.isGrabbingBlock()) {
			fail("Out of horisontal range, should not continue to grab");
		}
		
		startInteraction();
		player.setX(128);
		player.setY(player.getY() + 128);
		interaction.interact(Direction.RIGHT);
		
		if (player.isGrabbingBlock()) {
			fail("Out of vertical range, should not continue to grab");
		}
	}
	
	@Test
	public void testStartInteraction() {
		assertTrue(player.getAnimationState() 
				== AnimationState.GRAB_RIGHT);

	}
	

}
