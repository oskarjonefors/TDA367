package edu.chalmers.blockster.gdx.view;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import edu.chalmers.blockster.core.objects.World;

/**
 * A class used to draw background images.
 * @author Oskar Jönefors
 *
 */

public class BackgroundImage {
	
	private final Map<World, TextureRegion> backgrounds;
	private final float width;
	private final float height;
	private float scaleX;
	private float scaleY;
	private float x;
	private float y;
	
	public BackgroundImage(TextureRegion day, TextureRegion night) {
		backgrounds = new EnumMap<World, TextureRegion>(World.class);
		backgrounds.put(World.DAY, day);
		backgrounds.put(World.NIGHT, night);
		
		width = day.getRegionWidth();
		height = day.getRegionHeight();
		scaleX = 1f;
		scaleY = 1f;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void draw(SpriteBatch batch, World world) {
		batch.draw(backgrounds.get(world), x, y, width*scaleX, height*scaleY);
	}
	
	public void setScale(float scaleXY) {
		scaleX = scaleXY;
		scaleY = scaleXY;
	}
	
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
