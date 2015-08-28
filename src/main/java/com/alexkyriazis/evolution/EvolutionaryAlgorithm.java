package com.alexkyriazis.evolution;

public class EvolutionaryAlgorithm {

	public static void main(String[] args) {
		String startingDna = "aswawaawawswadswdwdwwswswssswsdddwddwdwdswawadwawawdasaawawdaaaaadwadadadsadwawadwswswwsawsaswawsawsaadwads";
		Evolution findTarget = new Evolution(TargetFinder.class, startingDna);
		for (int i = 0; i < 500; i++) {
			findTarget.evolve(1);

			System.out.println("Generation " + findTarget.getGenerationCount());
			findTarget.printGen(findTarget.getCurrentGeneration());
		}

	}

}
