import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * An agent implementing a basic version of Reynold's
 * flocking algorithm from SIGGRAPH 1987.
 * Skeleton code.
 * 
 * @version 1.0
 */
public class Flocker extends Follower {

	
    /** Element name for entities of this type in XML documents */
    static final String XML_NAME = "flocker";

    /**
     * Distinctive static properties of a flocker
     */
    static class FlockerAttributes {

    	/** XML attribute for setting obstacle avoidance */
    	static final String AVOIDS_OBSTACLES_PARAM = "clear";
    	
        /** XML attribute for setting collision avoidance */
        static final String AVOIDS_COLLISIONS_PARAM = "evade";

        /** XML attribute for setting alignment with neighbors */
        static final String ALIGNS_WITH_NEIGHBORS_PARAM = "align";

        /** XML attribute for setting collision avoidance */
        static final String DOES_CENTERING_PARAM = "center";
        
        /** XML attribute for setting food following */
        static final String FOLLOWS_LIGHT_PARAM = "follow";

        /** XML attribute for distance at which obstacles are a worry */
        static final String CLEARANCE_DISTANCE_PARAM = "clearance";
        
        /** XML attribute for angle at which obstacles are a worry */
        static final String CLEARANCE_ANGLE_PARAM = "cone";
        
        /** XML attribute for setting minimum separation distance */
        static final String SEPARATION_DISTANCE_PARAM = "separation";

        /** XML attribute for setting distance at which agent starts to pay attention */
        static final String DETECTION_DISTANCE_PARAM = "detection";

        /** XML attribute for setting obstacle weight */	
        static final String OBSTACLE_WEIGHT_PARAM = "ow";
        
        /** XML attribute for setting separation weight */
        static final String SEP_WEIGHT_PARAM = "sw";

        /** XML attribute for setting alignment weight */
        static final String ALIGN_WEIGHT_PARAM = "aw";

        /** XML attribute for setting centering weight */
        static final String CTR_WEIGHT_PARAM = "cw";
        
        /** XML attribute for setting importance of following light */
        static final String LIGHT_WEIGHT_PARAM = "lw";

        /** Does the flocker avoid obstacles */
        boolean avoidsObstacles;
        /** How close must an obstacle be before collision is a worry */
        double clearance;
        /** How close ahead must an obstacle be before collision is a worry */
        double cone;
        /** How important is avoiding obstacles */
        double obstacleWeight;
        
        /** Does the flocker avoid collisions */
        boolean avoidsCollisions;
        /** How close must a neighbor be before collision is a worry */
        double separationDistance;
        /** How important is avoiding collision compared to other goals */
        double separationWeight;

        /** How close must other things be to affect boid's decisions */
        double detectionDistance;
        /** Does the boid try to fly in the same direction as neighbors? */
        boolean alignsWithNeighbors;
        /** How important is aligning with neighbors compared to other goals? */
        double alignmentWeight;

        /** Does the boid try to fly in the middle of its local group? */
        boolean doesCentering;
        /** How important is flying in the center compared to other goals? */
        double centeringWeight;

        /** Does the bird follow (and eat) light sources */
        boolean followsLight;
        /** How important is following light compared to other goals? */
        double followWeight;
        
       
        /**
         * Constructor from specific data
         * 
         * @param obstacles true if boid should avoid obstacles
         * @param collide true if boid should avoid collisions with neighbors
         * @param align true if boid should align heading with neighbors
         * @param center true if boid should stay in midst of neighbors
         * @param follow true if boid should follow lights
         * @param clearance distance threshold for obstacles
         * @param cone angle threshold for obstacles
         * @param separation distance threshold for evasive action
         * @param detection distance threshold for other action
         * @param ow strength of obstacle drive
         * @param sw strength of separation drive
         * @param aw strength of alignment drive
         * @param cw strength of centering drive
         * @param fw strength of light drive
         */
        FlockerAttributes(boolean obstacles, boolean collide, 
        		boolean align, boolean center, boolean follow,
                double clearance, double cone,
                double separation, double detection,
                double ow, double sw, double aw, double cw, double fw) {
            avoidsObstacles = obstacles;
            avoidsCollisions = collide;
            alignsWithNeighbors = align;
            doesCentering = center;
            followsLight = follow;
            this.clearance = clearance;
            this.cone = cone;
            separationDistance = separation;
            detectionDistance = detection;
            this.obstacleWeight = ow;
            separationWeight = sw;
            alignmentWeight = aw;
            centeringWeight = cw;
            followWeight = fw;
        }

