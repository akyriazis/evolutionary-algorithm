# Evolutionary Algorithm Engine

Evolutionary Algorithms is a method of optimization inspired by nature. Given a problem, solutions that can be expressed as a string of characters, and have a quantitative function to determine the 'success' of a solution, one can implement an evolutionary algorithm to optimize the problem.

## How it works

A high level description is as follows.

A solution is thought of as a string of 'DNA'.

- An initial solution is evaluated to give a reference 'fitness'
- Mutations are made to the solution's DNA
- The newly mutated solution is re-evaluated to determine if its an improvement to the previous solution.
- If it is, use it and perform further mutations. If not, use the previous solution.

This procedure is very effective at solving specific kinds of optimization problems.

In this specific implementation of an evolutionary algorithm, solutions are evolved alongside a population of solutions. Each solution's 'fitness' evaluation determines how likely that particular solution (in the population of solutions for that generation) is to survive in the next generation. In addition to mutation, the DNA of solutions are changed by 'crossover'. This is essentially splicing of DNA between two individuals- reminiscent of breeding in nature.

## Quick Start

The code in this repository is responsible for generating the library. To use the evolutionary algorithm in your own project, you are welcome to download the library file [here](https://github.com/akyriazis/evolutionary-algorithm/releases/download/1.0.0/evolutionary-algorithm-1.0.0.jar).

###Steps to implementing the algorithm

1. Import the JAR library file (downloaded above) into your own eclipse project.

2. In your implementation, you must decide how your solution is to be represented by a DNA string. In your project, extend the Evolver class. You are required to implement 3 methods.

  ```
  /*
    This is where you will define how each DNA character is interpreted by your solution.
    When decoding DNA, the evolutionary algorithm will interpret the DNA character by character, and perform the actions specificed below.
    Example: In the Evolver implemented below, When the DNA "abbbababab" is decoded, the console output will be "ABBBABABAB"
	*/
	
	@Override
	protected void defineBehaviour() {
		this.defineAction('a', new Action() { public void act() {
				System.out.println("A");
		}});
		this.defineAction('b', new Action() { public void act() {
				System.out.println("B");
		}});
	}
  ```
  ```
  /*
    Implement a function that will evaluate the 'fitness' of this solution
  */
	@Override
	protected int computeFitness() {
      /* Some code to determine fitness */
      return myCalculatedFitness;
	}
  ```
  ```
  /*
    Implement a function that would reset your evolver back to its initial state.
  */
	@Override
	protected void reset() {
		  /*
		    Code that resets me
	    */
	}
  ```
3. To actually run the algorithm:

  ```
		Evolution myEvolution = new Evolution(myExtendedEvolver.class, 10, "abbbabbbabbbaa");
		myEvolution.evolve(500);
		myEvolution.printGen(myEvolution.getCurrentGeneration());		
  ```

  The evolution object is initialized with the evolver subclass you extended earlier and starting DNA. The second parameter represents the number of evolvers in each generation. The third parameter is the starting DNA. It should contains characters that you have defined actions for in the defineBehaviour() method above.
  `myEvolution.evolve(500)` will simulate the evolution for 500 generations.
  Finally, to see the results of the evolution, `myEvolution.getCurrentGeneration()` will return the current generation (500th in this case) and `myEvolution.printGen(List<Evolver> gen)` will print out a list of DNA sequences and their corresponding fitnesses. You are encouraged to make your own function to analyze the current generation. 

### More details

####Evolution configuration values

There are many more parameters that can change the course of evolution. These will need to be fine tuned for your use case. In the quick start example above, `myEvolution.evolve(500)` is called. This evolves with default parameters, and is an easy way to start off. Alternatively, for more control, the following can be used.

```	public void evolve(int numGenerations, double survivalConstant, double individualMutationRate, double dnaMutationRate, double crossOverRate) {} ```

1. `numGenerations`: this is the same as the simplified version of evolve(). It sets how many generations to simulate for.

2. `survivalConstant`: This determines to what extent a high performing individual will survive in the next generation. In essence, after each generation, the evolvers are assessed and ordered in terms of fitness. The probability of surviving to the next generation increases with increasing fitness, however this constant determines what slice of the survival pie the less/more fit individuals get. The highest survival constant (1) will mean that the next generation is ONLY populated by variants of the MOST fit individual from the last generation. As you decrease from 1, there is more probability that lower fitness individuals will also sneak into the next generation. Although this may seem counterintuitive, it promotes diversity which is an important element in the workings of an evolutionary algorithm.

3. `individualMutationRate`: After the individuals surviving into the next generation are determined, a hypothetical dice is rolled for each one to determine if it will be subject to mutation. This parameter determines the probability that each individual would undergo mutation.

4. `dnaMutationRate`: In the specific individuals that do undergo mutation (as determined by individualMutationRate), this mutation rate determines the probability that each individual 'character' in the DNA strand would mutate into another character.

5. `crossOverRate`: Again, looking at the individuals that survive into each round. They are randomly arranged into pairs, and for each pair, more theoretical dice are thrown to determine whether there will be gene crossover between the two. This parameter determines the probability that 2 individuals will undergo crossover. 

Understanding how these parameters work and adjusting them for your needs is imperative to optimizing the results of the evolutionary algorithm.

It may be cumbersome to keep inputting all those parameters in the evolve method. If the same values will be used throughout an entire project, it is encouraged to set the values as default values via:

```	public void setDefaultEvolutionParameters(double survivalConstant, double individualMutationRate, double dnaMutationRate, double crossOverRate) {} ```

Once the values are set, using `evolve(int numGenerations)` will automatically use these values.

##Building the Project

This repository is responsible for generating the library. If you want to contribute to improving aspects of the algorithm follow these instructions to build the project.

1. This project is built using Maven build manager. Install <a href="https://maven.apache.org/index.html">Maven</a>

2. To produce the JAR file, run the following command in the root directory
  ```$ mvn clean install```

3. The JAR will be generated in the \target folder
