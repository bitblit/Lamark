
Interactive Mode
================
1) Scan the classpath for all implementing methods
2) Allow user to select 1 of all required components, set values for all required values
2a) Requires that it has null arg constructor?  Or just that any constructor is annotated appropriately?
3) Class implementing (TBD) then instantiates from this
4) Run



Standard Mode
=============
Single class implementing (TBD), which has exactly 1 of all required components - instantiate and run


Network Mode
============
As in standard mode, but Jar containing single class downloaded from net
(validate version?)


System
======
Each run has:
* 1 each of Supplier, Crossover, FitnessFunction, Formatter, Mutator, Selector
* 0 or more ParamProviders


Use Cases
=========
Standard (Research) use case : we are solving a single problem which implies that the fitness function and supplier are
static, other things may change.

Sample use case : Selecting 