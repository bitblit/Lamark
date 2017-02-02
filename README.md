Lamark
======

A Java based library for executing genetic algorithms


Notes and Caveats
-----------------
First and foremost: Yes, I know that Lamarck has a 'c' in it.  It is this way on purpose.  Sue me.

Lamark is a library I wrote back in 2006 when I was contemplating doing my masters thesis on music generated via genetic
algorithm.  Fast-forward to 2014 - I've long ago finished my masters thesis ([on a different topic, as it turns out]
(http://books.google.com/books/about/An_Improved_Algorithm_for_Deinterlacing.html?id=W41unQEACAAJ), buy your
copy today!) and was looking for some library I've written that's reasonably complete and I wouldn't be ashamed of, such
that I can practice doing releases into the public Maven repository... and this is the one I chose.

As Java-based GA processors go, Lamark isn't too bad - it supports generics, plugs in pretty easily with Spring, and
even has some attachments for running the browser as a plugin or distributing via a custom HTTP protocol.  Having said
that, if you are actually doing research, you may wanna look at these competitors too:
* [Watchmaker](http://watchmaker.uncommons.org/)
* [Jenes](http://sourceforge.net/projects/jenes/)
* [Jenetics](http://jenetics.sourceforge.net/)
* [Java-GaLib](http://sourceforge.net/projects/java-galib/)
* [ECJ](http://cs.gmu.edu/~eclab/projects/ecj/)
* [Apache Math/Genetic Algos](http://commons.apache.org/proper/commons-math/userguide/genetics.html)

The main thing I'm trying to accomplish, though, is allow you to add Lamark to your project by using Maven - so
you'll know I've hit my goal when you see the Maven import listed below:

> Nope!
> It is simply
> Not here yet
> But I'm working on it!

Introduction to Genetic Algorithms
----------------------------------
A _Genetic Algorithm_ is a search algorithm used to attempt to solve optimization
problems by use of operators based on genetic principles. Optimization problems are those
that, due to their nature, are usually impossible to solve efficiently, but possible to find
a good approximation of the best value. Such problems are typically NP-Complete/NP-Hard.

A typical genetic algorithm consists of several parts (herein called components), which are
tailored to the specific problem. The functioning of the algorithm, given these components,
is consistent across problem domains. Solutions to a given problem are modeled as Genomes (herein
referred to as 'Individuals'), whos competitiveness in the space is determined by a "fitness
function".

NOTE: This page is not a complete introduction to what is a large topic; it assumes a large amount
of familiarity with the terrain. If you lack this, try one of these books:
* <a href="http://www.amazon.com/Introduction-Genetic-Algorithms-Complex-Adaptive/dp/0262631857/ref=pd_bbs_sr_1?ie=UTF8&s=books&qid=1195895728&sr=1-1">Melanie
    Mitchell : An Introduction to Genetic Algorithms</a></li>
* <a href="http://www.amazon.com/Genetic-Algorithms-Optimization-Machine-Learning/dp/0201157675/ref=pd_bbs_sr_2?ie=UTF8&s=books&qid=1195895728&sr=1-2">David
    Goldberg : Genetic Algorithms in Search, Optimization, and Machine Learning</a></li>

Parts of a GA
-------------

### Suppliers

A _Supplier_ is a class used to create new Individuals, typically randomly. How the creation
is performed is problem dependant, and can range from very simple (e.g., pick a random number and use
it as the genome) to the quite complex (select a 100-integer permutation and use it as the genome).

### Crossovers

A _Crossover_ is a class that takes some number of "parent" individuals and from them produces
a single "child" genome. In the spirit of the GA, the child genome should share all of it's genetic
material with its parents. By convention, there are typically 2 parents in a crossover, but this
is not a hard-and-fast rule.

### Mutators

A _Mutator_ is a class that takes an individual and changes it in a random way. Mutation is
typically used to free the GA from a local minima/maxima in the problem space, however, a GA that
relies too heavily on mutation to find solutions is essentially performing a random walk of the
problem space and cannot be considered efficient.

### Fitness Functions

A _Fitness Function_ determines how "good" a given individual is, assigning them a number that
represents this goodness. This number may be higher or lower, depending on whether the problem is a minimizing
or maximizing problem. A canonical example of a fitness function is the length of the path on a Travelling
Salesman Problem (a minimizing function).

### Selectors

A _Selector_ is used by the GA to choose which individuals will be used for crossover. Of all the
components, the selector is the least dependant on the problem space and as such the common selectors provided
by Lamark can typically be used without modification.

Parameters
----------

### maximumPopulations
A cap on the number of generations a GA will run before stopping. Leave NULL to let the GA run forever (or till
aborted)
### populationSize
The number of individuals in a given population
### upperElitism
The percentage of a population that should be retained across generations. Always taken from the top (the best
are retained)
### lowerElitism
The percentage of a population that should be simply discarded and replaced with random Individuals the next
generation. Use
sparingly, as lowerElitism is simply a strong form of mutation
### crossoverProbability
The likelihood that a crossover will be performed. Typically 1 (100%). If crossover doesnt occur, a copy of the
selected
individual will instead be used.
### mutationProbability
The likelihood of a individual mutating. Typically about .001. Individuals retained via upper elitism are immune
to
mutation.
### numberOfWorkerThreads
Lamark will always use one thread for the main process, and some number of pooled threads for the processes of
the various
components (such as creation, crossover, and calculating fitness). This should typically be equal to the number
of processors in the machine (what
is returned by Runtime.availableProcessors
### targetScore
A score which, when reached, the GA should stop. Typically used in cases where a "best-case" scenario is already
known. Leave null
to allow the GA to run forever
### trackParentage
Determines whether Lamark will keep track of the parentage of individuals. Allows calculation of parenting
trees. Use with
care, as every individual will be kept in memory which can lead to dramatic memory consumption.
### abortOnUniformPopulation
If true, stops Lamark if it ever reaches a state where all members of the population are the same (according to
java's .equals method
### randomSeed
A long value used to initialize the GA. Can be set explicitly to allow reproducible runs of the GA

Listeners
---------

Lamark is meant to be run in highly multithreaded (and distributed) environments. Therefore, all
communication is performed by means of listeners and events. Any object can register itself as a listener
 for lamark components by implementing LamarkEventListener and then adding itself as a listener.

Potential future topics for this page: _Should Lamark recieve wide(r) use, more may
be written on the following topics:_

* Using LamarkFactory
* Using LamarkGUI
* Using Spring to configure Lamark
* Lamark Recursively: Using Lamark within a Lamark Component


Bundling into JAR Files
-----------------------
Lamark allows you to bundle all of your classes, along with the configuration file, into a single JAR file (this is
required if you are going to use the distributed form).  Please note if are going to load resources from that JAR
file, Lamark will make your jar file the context classpath.  Therefore you should load resources like so:

Thread.currentThread().getContextClassLoader().getResourceAsStream(myResource);

Rather than

getClass().getResourceAsStream(myResource)



