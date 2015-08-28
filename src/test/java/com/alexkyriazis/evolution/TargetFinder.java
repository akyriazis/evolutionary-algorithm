package com.alexkyriazis.evolution;

import java.awt.Point;

public class TargetFinder extends Evolver {

	public Point position = new Point(START_X, START_Y);

	private static final int START_X = 0;
	private static final int START_Y = 0;
	private static final int TARGET_X = 500;
	private static final int TARGET_Y = 500;

	/**
	 * Creates a target finder. An object that is assessed by its proximity to a
	 * set point.
	 * 
	 * @param The
	 *            DNA of this particular target finder.
	 */
	public TargetFinder(String dna) {
		super(dna);
	}

	/**
	 * Gets the current position of the object.
	 * 
	 * @return the 2D position.
	 */
	public Point getPosition() {
		return (Point) this.position.clone();
	}
	
	private int computeDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.round(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}

	@Override
	protected void defineBehaviour() {
		this.defineAction('a', new Action() {
			public void act() {
				TargetFinder.this.position.translate(-1, 0);
			}
		});

		this.defineAction('d', new Action() {
			public void act() {
				TargetFinder.this.position.translate(1, 0);
			}
		});

		this.defineAction('w', new Action() {
			public void act() {
				TargetFinder.this.position.translate(0, 1);
			}
		});

		this.defineAction('s', new Action() {
			public void act() {
				TargetFinder.this.position.translate(0, -1);
			}
		});
	}

	@Override
	protected int computeFitness() {
		return computeDistance(START_X, START_Y, TARGET_X, TARGET_Y)
				- computeDistance((int) this.position.getX(), (int) this.position.getY(), TARGET_X, TARGET_Y);
	}

	@Override
	protected void reset() {
		this.position.setLocation(START_X, START_Y);
	}
}