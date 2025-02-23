
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import javax.swing.*;

class City {

    //This class represents a city in a graph, designed for use in a TSP context
    private final int x;// x-coordinate of the city
    private final int y;//y-coordinate of the city
    private final int id;//A unique identifier for the city.
    private final Map<City, List<Point>> roadPaths;
    //A map that stores the paths (as lists of Point objects)o other cities. 
    //The keys are the destination City objects, and the values are the List<Point> representing the route

    public City(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.roadPaths = new HashMap<>();//creates new hash maps for road paths
    }

    public void addRoadTo(City other, List<Point> path) {
        //Adds a road to another city, with the given path
        //It puts the other city as the key and the path (a List<Point>) as the value into the roadPaths map.
        // This allows defining specific routes between cities.
        roadPaths.put(other, path);
    }

    public List<Point> getRoadTo(City other) {
        //retrieves the road path to another city
        //If no path exists, it calls generateDefaultPath(other) to create a default path and returns that.
        return roadPaths.getOrDefault(other, generateDefaultPath(other));
    }

    private List<Point> generateDefaultPath(City other) {
        /*Generates a default road path to another city.
        It creates a list of Point objects representing the path.
        It adds the starting point (the city's own coordinates).
        It generates a random number of intermediate points (between 2 and 4). */

        List<Point> path = new ArrayList<>();
        path.add(new Point(x, y));

        Random rand = new Random();
        int numPoints = rand.nextInt(3) + 2;

        for (int i = 0; i < numPoints; i++) {
            int midX = x + (other.x - x) * (i + 1) / (numPoints + 1);
            int midY = y + (other.y - y) * (i + 1) / (numPoints + 1);

            midX += rand.nextInt(41) - 20;
            midY += rand.nextInt(41) - 20;
            /*It calculates the coordinates of these intermediate points by interpolating between the starting
         and ending coordinates, with some random offset to make the paths look less straight. */
            path.add(new Point(midX, midY));

            /*It adds the ending point (the coordinates of the other city).
Returns the generated path.
This is important, because if no road is manually added, this function creates a road. */
        }

        path.add(new Point(other.x, other.y));
        return path;
    }

    public double distanceTo(City other) {
        //Calculates the total distance of the road to another city.
        //It uses the getRoadTo(other) method to get the road path, 
        //and then sums up these distances to get the total distane of the road
        List<Point> path = getRoadTo(other);
        double distance = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            distance += Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        }

        return distance;//Returns the total distance.
    }

    //These are standard getter methods to access the x, y, and id values of the city
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }
}

class RoadNetwork {
//class creates a graph-like structure where cities are nodes and roads are edges.

    private final List<City> cities;//A list of City objects that the network connects.
    private final Random rand = new Random();
    //A Random object used for generating random numbers, crucial for creating random roads and paths.

    public RoadNetwork(List<City> cities)//Constructor
    {
        this.cities = cities;
        generateRoads();// method to create the road network.
    }

    private void generateRoads() {
        //This method generates the random roads between the cities.
//It uses nested loops to iterate through all pairs of cities.
        for (City city1 : cities) {
            for (City city2 : cities) {

                if (city1 != city2 //Checks if the cities are different
                        && rand.nextDouble() < 0.4) //to randomly decide whether to create a road between the cities.
                // This introduces randomness, making the network less predictable.
                // A 40% probability of a road existing between any two cities.
                {
                    List<Point> roadPath = generateRoadPath(city1, city2);
                    city1.addRoadTo(city2, roadPath);
                }
            }
        }
    }

