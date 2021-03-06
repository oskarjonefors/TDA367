package edu.chalmers.blockster.core.objects.interactions;

import static org.junit.Assert.fail;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.chalmers.blockster.core.objects.Block;
import edu.chalmers.blockster.core.objects.BlocksterMap;
import edu.chalmers.blockster.core.objects.Player;
import edu.chalmers.blockster.core.objects.World;
import edu.chalmers.blockster.core.objects.movement.AnimationState;
import edu.chalmers.blockster.core.objects.movement.Direction;
import edu.chalmers.blockster.core.objects.movement.Movement;

public class BlockGrabbedInteractionTest {
	
	private Player player;
	private Block block1;
	private Block block2;
	private BlocksterMap blockMap;
	private BlockGrabbedInteraction interaction;
	
	private void checkDown() {
		final Movement none = Movement.GRAB_LEFT; 
		final Movement none2 = Movement.GRAB_RIGHT;
		Movement playerMovement;
		
		/*
		 * Check down
		 */
		setVerticallyOutOfBoundsDown();
		
		/*
		 * Test the far lower end in the left direction
		 */
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		playerMovement = player.getAnimationState().getMovement();
		if (playerMovement != none && playerMovement != none2){
			fail("Horisontal test failed on the left side");
		}
		
		/*
		 * Test the far lower end in the right direction
		 */
		player.setDirection(Direction.RIGHT);
		interaction.interact(Direction.RIGHT);
		playerMovement = player.getAnimationState().getMovement();
		
		if (playerMovement != none && playerMovement != none2){
			fail("Horisontal test failed on the right side");
		}
	}
	
	private void checkNoOutOfBoundsHorisontally(Direction dir) {
		final Movement none = Movement.GRAB_LEFT; 
		final Movement none2 = Movement.GRAB_RIGHT;
		player.setDirection(dir);
		interaction.interact(dir);
		final Movement playerMovement = player.getAnimationState().getMovement();
		if (playerMovement != none && playerMovement != none2) {
			fail("Horisontal test (No OOB) failed with a "
					+dir+"wards direction");
		}
	}
	
	private void checkOutOfBoundsHorisontally(Direction dir) {
		final Movement none = Movement.GRAB_LEFT; 
		final Movement none2 = Movement.GRAB_RIGHT;
		player.setDirection(dir);
		interaction.interact(dir);
		
		final Movement playerMovement = player.getAnimationState().getMovement();
		if (playerMovement != none && playerMovement != none2){
			fail("Horisontal test (OOB) failed with a "
					+dir+"wards direction");
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
		final List<Point> playerPositions = new ArrayList<Point>(); 
		playerPositions.add(new Point(1, 3));
		blockMap = new BlocksterMap(10, 10, 128, 128, playerPositions);
		player = new Player(1, 3, blockMap, World.DAY);
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
	public void testBlockAbovePulledBlock() {
		{
			final Block weighted = new Block(2, 4, blockMap);
			weighted.setProperty("weight");
			blockMap.insertBlock(weighted);
			tryPullBlockLeftwards(false);
			
			setUp();
			blockMap.insertBlock(new Block(2, 4, blockMap));
			tryPullBlockLeftwards(true);
		}
		
		{
			setUp();
			Block weighted = new Block(2, 4, blockMap);
			weighted.setProperty("weight");
			blockMap.insertBlock(weighted);
			blockMap.removeBlock(blockMap.getBlock(0, 2));
			tryPullBlockLeftwards(false);
			
			setUp();
			blockMap.insertBlock(new Block(2, 4, blockMap));
			blockMap.removeBlock(blockMap.getBlock(0, 2));
			tryPullBlockLeftwards(false);
		}
	}
	
	public void tryPullBlockLeftwards(boolean shouldPull) {
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		
		if (player.getAnimationState().getMovement().isPullMovement() != shouldPull) {
			fail("Should" + (shouldPull ? " " : " not ") + "be pulling block");
		}
	}
	
	@Test
	public void testEndInteraction() {
		interaction.endInteraction();
		
		if (player.getAnimationState()
				!= AnimationState.NONE) {
			fail ("Animation state should be NONE");
		}
		
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
		blockMap.removeBlock(blockMap.getBlock(0, 2));
		
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		
		if (player.getAnimationState().getMovement() != Movement.GRAB_RIGHT) {
			fail("Should still be grabbing block");
		}
		
		setUp();
		player.setDirection(Direction.LEFT);
		interaction.interact(Direction.LEFT);
		
		if (!player.getAnimationState().getMovement().isPullMovement()) {
			fail("Should be pulling block");
		}
	}
	
	@Test
	public void testInteractRight() {
		block2.removeProperty("movable");
		interaction.interact(Direction.RIGHT);
		if (player.getAnimationState().getMovement() != Movement.GRAB_RIGHT) {
			fail("Player should be grabbing a block to the right");
		}


		block2.setProperty("movable");
		interaction.interact(Direction.RIGHT);
		if (player.getAnimationState()
				.getMovement() == Movement.NONE) {
			fail("Player should be moving");
		}

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
		Movement none = player.getDirection() == Direction.LEFT ? Movement.GRAB_LEFT : Movement.GRAB_RIGHT;
		if (player.getAnimationState().getMovement() != none) {
			fail("Player shouldn't be moving");
		}
	}
	

}