        /**
         * Constructor from XML data
         * 
         * @param atts SAX attributes corresponding to XML data
         * @param defaults values to use when data is unspecified
         * @param locator file information for error messages
         * @throws SAXException if data has wrong format
         */
        public void set(Attributes atts, FlockerAttributes defaults, Locator locator)
        throws SAXException {
            avoidsObstacles = FlockingReader.getBoolParam(atts, AVOIDS_OBSTACLES_PARAM, defaults.avoidsObstacles, locator);
            avoidsCollisions = FlockingReader.getBoolParam(atts, AVOIDS_COLLISIONS_PARAM, defaults.avoidsCollisions, locator);
            alignsWithNeighbors = FlockingReader.getBoolParam(atts, ALIGNS_WITH_NEIGHBORS_PARAM, defaults.alignsWithNeighbors, locator);
            doesCentering = FlockingReader.getBoolParam(atts, DOES_CENTERING_PARAM, defaults.doesCentering, locator);
            followsLight = FlockingReader.getBoolParam(atts, FOLLOWS_LIGHT_PARAM, defaults.followsLight, locator);
            clearance = FlockingReader.getDoubleParam(atts, CLEARANCE_DISTANCE_PARAM, defaults.clearance, locator);
            double degrees  = FlockingReader.getDoubleParam(atts, CLEARANCE_ANGLE_PARAM, defaults.cone * RADIANS_TO_DEGREES, locator);
          	cone = World.clampToCircle(degrees * DEGREES_TO_RADIANS, 2 * Math.PI);
            separationDistance = FlockingReader.getDoubleParam(atts, SEPARATION_DISTANCE_PARAM, defaults.separationDistance, locator);
            detectionDistance = FlockingReader.getDoubleParam(atts, DETECTION_DISTANCE_PARAM, defaults.detectionDistance, locator);
            obstacleWeight = FlockingReader.getDoubleParam(atts, OBSTACLE_WEIGHT_PARAM, defaults.obstacleWeight, locator);
            separationWeight = FlockingReader.getDoubleParam(atts, SEP_WEIGHT_PARAM, defaults.separationWeight, locator);
            alignmentWeight = FlockingReader.getDoubleParam(atts, ALIGN_WEIGHT_PARAM, defaults.alignmentWeight, locator);
            centeringWeight = FlockingReader.getDoubleParam(atts, CTR_WEIGHT_PARAM, defaults.centeringWeight, locator);
            followWeight = FlockingReader.getDoubleParam(atts, LIGHT_WEIGHT_PARAM, defaults.followWeight, locator);
        }

        /**
         * Change parameters based on XML data
         * 
         * @param atts SAX attributes corresponding to XML data
         * @param locator file information for error messages
         * @throws SAXException if data has wrong format
         */
        public void update(Attributes atts, Locator locator)
        throws SAXException {
        	set(atts, this, locator);
        }

        /**
         * Write a complete XML description of the flocking boid
         * (in its current state) on the file corresponding to out
         * 
         * @param out where data should be written
         * @throws IOException if writes fail
         */
        public void log(BufferedWriter out)
        throws IOException {
        	double degrees = RADIANS_TO_DEGREES * cone;
        	
            out.write(
                    AVOIDS_OBSTACLES_PARAM + OPEN + Boolean.toString(avoidsObstacles) + CLOSE +
                    AVOIDS_COLLISIONS_PARAM + OPEN + Boolean.toString(avoidsCollisions) + CLOSE +
                    ALIGNS_WITH_NEIGHBORS_PARAM + OPEN + Boolean.toString(alignsWithNeighbors) + CLOSE +
                    DOES_CENTERING_PARAM + OPEN + Boolean.toString(doesCentering) + CLOSE +
                    FOLLOWS_LIGHT_PARAM + OPEN + Boolean.toString(followsLight) + CLOSE +
                    CLEARANCE_DISTANCE_PARAM + OPEN + Double.toString(clearance) + CLOSE +
                    CLEARANCE_ANGLE_PARAM + OPEN + Double.toString(degrees) + CLOSE +
                    SEPARATION_DISTANCE_PARAM + OPEN + Double.toString(separationDistance) + CLOSE +
                    DETECTION_DISTANCE_PARAM + OPEN + Double.toString(detectionDistance) + CLOSE +
                    OBSTACLE_WEIGHT_PARAM + OPEN + Double.toString(obstacleWeight) + CLOSE +
                    SEP_WEIGHT_PARAM + OPEN + Double.toString(separationWeight) + CLOSE +
                    ALIGN_WEIGHT_PARAM + OPEN + Double.toString(alignmentWeight) + CLOSE +
                    CTR_WEIGHT_PARAM + OPEN + Double.toString(centeringWeight) + CLOSE +
                    LIGHT_WEIGHT_PARAM + OPEN + Double.toString(followWeight) + CLOSE +
                    "\n");

        }

