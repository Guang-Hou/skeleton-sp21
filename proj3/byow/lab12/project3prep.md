# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:
The difference is on the anchored point. I chose the anchor point to the first row's most left element.
The provided implementation used the top left corner in the square containing the hexagon.
The provided implementation used recursion whereas I used iterative way drawing row by row.
-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: The hexagon is one tile (room or hallway). The tesselation is like exploring the different possibilities in all directions.


-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:
Draw rooms and hallways.
-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:
A hallway connects to multiple rooms and a room connects to one or more hallways. 
They are both bounded by walls. 
