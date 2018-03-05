###Evolutionary rocket engine nozzle generator

This is a project based on the AeroSim project, from end of 2014/beginning of 2015. It generates a symmetric bunch of walls to the aerodynamic simulation, around a particle source. There's 100 of these bunches, each of which is tested and evaluated by how much it accelerates the gas sideways. Best ones are kept and multiplied, bad ones discarded. 

The final effect is that some kind of rocket nozzle will be generated: it typically generates a pressure chamber and some kind of system to direct the escaping gas to one direction.

There's some bugs in the simulation: a particle can bounce from walls at most 3 times during a single frame. This means that the optimal strategy involves a completely closed chamber for maximum pressure, so that a relatively small amount of high-velocity gases maximize the fitness function.