    private List<Point> generateRoadPath(City from, City to) {//Generates a road path (a list of Point objects) between two cities.
        /*
         * function is very similar to the one found in the city class, but it has a larger random offset. 
         * This will create more winding roads.
          The 0.4 value in the generateRoads function, dictates the likely density of the road network.
          This class is a key component for setting up the TSP problem. It creates the environment in which the shortest path will be found.
         */
        List<Point> path = new ArrayList<>();
        //Creates a new ArrayList to store the points of the path
        path.add(new Point(from.getX(), from.getY()));

        int numPoints = rand.nextInt(3) + 2;//Generates a random number of intermediate points (between 2 and 4).
        for (int i = 0; i < numPoints; i++) {
            /*
            calculates the coordinates of the intermediate points by interpolating between the starting and ending coordinates,
             with a random offset to make the paths less straight. */


            int midX = from.getX() + (to.getX() - from.getX()) * (i + 1) / (numPoints + 1);
            int midY = from.getY() + (to.getY() - from.getY()) * (i + 1) / (numPoints + 1);

            //Adds a random offset to the x and y coordinates of the intermediate points, creating a more winding road. 
            midX += rand.nextInt(61) - 30;
            midY += rand.nextInt(61) - 30;

            path.add(new Point(midX, midY));
        }
        //Adds the ending point (the to city's coordinates) to the path.
        path.add(new Point(to.getX(), to.getY()));
        return path;
        //Returns the generated path.
    }
}

class TSP {
    //It provides two methods for finding a near-optimal tour: the nearest neighbor algorithm and the 2-opt local search algorithm

    /*Nearest Neighbor: A greedy algorithm that finds a relatively short tour quickly, but it may not be optimal.

    2-opt: A local search algorithm that iteratively improves a tour by swapping pairs of edges. It can find better solutions than the nearest neighbor algorithm, but it may get stuck in local optima.

    Traveling Salesperson Problem (TSP): A classic optimization problem that seeks to find the shortest possible route that visits each city exactly once and returns to the starting city.

    Heuristics: Algorithms that find good, but not necessarily optimal, solutions in a reasonable amount of time.

    Local Search: Algorithms that iteratively improve a solution by making small changes. */
    private final List<City> cities;
    private List<City> currentTour;
    private double currentDistance;

    public TSP(List<City> cities) { // constructor
        this.cities = new ArrayList<>(cities);
        this.currentTour = new ArrayList<>();
    }

    public List<City> nearestNeighbor() { //Implements the nearest neighbor heuristic

        /* Starts with the first city in the list of cities.
         * Repeatedly selects the nearest unvisited city and adds it to the tour.
         * Keeps track of visited cities using a boolean[] visited array.
         * Calculates the total distance of the tour using calculateTotalDistance().
         * Returns the resulting tour.
         * 
         * This is a greedy algorithm, and does not guarantee the optimal solution.
         */
        if (cities.isEmpty()) {
            return new ArrayList<>();
        }

        boolean[] visited = new boolean[cities.size()];//Keeps track of visited cities
        currentTour = new ArrayList<>(cities.size());

        City current = cities.get(0);
        currentTour.add(current);
        visited[0] = true;

        while (currentTour.size() < cities.size()) {
            double minDistance = Double.MAX_VALUE;
            int nextIndex = -1;

            for (int i = 0; i < cities.size(); i++) {
                if (!visited[i]) {
                    double distance = current.distanceTo(cities.get(i));
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextIndex = i;
                    }
                }
            }

            current = cities.get(nextIndex);
            currentTour.add(current);
            visited[nextIndex] = true;
        }

        calculateTotalDistance();
        return new ArrayList<>(currentTour);
    }

    public List<City> twoOpt() {
        /*
         * Implements the 2-opt local search algorithm.
         * Starts with the tour produced by the nearest neighbor algorithm or another initial tour.
         * Iteratively tries to improve the tour by swapping pairs of edges.
         * reverse(newTour, i, j): Reverses the order of cities between indices i and j in the tour.
         * If a swap results in a shorter tour, it updates the currentTour.
         * Continues until no further improvement is found.
         * Returns the improved tour.
         * 
     This is a local search algorithm that attempts to improve a previous solution.
         */
        boolean improved = true;
        while (improved) {
            improved = false;
            double bestDistance = calculateTotalDistance();

            for (int i = 0; i < currentTour.size() - 1; i++) {
                for (int j = i + 1; j < currentTour.size(); j++) {
                    List<City> newTour = new ArrayList<>(currentTour);
                    reverse(newTour, i, j);

                    double newDistance = calculateTourDistance(newTour);
                    if (newDistance < bestDistance) {
                        currentTour = newTour;
                        bestDistance = newDistance;
                        improved = true;
                    }
                }
            }
        }

        currentDistance = calculateTotalDistance();
        return new ArrayList<>(currentTour);
    }

    private void reverse(List<City> tour, int i, int j) {
        //Reverses the order of cities in a tour between indices i and j.
        //Uses Collections.swap() to efficiently swap cities.

        while (i < j) {
            Collections.swap(tour, i, j);
            i++;
            j--;
        }
    }

    public double calculateTotalDistance() {
        /*Calculates the total distance of the currentTour.
        Calls calculateTourDistance() to do the actual calculation.
        Updates the currentDistance member variable.
        Returns the total distance. */
        currentDistance = calculateTourDistance(currentTour);
        return currentDistance;
    }

    private double calculateTourDistance(List<City> tour) {
        /*Calculates the total distance of a given tour.
        Sums up the distances between consecutive cities in the tour.
        Adds the distance between the last city and the first city to complete the tour. */
        double distance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            distance += tour.get(i).distanceTo(tour.get(i + 1));
        }
        if (!tour.isEmpty()) {
            distance += tour.get(tour.size() - 1).distanceTo(tour.get(0));
        }
        return distance;
    }

    public double getCurrentDistance() {
        return currentDistance;// to return current distance
    }
}

