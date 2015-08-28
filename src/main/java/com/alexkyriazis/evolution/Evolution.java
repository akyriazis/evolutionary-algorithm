package com.alexkyriazis.evolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class Evolution {

	private List<Evolver> currentGeneration = new ArrayList<Evolver>();
	private int generationCount = 0;

	/*
	 * Config values. They are initialized here with default values to be
	 * overwritten by config file
	 */
	private int popSize = 10;
	private double probDistConst = 0.5;
	private double individualMutationRate = 0.5;
	private double dnaMutationRate = 0.02;
	private double crossOverRate = 1;

	private double probDist[];

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
	 * @param startingDna:
	 *            The DNA that the first generation evolves off of. Note: the
	 *            first generation does not exactly have this DNA, but rather a
	 *            mutation of it.
	 */
	public Evolution(Class<? extends Evolver> evolverType, String startingDna) {

		File configFile = new File("config.properties");

		try {
			FileReader reader = new FileReader(configFile);
			Properties props = new Properties();
			props.load(reader);

			this.popSize = Integer.parseInt(props.getProperty("popSize"));
			this.probDistConst = Double.parseDouble(props.getProperty("probDistConst"));
			this.individualMutationRate = Double.parseDouble(props.getProperty("individualMutationRate"));
			this.dnaMutationRate = Double.parseDouble(props.getProperty("dnaMutationRate"));
			this.crossOverRate = Double.parseDouble(props.getProperty("crossOverRate"));

			reader.close();
		} catch (FileNotFoundException ex) {
			System.err.println("Error: Cannot find file config.properties. Using default config values");
		} catch (IOException ex) {
			System.err.println("Error: Config read error. Using default values");
		}

		this.evolverType = evolverType;
		this.setup(startingDna);
	}

	/**
	 * Simulates evolution for the given number of generations.
	 * 
	 * After evolving, getCurrentGeneration() can be called to see the evolved
	 * population.
	 * 
	 * @param numGenerations:
	 *            the number of generations to simulate for. Must be greater
	 *            than 0.
	 */
	public void evolve(int numGenerations) {
		if (numGenerations < 0) {
			numGenerations = 0;
		}
		for (int i = 0; i < numGenerations; i++) {
			this.loop();
		}
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
	public List<Evolver> getCurrentGeneration() {
		return Collections.unmodifiableList(this.currentGeneration);
	}
	
	/**
	 * Prints DNA and Fitness data about a population of evolvers.
	 * 
	 * @param generation:
	 *            a list of evolvers whose information will be printed
	 */

	public void printGen(List<Evolver> generation) {
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

		// setup probability distribution. An accumulated, normalized set of
		// probabilities.
		double sum = 0;

		probDist = new double[this.popSize];
		for (int i = 0; i < this.popSize - 1; i++) {
			sum += (Math.pow(1 - this.probDistConst, i)) * this.probDistConst;
			probDist[i] = sum;
		}
		probDist[this.popSize - 1] = sum + Math.pow(1 - this.probDistConst, this.popSize - 1);
	}

	private void loop() {
		List<Evolver> newGen = breedNewGeneration(this.currentGeneration);
		this.createNewGeneration(newGen);
		this.simulateGeneration(this.currentGeneration);
	}

	private void simulateGeneration(List<Evolver> generation) {
		for (Evolver evolver : generation) {
			evolver.simulateLife();
		}
		generation.sort(Evolution.FitnessComparator);
	}

	private void createNewGeneration(List<Evolver> generation) {
		this.currentGeneration.clear();
		this.currentGeneration.addAll(generation);
		this.generationCount++;
	}

	private List<Evolver> breedNewGeneration(List<Evolver> oldGeneration) {
		List<Evolver> newPop = new ArrayList<Evolver>();

		for (int i = 0; i < this.popSize; i++) {
			double choice = Math.random();

			for (int j = 0; j < this.probDist.length; j++) {

				if (choice < this.probDist[j]) {
					newPop.add(Evolution.createEvolver(this.evolverType, oldGeneration.get(j).getDna()));
					break;
				}
			}
		}
		this.crossOver(newPop, this.crossOverRate);
		this.mutate(newPop, this.individualMutationRate, this.dnaMutationRate);

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

	private static Evolver createEvolver(Class<? extends Evolver> evolverType, String dna) {
		if (!Evolver.class.isAssignableFrom(evolverType)) {
			throw new UnsupportedOperationException("Trying to create a non-evolver class");
		}

		try {
			Evolver ev = evolverType.getConstructor(String.class).newInstance(dna);
			return ev;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
}