        /**
         * Constructor from XML data
         * 
         * @param atts SAX attributes defining flocker element
         * @param defaults what to do when attributes are unspecified
         * @param loc file information for error messages
         * @throws SAXException if data has wrong format
         */
        FlockerAttributes(Attributes atts, FlockerAttributes defaults, Locator loc) 
        throws SAXException {
            set(atts, defaults, loc);
        }
    }

    /** Should a flocker by default avoid obstacles */
    static final boolean AVOIDS_OBSTACLES = true;
    
    /** Should a flocker by default avoid collisions */
    static final boolean AVOIDS_COLLISIONS = true;

    /** Should a flocker by default align with neighbors */
    static final boolean ALIGNS_WITH_NEIGHBORS = true;

    /** Should a flocker by default try to maneuver to the center of the flock */
    static boolean DOES_CENTERING = true;
    
    /** Should a flocker follow lights? */
    static boolean FOLLOWS_LIGHT = true;

    /** By default, how close can individuals in the flock get before they get antsy */
    static final int DEFAULT_SEPARATION_DISTANCE = 50;

    /** By default, how much of the world does an individual in the flock attend to */
    static int DEFAULT_DETECTION_DISTANCE = 250;

    /** By default, when does an agent get worried about obstacles */
    static final double DEFAULT_CLEARANCE_DISTANCE = 140;
    
    /** By default, what headings count as an approaching crash */
    static final double DEFAULT_CLEARANCE_ANGLE = 60 * DEGREES_TO_RADIANS;
    
    /** By default, how hard do agents work to avoid obstacles */
    static final double OBSTACLE_WEIGHT = 2.0;
    
    /** By default, how hard do agents work to avoid collisions */
    static final double SEPARATION_WEIGHT = 2.0;

    /** By default, how hard do agents work to align with one another */
    static double ALIGNMENT_WEIGHT = 5.0;

    /** By default, how hard do agents work at centering */
    static double CENTERING_WEIGHT = 10.0;
    
    /** By default, how hard to agents work to follow lights */
    static double LIGHT_WEIGHT = 5.0;

    /** Record that allows XML files to set Flocker defaults */
    static FlockerAttributes defaultFlockerAttributes =
        new FlockerAttributes(AVOIDS_OBSTACLES,
        		AVOIDS_COLLISIONS, ALIGNS_WITH_NEIGHBORS,
                DOES_CENTERING, FOLLOWS_LIGHT,
                DEFAULT_CLEARANCE_DISTANCE,
                DEFAULT_CLEARANCE_ANGLE,
                DEFAULT_SEPARATION_DISTANCE,
                DEFAULT_DETECTION_DISTANCE,
                OBSTACLE_WEIGHT, SEPARATION_WEIGHT, 
                ALIGNMENT_WEIGHT, CENTERING_WEIGHT, LIGHT_WEIGHT);

    /** Record that allows XML files to set Flocker defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                FOLLOWER_SIZE,
                FOLLOWER_MAX_SPEED,
                FOLLOWER_MIN_SPEED,
                FOLLOWER_MAX_ACCEL,
                FOLLOWER_MAX_DECEL,
                FOLLOWER_MAX_TURN,
                0, HALF_CIRCLE, new Color(150, 0, 150),
                false, false);

    /** Record that allows XML files to set Flocker defaults
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * This is a convenience class that handles the
     * integration and visualization of control 
     * information from different sources
     */
    static class WeightedForce {
    	static final double PIXELS_PER_UNIT_WEIGHT = 8;
    	static final double ARROWHEAD_LENGTH = 10;
    	
    	/** need to change (forward or back) in direction of heading */
        public double fx;
        /** need to change (right or left) perpendicular to heading */
        public double fy;
        
        /** 
         * Constructor
         * @param x forward--backward force value
         * @param y right--left force value
         */
        public WeightedForce(double weight, double angle) {
            fx = weight * Math.cos(angle); 
            fy = weight * Math.sin(angle);
        }
        /**
         * Default constructor for zero force
         */
        public WeightedForce() {
        	fx = 0; fy = 0;
        }
        /**
         * @return overall strength of force
         */
        public double getWeight() {
            return Math.sqrt(fx * fx + fy * fy);
        }
        
        /**
         * @return direction of force relative to heading
         */
        public double getAngle() {
            return World.displacementOnCircle(0, Math.atan2(fy, fx), 2 * Math.PI);
        }
        
