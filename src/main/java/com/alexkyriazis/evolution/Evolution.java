package com.alexkyriazis.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Evolution {
	private double defaultSurvivalConstant = 0.5;
	private double defaultIndividualMutationRate = 0.5;
	private double defaultDnaMutationRate = 0.02;
	private double defaultCrossoverRate = 1;
	
	private List<Evolver> currentGeneration = new ArrayList<Evolver>();
	private int generationCount = 0;
	private int popSize;
	private double savedSurvivalDistribution[];

	private Class<? extends Evolver> evolverType;

	private static Comparator<Evolver> FitnessComparator = new Comparator<Evolver>() {
		public int compare(Evolver e1, Evolver e2) {
			return e2.getFitness() - e1.getFitness();
		}
	};

	private static Comparator<Evolver> FitnessAndDiversityComparator = new Comparator<Evolver>() {
		public int compare(Evolver e1, Evolver e2) {
			// TODO. This will be a better metric for assessing survival rates,
			// but more difficult to implement.
			return 0;
		}
	};

	/**
	 * Creates the evolution system.
	 * 
	 * @param evolverType:
	 *            The class type to evolve. Must extend Evolver.
	 * 
	 * @param popSize:
	 * 			  The number of individuals in each generation. Musts be larger than 0.
	 * 
	 * @param startingDna:
	 *            The DNA that the first generation evolves off of. Note: the
	 *            first generation does not exactly have this DNA, but rather a
	 *            mutation of it.
	 */
	public Evolution(Class<? extends Evolver> evolverType, int popSize, String startingDna) {
		if (popSize < 1) {
			throw new IllegalArgumentException("Population size out of bounds");
		}
		this.popSize = popSize;
		this.evolverType = evolverType;
		this.setup(startingDna);
	}

	/**
	 * Simulates evolution for the given number of generations, with advanced evolution parameters.
	 * 
	 * After evolving, getCurrentGeneration() can be called to see the evolved
	 * population.
	 * 
	 * @param numGenerations:
	 *            the number of generations to simulate for. Must be greater
	 *            than 0.
	 *            
	 * @param survivalConstant:
	 * 			  a number between 0 and 1 (inclusive) representative of the relative likeliness of the top performer to advance to the next generation compared to lower performers.
	 * 
	 * @param individualMutationRate:
	 * 			  a percentage between 0 and 1 representing the probability that a certain individual in a population will undergo mutation.
	 * 
	 * @param dnaMutationRate:
	 * 			  a percentage between 0 and 1 representing the probability that a letter in a DNA strand will undergo mutation (in an individual that has already been selected to mutate using the individualMutationRate probability)
	 * 
	 * @param double crossOverRate:
	 * 			  a percentage between 0 and 1 representing the probability that two individuals in a population will crossOver their DNA.
	 *
	 * @throws IllegalArgumentException if any parameter conditions are not met
	 */
	public void evolve(int numGenerations, double survivalConstant, double individualMutationRate, double dnaMutationRate, double crossOverRate) {
		if (numGenerations < 0
				|| survivalConstant < 0 || survivalConstant > 1 
				|| individualMutationRate < 0 || individualMutationRate > 1 
				|| dnaMutationRate < 0 || dnaMutationRate > 1 
				|| crossOverRate < 0 || crossOverRate > 1) {
			throw new IllegalArgumentException("Parameter(s) out of bounds");
		}
		for (int i = 0; i < numGenerations; i++) {
			this.loop(survivalConstant, individualMutationRate, dnaMutationRate, crossOverRate);
		}
	}
	
	/**
	 * Simulates evolution for the given number of generations, with default evolution parameters. These default parameters can be set with setDefaultEvolutionParameters()
	 * 
	 * After evolving, getCurrentGeneration() can be called to see the evolved
	 * population.
	 * 
	 * @param numGenerations:
	 *            the number of generations to simulate for. Must be greater
	 *            than 0.
	 *            
	 * The default evolution parameters are         
	 * 			  SURVIVAL_CONSTANT = 0.5
	 *			  INDIVIDUAL_MUTATION_RATE = 0.5
	 *			  DNA_MUTATION_RATE = 0.02
	 *			  CROSSOVER_RATE = 1
	 */
	public void evolve(int numGenerations) {
		evolve(numGenerations,this.defaultSurvivalConstant,this.defaultIndividualMutationRate,this.defaultDnaMutationRate,this.defaultCrossoverRate);
	}
	
	/**
	 * Returns the number of generations the population has been evolving for.
	 * 
	 * The initial population is defined as generation 0. After 1 evolve() it is
	 * at generation 1.
	 * 
	 * @return the generation count
	 */
	public int getGenerationCount() {
		return this.generationCount;
	}

	/**
	 * Gets the evolvers in the current generation
	 * 
	 * These evolvers have already been simulated meaning their properties are
	 * all accessible without having to call simulateLife()
	 * 
	 * Note: This is NOT a copied list. The representation invariant of the
	 * Evolvers will still be preserved, but the Evolvers are still mutable via
	 * mutate() and crossOver(). Excessive external mutation may delay the
	 * progress of the algorithm.
	 *
	 * @return the list of Evolvers from the current generation.
	 */
	public List<? extends Evolver> getCurrentGeneration() {
		return Collections.unmodifiableList(this.currentGeneration);
	}
	/**
	 * Sets the parameters that will be called by default when evolve(int numGenerations) is called.
	 * 
	 * Useful if the same parameters are to be used for the entire program.
	 * 
	 * @param survivalConstant:
	 * 			  a number between 0 and 1 (inclusive) representative of the relative likeliness of the top performer to advance to the next generation compared to lower performers.
	 * 
	 * @param individualMutationRate:
	 * 			  a percentage between 0 and 1 representing the probability that a certain individual in a population will undergo mutation.
	 * 
	 * @param dnaMutationRate:
	 * 			  a percentage between 0 and 1 representing the probability that a letter in a DNA strand will undergo mutation (in an individual that has already been selected to mutate using the individualMutationRate probability)
	 * 
	 * @param double crossOverRate:
	 * 			  a percentage between 0 and 1 representing the probability that two individuals in a population will crossOver their DNA.
	 *
	 * @throws IllegalArgumentException if any parameter conditions are not met
	 */
	public void setDefaultEvolutionParameters(double survivalConstant, double individualMutationRate, double dnaMutationRate, double crossOverRate) {
		if (survivalConstant < 0 || survivalConstant > 1 
				|| individualMutationRate < 0 || individualMutationRate > 1 
				|| dnaMutationRate < 0 || dnaMutationRate > 1 
				|| crossOverRate < 0 || crossOverRate > 1) {
			throw new IllegalArgumentException("Parameter(s) out of bounds");
		}
		
		this.defaultSurvivalConstant = survivalConstant;
		this.defaultIndividualMutationRate = dnaMutationRate;
		this.defaultDnaMutationRate = dnaMutationRate;
		this.defaultCrossoverRate = crossOverRate;
	}
	/**
	 * Prints DNA and Fitness data about a population of evolvers.
	 * 
	 * @param generation:
	 *            a list of evolvers whose information will be printed
	 */
	public void printGen(List<? extends Evolver> generation) {
		System.out.println("================");
		for (Evolver evol : generation) {
			System.out.println(evol.getDna() + " - " + evol.getFitness());
		}
		System.out.println();
	}
	
	private void setup(String dna) {

		// create initial population as mutated variants of the starting DNA

		for (int i = 0; i < this.popSize; i++) {
			Evolver individual = Evolution.createEvolver(this.evolverType, dna);
			individual.mutate(1, false);
			this.currentGeneration.add(individual);
		}
		this.simulateGeneration(this.currentGeneration);
	}

	private void loop(double survivalConstant, double individualMutationRate, double dnaMutationRate, double crossOverRate) {
		List<Evolver> newGen = breedNewGeneration(this.currentGeneration, survivalConstant, individualMutationRate, dnaMutationRate, crossOverRate );
		this.createNewGeneration(newGen);
		this.simulateGeneration(this.currentGeneration);
	}

	private void simulateGeneration(List<Evolver> generation) {
		for (Evolver evolver : generation) {
			evolver.simulateLife();
		}
		Collections.sort(generation, FitnessComparator);
	}

	private void createNewGeneration(List<Evolver> generation) {
		this.currentGeneration.clear();
		this.currentGeneration.addAll(generation);
		this.generationCount++;
	}

	private List<Evolver> breedNewGeneration(List<Evolver> oldGeneration, double survivalConstant, double individualMutationRate, double dnaMutationRate, double crossOverRate) {
		List<Evolver> newPop = new ArrayList<Evolver>();
		
		double[] probDist = this.getSurvivalProbabilities(survivalConstant);

		for (int i = 0; i < this.popSize; i++) {
			double choice = Math.random();

			for (int j = 0; j < probDist.length; j++) {

				if (choice < probDist[j]) {
					newPop.add(Evolution.createEvolver(this.evolverType, oldGeneration.get(j).getDna()));
					break;
				}
			}
		}
		this.crossOver(newPop, crossOverRate);
		this.mutate(newPop, individualMutationRate, dnaMutationRate);

		return newPop;
	}

	private void crossOver(List<Evolver> population, double crossOverRate) {
		Collections.shuffle(population);

		int numPairs = (int) Math.floor(population.size() / 2.0);

		for (int i = 0; i < numPairs; i++) {
			if (Math.random() < crossOverRate) {
				population.get(i * 2).crossOver(population.get(i * 2 + 1), false);
			}
		}
	}

	private void mutate(List<Evolver> population, double individualMutationRate, double dnaMutationRate) {
		Collections.shuffle(population);
		for (int i = 0; i < population.size(); i++) {
			if (Math.random() < individualMutationRate) {
				population.get(i).mutate(dnaMutationRate, false);
			}
		}
	}
	
	private double[] getSurvivalProbabilities(double survivalConstant) {
		
		if (this.savedSurvivalDistribution != null && this.savedSurvivalDistribution[0] == survivalConstant) {return this.savedSurvivalDistribution;}
		
		double sum = 0;
		double probDist[] = new double[this.popSize];
		
		for (int i = 0; i < this.popSize - 1; i++) {
			sum += (Math.pow(1 - survivalConstant, i)) * survivalConstant;
			probDist[i] = sum;
		}
		probDist[this.popSize - 1] = sum + Math.pow(1 - survivalConstant, this.popSize - 1);
		
		assert (Math.abs(probDist[this.popSize - 1] - 1) < 0.01);
		
		this.savedSurvivalDistribution = probDist;
		return probDist;
	}

	private static Evolver createEvolver(Class<? extends Evolver> evolverType, String dna) {
		if (!Evolver.class.isAssignableFrom(evolverType)) {
			throw new UnsupportedOperationException("Trying to create a non-evolver class");
		}

		try {
			Evolver ev = evolverType.getConstructor(String.class).newInstance(dna);
			return ev;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}