class TSPVisualizer extends JPanel {
    //This class extends JPanel and is responsible for visually displaying the cities and the tour found by the TSP algorithm
    /*
    Visualization: Provides a visual representation of the TSP problem and its solutions.

    Animation: Animates the tour, showing the path taken by the algorithm.

    Scaling: Scales the coordinates of the cities to fit within the panel.

    Customization: Allows customization of city size and padding.

    Clear Drawing: Draws the road network and tour paths with different colors for clarity. 
    */
    
    private final List<City> cities;//The list of cities to display.

    private List<City> tour;// the current tour to visualize

    private int currentPathIndex = -1;//the index of the current path segment being animated

    private static final int CITY_SIZE = 10;//the size of the city markers

    private static final int PADDING = 50;//the padding around the visualisation

    private Timer animationTimer;// timer obj used for animating the tour

    public TSPVisualizer(List<City> cities) {//constructor

        this.cities = cities;
        this.tour = new ArrayList<>();
        setPreferredSize(new Dimension(800, 600));
    }

    public void updateTour(List<City> newTour) {
        this.tour = newTour;//Updates the tour with a new tour
        startAnimation();//to begin the animation.
    }

    private void startAnimation() {
        currentPathIndex = -1;//Resets 
        if (animationTimer != null) {
            animationTimer.cancel();//Cancels any existing animationTimer.
        }
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() //Creates a new Timer and schedules a TimerTask to run at fixed intervals (300ms).
        {
            @Override
            public void run() {
                currentPathIndex++;
                if (currentPathIndex >= tour.size()) {
                    animationTimer.cancel();
                }
                repaint();
            }
        }, 0, 300);
    }

    @Override
    protected void paintComponent(Graphics g) // Overrides the paintComponent() method to draw the visualization.
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;//object for drawing.

        //for antialiasing.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Calculates scaling factors to fit the cities within the panel.
        int maxX = cities.stream().mapToInt(City::getX).max().orElse(100);
        int maxY = cities.stream().mapToInt(City::getY).max().orElse(100);

        double scaleX = (getWidth() - 2.0 * PADDING) / maxX;
        double scaleY = (getHeight() - 2.0 * PADDING) / maxY;

        // Draw road network = Iterates through all pairs of cities and draws the roads between them using drawPath().
        g2d.setColor(new Color(220, 220, 220));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (City city1 : cities) {
            for (City city2 : cities) {
                if (city1 != city2) {
                    List<Point> roadPath = city1.getRoadTo(city2);
                    if (roadPath != null) {
                        drawPath(g2d, roadPath, scaleX, scaleY, false);
                    }
                }
            }
        }

        // Draw animated tour
        /* If the tour is not empty and currentPathIndex is valid, it draws the tour segments.
The current path segment being animated is drawn in red, while the rest are in blue. */
        if (!tour.isEmpty() && currentPathIndex >= 0) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < Math.min(currentPathIndex + 1, tour.size()); i++) {
                City current = tour.get(i);
                City next = tour.get((i + 1) % tour.size());
                List<Point> roadPath = current.getRoadTo(next);
                if (i == currentPathIndex) {
                    g2d.setColor(Color.RED);
                }
                drawPath(g2d, roadPath, scaleX, scaleY, true);
                g2d.setColor(Color.BLUE);
            }
        }

        // Draw cities
        /* The starting city is drawn in green, and the other cities are in red.
         Displays the city ID next to each marker. */
        for (City city : cities) {
            int x = PADDING + (int) (city.getX() * scaleX) - CITY_SIZE / 2;
            int y = PADDING + (int) (city.getY() * scaleY) - CITY_SIZE / 2;

            if (city == cities.get(0)) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval(x - 2, y - 2, CITY_SIZE + 4, CITY_SIZE + 4);
            } else {
                g2d.setColor(Color.RED);
                g2d.fillOval(x, y, CITY_SIZE, CITY_SIZE);
            }
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(city.getId()), x, y);
        }
    }

    private void drawPath(Graphics2D g2d, List<Point> path, double scaleX, double scaleY, boolean isTourPath) {
        /* Draws a path (a list of Point objects) as a series of lines.
Scales the coordinates of the points to fit within the pan */

        if (path == null || path.size() < 2) {
            return;
        }

        int[] xPoints = new int[path.size()];
        int[] yPoints = new int[path.size()];

        for (int i = 0; i < path.size(); i++) {
            Point p = path.get(i);
            xPoints[i] = PADDING + (int) (p.x * scaleX);
            yPoints[i] = PADDING + (int) (p.y * scaleY);
        }

        for (int i = 0; i < path.size() - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
    }
}

