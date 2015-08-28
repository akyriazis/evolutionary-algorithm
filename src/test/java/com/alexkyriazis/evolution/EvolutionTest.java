package com.alexkyriazis.evolution;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import com.alexkyriazis.evolution.Evolution;
import com.alexkyriazis.evolution.Evolver;
import com.alexkyriazis.evolution.TargetFinder;

public class EvolutionTest {

	Evolution evolutionTest;

	public final String DNA = "wadsaddadwadwaadawadwadwawdad";
	public final int GEN_NUM = 100;

	@Before
	public void init() {
		evolutionTest = new Evolution(TargetFinder.class, DNA);
	}

	/*
	 * Simply tests that getCurrentGeneration() returns a list of evolvers with
	 * the same DNA length
	 */
	@Test
	public void testGetCurrentGeneration() {
		List<Evolver> population = evolutionTest.getCurrentGeneration();
		int dnaSize = DNA.length();

		for (Evolver evol : population) {
			if (evol.getDna().length() != dnaSize) {
				fail();
			}
		}
	}

	/* Tests that the evolution keeps track of generation count */
	@Test
	public void testEvolutionCount() {
		assertEquals(evolutionTest.getGenerationCount(), 0);
		for (int i = 0; i < GEN_NUM; i++) {
			evolutionTest.evolve(1);
		}
		assertEquals(evolutionTest.getGenerationCount(), GEN_NUM);
	}

	/*
	 * We can't test short-term evolution reliably since it is probablistic
	 * 
	 * What we can do, although we are not 100% certain due to the probalistic
	 * nature of genetic algorithms, is assume that fitness increases over time.
	 */

	@Test
	public void testEvolutionImprovement() {
		int startingFitness = evolutionTest.getCurrentGeneration().get(0).getFitness();
		for (int i = 0; i < GEN_NUM; i++) {
			evolutionTest.evolve(1);
		}
		int endingFitness = evolutionTest.getCurrentGeneration().get(0).getFitness();

		assertTrue(endingFitness > startingFitness);
	}

}