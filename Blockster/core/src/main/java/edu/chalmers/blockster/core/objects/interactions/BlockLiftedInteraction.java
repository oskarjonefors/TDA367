package edu.chalmers.blockster.core.objects.interactions;

import edu.chalmers.blockster.core.objects.movement.AnimationState;
import edu.chalmers.blockster.core.objects.movement.Direction;
import edu.chalmers.blockster.core.objects.movement.Movement;
import edu.chalmers.blockster.core.util.Calculations;
import edu.chalmers.blockster.core.util.GridMap;

public class BlockLiftedInteraction extends PlayerInteraction {
	
	private final Interactor interactor;
	private final Interactable interacted;
	private final GridMap blockMap;
	
	public BlockLiftedInteraction(Interactor interactor, Interactable interacted,
			GridMap blockMap) {
		this.interactor = interactor;
		this.interacted = interacted;
		this.blockMap = blockMap; 
	}

	@Override
	public void interact(Direction dir) {
		if(canPerformMove(dir)) {
			final AnimationState anim = 
					new AnimationState(Movement.getMoveMovement(dir));
			interactor.setAnimationState(anim);
			interacted.setAnimationState(anim);
		}
	}

	@Override
	public void endInteraction() {
		interactor.setLifting(false);
		
		final Direction dir = interactor.getDirection();
		
		interacted.setAnimationState(new AnimationState(Movement.getPlaceMovement(dir)));
		
		final AnimationState interactorAnim = dir == Direction.LEFT ?
				AnimationState.PLACE_LEFT : AnimationState.PLACE_RIGHT;
		
		interactor.setAnimationState(interactorAnim);
	}

	@Override
	public void startInteraction() {
		final Direction dir = Direction.getDirection(interactor.getX(),
				interacted.getX() * blockMap.getBlockWidth());
		interacted.setAnimationState(new AnimationState(Movement.getLiftMovement(dir)));
		interactor.setLifting(true);
		
		final AnimationState anim = dir == Direction.LEFT ?
				AnimationState.LIFT_RIGHT : AnimationState.LIFT_LEFT;
		
		interactor.setAnimationState(anim);
	}
	
	public boolean canPerformMove(Direction dir) {
		int flag = 0;
		if (dir == Direction.LEFT) {
			flag = Calculations.CHECK_LEFT_FLAG | Calculations.CHECK_DOWN_LEFT_FLAG;
		} else {
			flag = Calculations.CHECK_RIGHT_FLAG | Calculations.CHECK_DOWN_RIGHT_FLAG;
		}
		return !Calculations.collisionSurroundingBlocks(interacted, blockMap, flag);
	}

}
