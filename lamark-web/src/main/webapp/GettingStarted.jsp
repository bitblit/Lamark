<div id="current" class="post">
    <h2 class="title">Lamark - A Java Genetic Algorithm Processor</h2>

    <div class="story">
        <h1>Introduction to genetic algorthims</h1>

        <p>
            A <em>Genetic Algorithm</em> is a search algorithm used to attempt to solve optimization
            problems by use of operators based on genetic principles. Optimization problems are those
            that, due to their nature, are usually impossible to solve efficiently, but possible to find
            a good approximation of the best value. Such problems are typically NP-Complete/NP-Hard.
        </p>

        <p>
            A typical genetic algorithm consists of several parts (herein called components), which are
            tailored to the specific problem. The functioning of the algorithm, given these components,
            is consistent across problem domains. Solutions to a given problem are modeled as Genomes (herein
            referred to as 'Individuals'), whos competitiveness in the space is determined by a "fitness
            function".
        </p>

        <p>
            NOTE: This page is not a complete introduction to what is a large topic; it assumes a large amount
            of familiarity with the terrain. If you lack this, try one of these books:
        <ul>
            <li>
                <a href="http://www.amazon.com/Introduction-Genetic-Algorithms-Complex-Adaptive/dp/0262631857/ref=pd_bbs_sr_1?ie=UTF8&s=books&qid=1195895728&sr=1-1">Melanie
                    Mitchell : An Introduction to Genetic Algorithms</a></li>
            <li>
                <a href="http://www.amazon.com/Genetic-Algorithms-Optimization-Machine-Learning/dp/0201157675/ref=pd_bbs_sr_2?ie=UTF8&s=books&qid=1195895728&sr=1-2">David
                    Goldberg : Genetic Algorithms in Search, Optimization, and Machine Learning</a></li>
        </ul>
        </p>
        <h1>Parts of a GA</h1>

        <p>

        <h3>Suppliers</h3>
        A <em>Supplier</em> is a class used to create new Individuals, typically randomly. How the creation
        is performed is problem dependant, and can range from very simple (e.g., pick a random number and use
        it as the genome) to the quite complex (select a 100-integer permutation and use it as the genome).
        </p>
        <p>

        <h3>Crossovers</h3>
        A <em>Crossover</em> is a class that takes some number of "parent" individuals and from them produces
        a single "child" genome. In the spirit of the GA, the child genome should share all of it's genetic
        material with its parents. By convention, there are typically 2 parents in a crossover, but this
        is not a hard-and-fast rule.
        </p>
        <p>

        <h3>Mutators</h3>
        A <em>Mutator</em> is a class that takes an individual and changes it in a random way. Mutation is
        typically used to free the GA from a local minima/maxima in the problem space, however, a GA that
        relies too heavily on mutation to find solutions is essentially performing a random walk of the
        problem space and cannot be considered efficient.
        </p>
        <p>

        <h3>Fitness Functions</h3>
        A <em>Fitness Function</em> determines how "good" a given individual is, assigning them a number that
        represents this goodness. This number may be higher or lower, depending on whether the problem is a minimizing
        or maximizing problem. A canonical example of a fitness function is the length of the path on a Travelling
        Salesman Problem (a minimizing function).
        </p>
        <p>

        <h3>Selectors</h3>
        A <em>Selector</em> is used by the GA to choose which individuals will be used for crossover. Of all the
        components, the selector is the least dependant on the problem space and as such the common selectors provided
        by Lamark can typically be used without modification.
        </p>
        <h1>Parameters</h1>

        <p>

        <h3>maximumPopulations</h3>
        A cap on the number of generations a GA will run before stopping. Leave NULL to let the GA run forever (or till
        aborted)
        <h3>populationSize</h3>
        The number of individuals in a given population
        <h3>upperElitism</h3>
        The percentage of a population that should be retained across generations. Always taken from the top (the best
        are retained)
        <h3>lowerElitism</h3>
        The percentage of a population that should be simply discarded and replaced with random Individuals the next
        generation. Use
        sparingly, as lowerElitism is simply a strong form of mutation
        <h3>crossoverProbability</h3>
        The likelihood that a crossover will be performed. Typically 1 (100%). If crossover doesnt occur, a copy of the
        selected
        individual will instead be used.
        <h3>mutationProbability</h3>
        The likelihood of a individual mutating. Typically about .001. Individuals retained via upper elitism are immune
        to
        mutation.
        <h3>numberOfWorkerThreads</h3>
        Lamark will always use one thread for the main process, and some number of pooled threads for the processes of
        the various
        components (such as creation, crossover, and calculating fitness). This should typically be equal to the number
        of processors in the machine (what
        is returned by Runtime.availableProcessors
        <h3>targetScore</h3>
        A score which, when reached, the GA should stop. Typically used in cases where a "best-case" scenario is already
        known. Leave null
        to allow the GA to run forever
        <h3>trackParentage</h3>
        Determines whether Lamark will keep track of the parentage of individuals. Allows calculation of parenting
        trees. Use with
        care, as every individual will be kept in memory which can lead to dramatic memory consumption.
        <h3>abortOnUniformPopulation</h3>
        If true, stops Lamark if it ever reaches a state where all members of the population are the same (according to
        java's .equals method
        <h3>randomSeed</h3>
        A long value used to initialize the GA. Can be set explicitly to allow reproducible runs of the GA
        </p>
        <h1>Listeners</h1>

        <p>
            Lamark is meant to be run in highly multithreaded (and distributed) environments. Therefore, all
            communication is performed by means
            of listeners and events. Any object can register itself as a listener for lamark components by implementing
            LamarkEventListener and
            then adding itself as a listener.

        <p>
            Potential future topics for this page: <em>Should Lamark recieve wide(r) use, more may
            be written on the following topics:</em>
        <ul>
            <li>Using LamarkFactory</li>
            <li>Using LamarkGUI</li>
            <li>Using Spring to configure Lamark</li>
            <li>Lamark Recursively: Using Lamark within a Lamark Component</li>
        </ul>
        </p>
    </div>
</div>

</div>
	