        /**
         * Update force to include new component
         * @param f additional force to take into account
         */
        public void addIn(WeightedForce f) {
        	fx += f.fx; fy += f.fy;
        }
        /**
         * Weight the force by a factor
         * @param factor multiplicative change to force
         */
        public void reweight(double factor) {
        	fx *= factor; fy *= factor;
        }
        
        /**
         * Draw on the screen
         * @param x center of drawing
         * @param y center of drawing
         * @param theta heading direction
         * @param world to control overlap
         * @param g for display attributes
         */
        public void draw(DynamicAgentAttributes a, World w, Graphics g) {
        	double segmentAngle = a.heading + getAngle();
        	double topArrowAngle = segmentAngle - 5 * Math.PI / 6;
        	double bottomArrowAngle = segmentAngle + 5 * Math.PI / 6;
        	double length = PIXELS_PER_UNIT_WEIGHT * getWeight();
        	double x1 = a.locX + length * Math.cos(segmentAngle);
        	double y1 = a.locY + length * Math.sin(segmentAngle);
        	w.drawLine((int)Math.round(a.locX), (int)Math.round(a.locY),
        			(int)Math.round(x1),(int)Math.round(y1), g);
        	w.drawLine((int)Math.round(x1), (int)Math.round(y1),
        			(int)Math.round(x1 + ARROWHEAD_LENGTH * Math.cos(topArrowAngle)),
        			(int)Math.round(y1 + ARROWHEAD_LENGTH * Math.sin(topArrowAngle)),
        			g);
           	w.drawLine((int)Math.round(x1), (int)Math.round(y1),
        			(int)Math.round(x1 + ARROWHEAD_LENGTH * Math.cos(bottomArrowAngle)),
        			(int)Math.round(y1 + ARROWHEAD_LENGTH * Math.sin(bottomArrowAngle)),
        			g);
        }
    }
    
    /** Decision making parameters for this boid's flocking behavior */
    FlockerAttributes flocking;

    /**
     * Constructor: initialize general agent fields to describe
     * a flocking agent.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    Flocker(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);
        flocking = new FlockerAttributes(atts, defaultFlockerAttributes, loc);
  }

    /**
     * Output an XML element describing the current state of
     * this boid.
     * 
     * @param out an open file to write to, wrapped in BufferedWriter 
     *            convenience class
     */
    @Override
    public void log(BufferedWriter out) throws IOException {
        out.write("   <" + XML_NAME + " " + ID_PARAM + OPEN + Integer.toString(id) + CLOSE + "\n     ");
        form.log(out);
        out.write("     ");
        status.log(out);
        out.write("     ");
        flocking.log(out);
        out.write("    />\n");
    }

    /**
     * Eat light sources
     * 
     * @param neighbor type of agent you've collided with
     * @return what you do with them
     */
    @Override
    public InteractiveBehavior behaviorOnApproach(Percept.ObjectCategory neighbor) {
    	if (neighbor == Percept.ObjectCategory.LIGHT) 
    		return InteractiveBehavior.ATTACK;
    	else
    		return InteractiveBehavior.COEXIST;
    }
    
    /********************************************************************
     * 
     * Code block for solution to behavior generation.
     * 
     */
    
