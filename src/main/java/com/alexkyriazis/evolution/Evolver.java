package com.alexkyriazis.evolution;

import java.util.HashMap;
import java.util.Map;

public abstract class Evolver {

	private Map<Character, Action> actionMap = new HashMap<Character, Action>();
	private String dna;
	private int fitness = 0;

	protected interface Action {
		void act();
	}

	/**
	 * Creates an individual
	 * 
	 * simulateLife() will need to be called before details about the individual
	 * will actually be calculated
	 * 
	 * @param dna:
	 *            The DNA of this individual
	 */
	public Evolver(String dna) {
		this.dna = dna;
		this.defineBehaviour();
	}

	/**
	 * Returns the DNA of this particular Evolver
	 * 
	 * This value is mutable and could change between successive calls.
	 * 
	 * @return the current string representation of the DNA.
	 */
	public String getDna() {
		return this.dna;
	}

	/**
	 * Returns the fitness value of this Evolver.
	 * 
	 * This value could change between successive calls (if the evolver's DNA
	 * has changed)
	 * 
	 * @precondition This evolver has been updated, either through a direct call
	 *               to simulateLife() or through default (or explicit) updating
	 *               after mutation or crossover.
	 * 
	 * @return the fitness value as defined by a custom fitness function. If no
	 *         update occurred, the default value of fitness is 0.
	 */
	public int getFitness() {
		return this.fitness;
	}
	
	/**
	 * Force updates the evolver so that its fitness is consistent with its DNA.
	 * 
	 * @modifies the Fitness of this individual.
	 */

	public void simulateLife() {
		this.reset();
		this.computeDna();
		this.updateFitness();
	}

	/**
	 * Mutates the DNA of this evolver by a given mutation rate. The rate given
	 * does not guarantee that an exact percentage of the genetic bases will be
	 * mutated. Each letter 'rolls the die' to determine if it will be mutated
	 * to a DIFFERENT letter. Optionally, there is the choice to auto-update the
	 * fitness of the evolver afterwards.
	 * 
	 * @param mutationRate:
	 *            a percentage that reflects the probability that each
	 *            individual genetic letter will be mutated. Between 0 and 1.
	 * 
	 * @param shouldUpdateAfter:
	 *            indicates whether or not the evolver should be resimulated
	 *            (recompute behaviour and fitness). If multiple mutations are
	 *            occuring, it is advised that you set this to false, and force
	 *            update after all mutations with simulateLife().
	 * 
	 * @modifies the DNA of this individual
	 * 
	 *           Example: If the dna consisted of 1000 bases A,C,T or G, and
	 *           mutate is called with a mutation of 10%, it is most likely that
	 *           100 of these bases will mutate to a different letter. However,
	 *           it is not guaranteed that exactly 10% will mutate because it is
	 *           based on probability.
	 */
	public void mutate(double mutationRate, Boolean shouldUpdateAfter) {

		StringBuffer sb = new StringBuffer(this.dna);
		Character[] dnaBases = actionMap.keySet().toArray(new Character[actionMap.keySet().size()]);

		for (int i = 0; i < sb.length(); i++) {
			if (Math.random() < mutationRate) {
				Character toReplace = sb.charAt(i);
				Character newChar;
				do {
					newChar = dnaBases[(int) (Math.random() * dnaBases.length)];
				} while (toReplace.equals(newChar));

				sb.replace(i, i + 1, newChar.toString());
			}
		}
		this.setDna(sb.toString());
		this.simulateLife();
	}

	/**
	 * Mutates the DNA of this evolver by a given mutation rate, then
	 * resimulates/updates fitness. The rate given does not guarantee that an
	 * exact percentage of the genetic bases will be mutated. Each letter 'rolls
	 * the die' to determine if it will be mutated to a DIFFERENT letter.
	 * 
	 * @param mutationRate:
	 *            a percentage that reflects the probability that each
	 *            individual genetic letter will be mutated. Between 0 and 1.
	 * 
	 * @modifies the DNA of this individual
	 * 
	 *           Example: If the dna consisted of 1000 bases A,C,T or G, and
	 *           mutate is called with a mutation of 10%, it is most likely that
	 *           100 of these bases will mutate to a different letter. However,
	 *           it is not guaranteed that exactly 10% will mutate because it is
	 *           based on probability.
	 */
	public void mutate(double mutationRate) {
		this.mutate(mutationRate, true);
	}

