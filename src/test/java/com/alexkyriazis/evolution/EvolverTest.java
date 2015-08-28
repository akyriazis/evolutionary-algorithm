package com.alexkyriazis.evolution;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import com.alexkyriazis.evolution.TargetFinder;

public class EvolverTest {

	TargetFinder testEvolver;

	public final String DNA = "awaawwwswddsswd";
	public final int FINAL_X = 0;
	public final int FINAL_Y = 3;
	public final int FITNESS = 2;

	/* Test Evolver using the TargetFinder implementation */
	@Before
	public void init() {
		testEvolver = new TargetFinder(DNA);
	}

	/*
	 * See if TargetFinder correctly computes DNA to arrive at specified
	 * position
	 */
	@Test
	public void testDnaBehaviour() {
		testEvolver.simulateLife();
		assertTrue(testEvolver.getPosition().equals(new Point(FINAL_X, FINAL_Y)));
	}

	/*
	 * Test mutate function. Must be on an all or nothing basis since randomness
	 * is hard to test
	 */
	@Test
	public void testMutate() {
		String startingDna = testEvolver.getDna();

		testEvolver.mutate(0);
		String nonmutatedDna = testEvolver.getDna();

		testEvolver.mutate(1);
		String mutatedDna = testEvolver.getDna();

		assertTrue(startingDna.equals(nonmutatedDna));
		assertFalse(startingDna.equals(mutatedDna));
	}

	/* Test if crossover really does crossover DNA */
	@Test
	public void testCrossOver() {
		TargetFinder testEvolver2 = new TargetFinder("awwdsdsaawddwsw");

		String startingDna1 = testEvolver.getDna();
		String startingDna2 = testEvolver2.getDna();

		testEvolver.crossOver(testEvolver2);

		String endingDna1 = testEvolver.getDna();
		String endingDna2 = testEvolver2.getDna();

		int i;

		for (i = 0; i < startingDna1.length(); i++) {
			if (!(startingDna1.charAt(i) == endingDna1.charAt(i) && startingDna2.charAt(i) == endingDna2.charAt(i))) {
				assertTrue(startingDna1.charAt(i) != endingDna1.charAt(i)
						&& startingDna2.charAt(i) != endingDna2.charAt(i));
				break;
			}
		}

		if (i == startingDna1.length()) { // no crossover at all
			fail();
		}

		for (int j = i; j < startingDna1.length(); j++) {
			if (!(startingDna1.charAt(j) == endingDna2.charAt(j) && startingDna2.charAt(j) == endingDna1.charAt(j))) {
				fail();
			}
		}
	}

	/*
	 * Make sure mutation automatically triggers a fitness update when
	 * explicitly triggered to
	 */
	@Test
	public void testAutoUpdate() {
		assertEquals(testEvolver.getFitness(), 0);
		testEvolver.mutate(0, true);
		assertEquals(testEvolver.getFitness(), FITNESS);

	}

	/*
	 * Test to make sure fitness is unset before simulating, then changes to
	 * correct value after simulating
	 */
	@Test
	public void testFitness() {
		assertEquals(testEvolver.getFitness(), 0);
		testEvolver.simulateLife();
		assertEquals(testEvolver.getFitness(), FITNESS);
	}

}