    protected Percept bestTarget(List<Percept> ps) {
        //Figure out the target angle the boid should head with full force
    	
    	WeightedForce force = new WeightedForce(1,0);
    	WeightedForce alignavg = new WeightedForce(0,0);
    	WeightedForce centeravg = new WeightedForce(0,0);
    	
    	double sumangle = 0;
    	double sumdist = 0;
    	int i = 0;
    	int nearboids = 0;
    	Percept clossestLight = null;
    	//generic percept, that will send the boid forward at max speed
    	
    	
    	while (i < ps.size()) {
    		/**
    		 * 
    		 * Start with a new force with weight 1 and heading 0 (straight ahead)
				For of the five flocker priorities 
   				If the agent is sensitive to that priority
      			Calculate its force in the current situation
     	 		Add that force into the overall force
				Create the intention to move in the direction of the force, at full speed
    		 * 
    		 */
    		
    		//first check if the perception is of interest to avoid
    		if((ps.get(i).getObjectCategory() == Percept.ObjectCategory.PREDATOR 
    				||
    				ps.get(i).getObjectCategory() == Percept.ObjectCategory.OBSTACLE) && flocking.avoidsObstacles ){
    			
    			
    			//Now make sure it is within range
    			if(ps.get(i).getDistance() <= flocking.clearance
    					&&
    				ps.get(i).getAngle()  <= flocking.cone) {
    				
    				//ADD FORCES
    				double weight = (flocking.clearance * flocking.obstacleWeight/ps.get(i).getDistance());
	    			double angle = 0;
	    			if (Math.abs(ps.get(i).getAngle() - flocking.cone) >=  flocking.cone) {
	    				System.out.println("here");
	    				 if(Math.abs(ps.get(i).getAngle() - flocking.cone) <= 2*flocking.cone)angle = flocking.cone;
	    			}
	    			else {
	    				System.out.println("ugh");
	    				angle = flocking.cone * -1;
	    			}
	    			
	    			
	    			WeightedForce addedforce = new WeightedForce(weight,angle);
	    			
	    			force.addIn(addedforce);
    				
    			}
	    			
    		}
    		
    		//Now check to see if other boids are in range
    		else if (ps.get(i).getObjectCategory() == Percept.ObjectCategory.BOID) {
    			//Check if seperation is necessary
    			if((ps.get(i).getDistance() <= flocking.separationDistance) && flocking.avoidsCollisions) {
    				
    				//ADD FORCES
    				
    				double weight = (flocking.separationDistance * flocking.separationWeight/ps.get(i).getDistance());
	    			double angle = ps.get(i).getAngle() *-1;
	    			
	    			
	    			
	    			WeightedForce addedforce = new WeightedForce(weight,angle);
	    			
	    			force.addIn(addedforce);
    				
    			}
    			
    			// check if alignment/centering is necessary
    			else if (ps.get(i).getDistance() <= flocking.detectionDistance 
    					&& ps.get(i).getDistance() > flocking.separationDistance){
    				
    				nearboids++;
    				
    				sumangle += ps.get(i).getAngle();
    				sumdist += ps.get(i).getDistance();
    				
    				
    				
    				
    				
    				//sum up all forces then reweight by 1/n at the end of the loop
    				alignavg.addIn(new WeightedForce(flocking.alignmentWeight, ps.get(i).getOrientation()));
    				
    			}
    			
    			
    			
	    			
    		}
    		
    		//check if it is a light we need to be chasing
    		else if (ps.get(i).getObjectCategory() == Percept.ObjectCategory.LIGHT) {
    			
    			//figure out closest light first, add force at the end
    			if(clossestLight == null || (ps.get(i).getDistance() < clossestLight.getDistance())) {
    				clossestLight = ps.get(i);
        		}
        		
    			
    		}
    		
    		
    		
    		
    		i++;
    	}
    	// add average force for aligning birds, and then center
    	if(nearboids !=0) {
    		if(flocking.alignsWithNeighbors){
    			alignavg.reweight(1/nearboids);
    			force.addIn(alignavg);
    		}
        	
    		if(flocking.doesCentering){
	        	double avgangle = sumangle / nearboids ;
	        	double avgdist = sumdist/ nearboids;
	        	
	        	Percept centroid = new Percept(Percept.ObjectCategory.BOID, null, avgdist,
		    			avgangle, 0, Runner.RUNNER_MAX_SPEED );
	        	
	        	force.addIn(new WeightedForce(flocking.centeringWeight, centroid.getAngle()));
    		}
    	}
    	
    	
    	
    	//add force for nearest light
    	if(clossestLight != null && flocking.followsLight) {
	    	WeightedForce lightforce = new WeightedForce(flocking.followWeight,clossestLight.getAngle());
			force.addIn(lightforce);
    	}
    	
    	Percept point = new Percept(null, null,100,
    			force.getAngle(), 0,Runner.RUNNER_MAX_SPEED );
    	return point;
    }
    
    /**
     * Specialized drawing method in case you want debugging help
     */
    @Override
    public void draw(Graphics g) {
        // TBC: Debug visualization here
    	
        super.draw(g);
    }
    
    /**
     * What are you interested in?  Stub code says nothing
     *
     * @param p percept of potential target
     * @return whether it's interesting or not
     */
    @Override
    protected boolean isTarget(Percept p) {
    	// TBC: Fix this
	return false;
    }
    
    /**
     * Specialized target method: helps define chasing behavior
     * Default version copied directly from superclass Follower
     */
    @Override
    public double targetCost(Percept p) {
        // TBC: change this if you want the predator to behave differently  
        return p.getDistance();
    }
    
    /**
     * Specialized deliberate method: helps define chasing behavior.
     * Default version copied directly from superclass Follower.
     * 
     * @param ps A description of everything the agent can see
     */
    @Override
    public void deliberate(List<Percept> ps) {
        // calculate next move based on forces algorithm 
        Percept nextMove = bestTarget(ps);
        
        todo = new LinkedList<Intention>();

        if (nextMove != null) {
            steerTo(nextMove);
        }
    }
}
