import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.util.LinkedList;

/**
 * A chasing agent that targets other boids.
 * Skeleton code
 * 
 * @version 1.0
 */

public class ModelPredator extends Follower {
    /**
     * Class information
     */

    /** Tag for XML element */
    static final String XML_NAME = "model-predator";

    /** Can go forward up to 6 pixels per step */
    static final double PREDATOR_MAX_SPEED = 6;

    /** Record that allows XML files to set ModelPredator defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                FOLLOWER_SIZE,
                PREDATOR_MAX_SPEED,
                FOLLOWER_MIN_SPEED,
                FOLLOWER_MAX_ACCEL,
                FOLLOWER_MAX_DECEL,
                HALF_CIRCLE,
                0, HALF_CIRCLE, Color.RED,
                false, false);

    /** Record that allows XML files to set follower defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * Constructor - initialize general agent fields to describe
     * agent that will follow other peaceful agents and eat them.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    public ModelPredator(World w, int id, Attributes atts, Locator loc) 
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * this follower.
     * 
     * @param out an open file to write to, wrapped in BufferedWriter 
     *            convenience class
     */
    public void log(BufferedWriter out) 
    throws IOException {
        out.write("   <" + XML_NAME + " " + ID_PARAM + OPEN + Integer.toString(id) + CLOSE +
        "\n     ");
        form.log(out);
        out.write("    ");
        status.log(out);
        out.write("    />\n");
    }

    /** 
     * Predators attack their prey!
     * 
     * @param neighbor A category of agent nearby
     * @return Agent's disposition for what to do with another agent
     */
    @Override
    public InteractiveBehavior behaviorOnApproach(Percept.ObjectCategory neighbor) {
        if (neighbor == Percept.ObjectCategory.BOID)
            return InteractiveBehavior.ATTACK;
        else
            return InteractiveBehavior.COEXIST;
    }

    /**
     * Predators look scary!  While alive,
     * they look like predators.
     * 
     * @return appearance PREDATOR as Percept.ObjectCategory
     *         (or CORPSE if dead)
     */
    @Override
    public Percept.ObjectCategory looksLike() {
        if (isAlive)
            return Percept.ObjectCategory.PREDATOR;
        else
            return Percept.ObjectCategory.CORPSE;
    }

    /********************************************************************
     * 
     * Code block for solution to behavior generation.
     * 
     */
    /**
     * Scan all percepts, and see which ones interception distance is the shortest, pick that target
     * and create a new percept with those coordinates
     */
    
    protected Percept bestTarget(List<Percept> ps) {
        //Figure out which boid is the closest, then go after it.
    	
    	int i = 0;
    	Percept target = null; // this is the best target to go after
    	double shortestdist = 0;
    	double alpha = 0;
    	double beta = 0;
    	double intdist = 0;
    	Percept intercept = null;
    	while (i < ps.size()) {
    		
    		//System.out.println("stats: " + ps.get(i).getDistance());
    		beta = ps.get(i).getOrientation() - ps.get(i).getAngle();
    		alpha = Math.asin((ps.get(i).getSpeed()*Math.sin(beta)) / PREDATOR_MAX_SPEED );
    		
    		//now calculate length of intercept path, and see it if is shorter than others...
    		intdist = ps.get(i).getDistance()/Math.cos(alpha);
    		if((target == null || (intdist < shortestdist)) && ps.get(i).getObjectCategory() == Percept.ObjectCategory.BOID) {
    			target = ps.get(i);
    			shortestdist = intdist;
    			
    			//create new percept which is the point of interception, treat it as an imaginary boid
    			intercept = new Percept(target.getObjectCategory(), target.getColor(),shortestdist,
    	    			target.getAngle() + alpha,0,target.getSpeed() );
    		}
    		i++;
    		
    	}
    	
    	
    	return intercept;
    	//in short, our predator will "reactivly" chase the closest interception point
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
     * Specialized target method: helps define chasing behavior
     */
    @Override
    public boolean isTarget(Percept p) {
        // TBC: change this if you want the predator to behave differently  
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
        // TBC: change this if you want the predator to behave differently  
        Percept closestSeen = bestTarget(ps);
        
        todo = new LinkedList<Intention>();

        if (closestSeen != null) {
            steerTo(closestSeen);
        }
    }
    
}
