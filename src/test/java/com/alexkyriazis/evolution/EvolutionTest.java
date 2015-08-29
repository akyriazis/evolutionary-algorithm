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

	public final String DNA = "aswawaawawswadswdwdwwswswssswsdddwddwdwdswawadwawawdasaawawdaaaaadwadadadsadwawadwswswwsawsaswawsawsaadwads";
	public final int GEN_NUM = 500;
	public final int POP_SIZE = 10;


	@Before
	public void init() {
		evolutionTest = new Evolution(TargetFinder.class, POP_SIZE, DNA);
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
	
	/*Make sure evolution complains at incorrect parameters*/
	@Test
	public void testOutofBounds() {
		try {
			evolutionTest.evolve(-5);
			fail();
		}catch(Exception e){}
		
		try {
			evolutionTest.evolve(2,0.5,0.8,1.2,0.2);
			fail();
		}catch(Exception e){}
		
		try {
			evolutionTest.setDefaultEvolutionParameters(0.5,0.8,-67,0.2);
			fail();
		}catch(Exception e){}
	}
	
	/*
	 * We can't test short-term evolution reliably since it is probablistic
	 * 
	 * What we can do, although we are not 100% certain due to the probalistic
	 * nature of genetic algorithms, is assume that fitness increases over time.
	 */
	@Test
	public void testEvolutionImprovementQuantitative() {
		int startingFitness = evolutionTest.getCurrentGeneration().get(0).getFitness();
		for (int i = 0; i < GEN_NUM; i++) {
			evolutionTest.evolve(1);
		}
		int endingFitness = evolutionTest.getCurrentGeneration().get(0).getFitness();

		assertTrue(endingFitness > startingFitness);
	}
	
	/*
	 * No JUnit checking here. It displays the last generation in the console for a qualitative observation 
	 */	
	@Test
	public void testEvolutionImprovementQualitative() {
		for (int i = 0; i < GEN_NUM; i++) {
			evolutionTest.evolve(1);
		}
		System.out.println("Generation " + evolutionTest.getGenerationCount());
		evolutionTest.printGen(evolutionTest.getCurrentGeneration());		
	}
	
	
	

}