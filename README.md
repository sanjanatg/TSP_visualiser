# TSP Solver with Visualization

This Java application solves the Traveling Salesperson Problem (TSP) and visualizes the solution. It implements two common TSP algorithms: Nearest Neighbor and 2-opt improvement.

## Features

* **Visual Representation:** Displays cities and the tour graphically.
* **Algorithms:** Implements the Nearest Neighbor and 2-opt algorithms for solving the TSP.
* **Animation:** Animates the tour, highlighting the path taken.
* **Random Road Network:** Generates a random road network between cities.
* **GUI:** Provides a graphical user interface for easy interaction.

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 8 or later

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone [repository_url]
    cd [repository_directory]
    ```
2.  **Compile the Java files:**
    ```bash
    javac Main.java City.java RoadNetwork.java TSP.java TSPVisualizer.java
    ```
3.  **Run the application:**
    ```bash
    java Main
    ```

## Usage

1.  The application window will open, displaying the cities and the road network.
2.  Click the "Nearest Neighbor" button to run the Nearest Neighbor algorithm.
3.  Click the "2-opt Improvement" button to run the 2-opt algorithm.
4.  The tour will be animated, and the total distance will be displayed.
5.  The buttons will be re-enabled after a short delay.

## Code Structure

* **`Main.java`:** The entry point of the application. Sets up the GUI and controls the program flow.
* **`City.java`:** Represents a city with coordinates, an ID, and roads to other cities.
* **`RoadNetwork.java`:** Generates random roads between cities.
* **`TSP.java`:** Implements the TSP algorithms (Nearest Neighbor and 2-opt).
* **`TSPVisualizer.java`:** Displays the cities, roads, and tours graphically.

## Algorithms

* **Nearest Neighbor:** A greedy algorithm that starts at a city and repeatedly visits the nearest unvisited city.
* **2-opt:** A local search algorithm that iteratively improves a tour by swapping pairs of edges.

## Visualizer

The `TSPVisualizer` class extends `JPanel` and is responsible for drawing the cities, roads, and tours. It uses a `Timer` to animate the tour.

WORKFLOW 
![image](https://github.com/user-attachments/assets/fd257bfa-cb28-4b1f-8b56-caaac7cc059a)

CORE COMPONENTS<br>
![image](https://github.com/user-attachments/assets/6748e103-c20e-41c5-b05f-58de53071469)

GUI SNAPSHOTS<br>
![image](https://github.com/user-attachments/assets/122327a9-feb0-4a0f-be4e-a120f1b1bfd9)
![image](https://github.com/user-attachments/assets/78c5d229-5072-46d6-92c4-7ddcb6df5737)