public class Main {
    /* This class is the entry point of the application. 
    It sets up the TSP problem, creates the GUI, and handles user interactions. */

    public static void main(String[] args) {

        List<City> cities = new ArrayList<>();// CREATE A LIST TO STORE CITY OBJECTS
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            cities.add(new City(i, rand.nextInt(700), rand.nextInt(500)));
        }//creates 20 cities with random x,y co-ordinates = (between 0 and 699 for x, and 0 and 499 for y)


        new RoadNetwork(cities);//object, which generates random roads between the cities.

        TSP tsp = new TSP(cities);// TSP Obj to solve TSP Problem

        TSPVisualizer visualizer = new TSPVisualizer(cities);// object to display the cities and tours

        JFrame frame = new JFrame("TSP Solver");
        //Creates a JFrame (window) for the GUI.

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        JButton nnButton = new JButton("Nearest Neighbor");
        JButton twoOptButton = new JButton("2-opt Improvement");
        JLabel distanceLabel = new JLabel("Distance: ");

        controlPanel.add(nnButton);
        controlPanel.add(twoOptButton);
        controlPanel.add(distanceLabel);

        nnButton.addActionListener(e -> //Disables the buttons to prevent multiple clicks.
        {
            nnButton.setEnabled(false);
            twoOptButton.setEnabled(false);
            List<City> nnTour = tsp.nearestNeighbor();//to run the nearest neighbor algorithm
            
            visualizer.updateTour(nnTour); //Updates the visualizer with the resulting tour.

            distanceLabel.setText(String.format("Distance: %.2f", tsp.getCurrentDistance()));
            //Updates the distanceLabel with the tour distance.

            Timer enableTimer = new Timer();//to re-enable the buttons after 6 seconds.
            enableTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    nnButton.setEnabled(true);
                    twoOptButton.setEnabled(true);
                }
            }, 3000);
        });

        twoOptButton.addActionListener(e -> {
            nnButton.setEnabled(false);
            twoOptButton.setEnabled(false);
            List<City> improvedTour = tsp.twoOpt();
            visualizer.updateTour(improvedTour);
            distanceLabel.setText(String.format("Distance: %.2f", tsp.getCurrentDistance()));
            Timer enableTimer = new Timer();
            enableTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    nnButton.setEnabled(true);
                    twoOptButton.setEnabled(true);
                }
            }, 6000);
        });

        frame.setLayout(new BorderLayout());//Sets the layout of the frame to BorderLayout
        frame.add(visualizer, BorderLayout.CENTER);
        //adds the visualizer to the center of the frame.
        
        frame.add(controlPanel, BorderLayout.SOUTH);
        //Adds the controlPanel to the south of the frame.

        frame.pack();//Packs the frame to fit its components.
        frame.setLocationRelativeTo(null);//Centers the frame on the screen.
        frame.setVisible(true);//Makes the frame visible.
    }
}