	/**
	 * Crosses two DNA strands. Similar to the procedure in biology. Given two
	 * evolvers, their two DNAs are spliced such the first evolver's DNA becomes
	 * the first part of its original DNA plus the last part of the other
	 * evolver's DNA. Similarily, the other evolver keeps its own first part and
	 * splices it with the first evolver's last part. The snipping point is
	 * determined randomly. The evolvers are then given the new DNA. There is
	 * also the option to auto-update the evolver's fitness afterwards.
	 * 
	 * @param other
	 *            The evolver to breed with. Must be of the same type and have
	 *            the same length of DNA otherwise nothing will happen.
	 * 
	 * @param shouldUpdateAfter:
	 *            indicates whether or not both evolvers should be resimulated
	 *            (recompute behaviour and fitness). If multiple
	 *            mutations/crossovers are occuring, it is advised that you set
	 *            this to false, and force update both evolvers after all
	 *            mutations with simulateLife().
	 * 
	 * @modifies both evolvers. Both their DNAs change.
	 * 
	 *           Example: If the DNA of the organisms are ACCG and CTAA, after a
	 *           splice before the second character for each, we have A | CCG
	 *           and C | TTA. Crossing over changes the DNA to ATTA and CCCG.
	 */
	public void crossOver(Evolver other, Boolean shouldUpdateAfter) {

		if (this.getClass() != other.getClass()) {
			return;
		}
		if (this.getDna().length() != other.getDna().length()) {
			return;
		}

		int splicePoint = (int) Math.floor(Math.random() * this.getDna().length());

		String splice1 = this.getDna().substring(0, splicePoint) + other.getDna().substring(splicePoint);
		String splice2 = other.getDna().substring(0, splicePoint) + this.getDna().substring(splicePoint);

		this.setDna(splice1);
		other.setDna(splice2);
	}

	/**
	 * Crosses two DNA strands, then resimulates/updates the fitness of the
	 * evolver. Similar to the procedure in biology. Given two evolvers, their
	 * two DNAs are spliced such the first evolver's DNA becomes the first part
	 * of its original DNA plus the last part of the other evolver's DNA.
	 * Similarily, the other evolver keeps its own first part and splices it
	 * with the first evolver's last part. The snipping point is determined
	 * randomly. The evolvers are then given the new DNA.
	 * 
	 * @param other
	 *            The evolver to breed with.
	 * 
	 * @modifies both evolvers. Both their DNAs change.
	 * 
	 *           Example: If the DNA of the organisms are ACCG and CTAA, after a
	 *           splice before the second character for each, we have A | CCG
	 *           and C | TTA. Crossing over changes the DNA to ATTA and CCCG.
	 */
	public void crossOver(Evolver other) {
		this.crossOver(other, true);
	}


	private void updateFitness() {
		this.fitness = this.computeFitness();
	}
	
	private void setDna(String dna) {
		this.dna = dna;
	}
	
	private void computeDna() {
		StringBuffer sb = new StringBuffer(this.dna);

		for (int i = 0; i < sb.length(); i++) {
			Character base = Character.valueOf(sb.charAt(i));

			Action action = this.actionMap.get(base);

			if (action != null) {
				action.act();
			} else {
				System.err.println("Action: '" + base.toString() + "' Does not exist. Ignoring");
			}
		}
	}
	
	protected void defineAction(char character, Action action) {
		this.actionMap.put(Character.valueOf(character), action);
	}

	/*To be overwritten*/
	protected abstract int computeFitness();
	protected abstract void reset();
	protected abstract void defineBehaviour();


}