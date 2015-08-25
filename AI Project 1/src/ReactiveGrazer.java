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
 * A following agent that is interested in three different
 * kinds of target and alternates between them.
 * Version without memory, just using cues from the 
 * environment.
 * Skeleton code.
 * 
 * @version 1.0
 */
public class ReactiveGrazer extends Follower {
    /**
     * Class information
     */
    
    /** Tag for XML element */
    static final String XML_NAME = "reactive-grazer";
    static final Color red = new Color(255, 100,100);
    static final Color green = new Color(100, 255,100);
    static final Color blue = new Color(100, 100,255);
    
    /** Record that allows XML files to set ReactiveGrazer defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                FOLLOWER_SIZE,
                FOLLOWER_MAX_SPEED,
                FOLLOWER_MIN_SPEED,
                FOLLOWER_MAX_ACCEL,
                FOLLOWER_MAX_DECEL,
                HALF_CIRCLE,
                0, HALF_CIRCLE, new Color(0, 80, 0),
                false, false);

    /** Record that allows XML files to set ReactiveGrazer defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /** XML attribute for how close agent comes to lights @see threshold */
    static final String THRESHOLD_PARAM = "threshold";
    
    /** How close agent comes to lights by default @see threshold */
    static double defaultThreshold = 30;

    /**
     * Use the XML data given by atts to update the default
     * parameters for creating new instances of the ReactiveGrazer
     * 
     * @param atts SAX attributes corresponding to XML data
     * @param loc file information for error messages
     * @throws SAXException if data is formatted wrong
     */
    static public void updateDefaults(Attributes atts, Locator loc)
    throws SAXException {
        defaultDynamicAgentAttributes.update(atts, loc);
        defaultFixedAgentAttributes.update(atts, loc);
        defaultThreshold = FlockingReader.getDoubleParam(atts, THRESHOLD_PARAM, defaultThreshold, loc);
    }
    
    /** How close should agent come to one target before moving to next */
    double threshold;

    /** Store the target here for visualization purposes */
    Percept lastTarget;

    /**
     * Constructor - initialize general agent fields to describe
     * grazer that alternates between seeking red lights, 
     * seeking green lights, and seeking blue lights.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    public ReactiveGrazer(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);   
        threshold = FlockingReader.getDoubleParam(atts, THRESHOLD_PARAM, defaultThreshold, loc);
        lastTarget = null;
    }

    /**
     * Output an XML element describing the current state of
     * this grazer.
     * 
     * @param out an open file to write to, wrapped in BufferedWriter 
     *            convenience class
     */
    public void log(BufferedWriter out) 
    throws IOException {
        out.write("   <" + XML_NAME + " " + ID_PARAM + OPEN + Integer.toString(id) + CLOSE +
                THRESHOLD_PARAM + OPEN + Double.toString(threshold) + CLOSE +
        "\n     ");
        form.log(out);
        out.write("    ");
        status.log(out);
        out.write("    />\n");
    }

    /********************************************************************
     * 
     * Code block for solution to behavior generation.
     * 
     */
    
    

	/**
	 * There are only certain situations the reactive grazer should be in
	 * 1.  Grazer may or may not be in threshold, and not facing any light
	 * 		-implies grazer is starting out, should target closest light, and proceed only forward
	 * 
	 * 2. Grazer is not in any threshold, but is facing a light
	 * 		-Grazer is on the way to a light, proceed forward only
	 * 
	 * 3. grazer is in threshold
	 * 		-PUSH IT OUT LIKE A REACTIVE FORCE
	 * 		-grazer has successfully reached a source, point grazer towards next light, and send directly forward
	 * 		-basically, if a grazer is in a threshold, and the light is a certian color, constantly steer the grazer in the correct direction
	 * 		  untill it has left the threshold, then condition 2 will take over.
	 * 
	 * 
	 */
    
    protected Percept bestTarget(List<Percept> ps) {
       //NEVER uses the variable LastTarget or any global state of any sort, 
    	//uses only information about the enviornment
    	
    	int i = 0;
    	int j = 0;
    	Percept target = null; // this is the best target to go after
    	Percept potential = null;
    	Color nextcolor = null;
    	Percept highprefTarget = null;
    	while (i < ps.size()) {
    		//System.out.println("I = " + + i + ", angle = " + ps.get(i).getAngle());
    		//first check if we are in the threshold
    		if(ps.get(i).getDistance() <= threshold) {
    			//if we are, depending on the color, turn toward the next closest target light
    			//System.out.println("test ");
    			if(ps.get(i).getColor().getRGB()==red.getRGB()) {
    				nextcolor = green;
            	}
            	else if(ps.get(i).getColor().getRGB()==green.getRGB()) {
            		nextcolor = blue;
            	}
            	else {
            		nextcolor = red;
            	}
    			//System.out.println("nextcolor is " + nextcolor.getRGB());
    			
    			//now find next closest light of that color, and proceed towards it
    			while (j < ps.size())  {
    				//System.out.println("j = " + j +", and RGB is " + ps.get(j).getColor().getRGB());
    				
   
    					if(ps.get(j).getColor().getRGB() == nextcolor.getRGB()
    							&&
    							(ps.get(j).getObjectCategory() == Percept.ObjectCategory.LIGHT)) {
    						if ((potential == null || (potential.getColor().getRGB() == nextcolor.getRGB()
    								&& ps.get(j).getDistance() < potential.getDistance()))){
    							//System.out.println("0");
    							potential = ps.get(j);
        					
    						}
    						
    					}
    					
    				j++;	
    	    	}
    			return potential;
    	    		
    	    		
    				
    			
    		}
    		
    		// if we are not in the threshold, then see if boid is facing a light, 
    		// if it is, just keep going towards it
    		
    		// this system deals with extremely small numbers, we must give it a tolerance
    		else if(-.01 <= ps.get(i).getAngle() && ps.get(i).getAngle() <= .01) {
    			//System.out.println("1");
    			highprefTarget = ps.get(i);
    			//return target;
    		}
    		
    		//or else we are in an initial state, now we should pick the closest light
    		else if((target == null || (ps.get(i).getDistance() < target.getDistance()))
    				&&
    				(ps.get(i).getObjectCategory() == Percept.ObjectCategory.LIGHT)) {
    			//System.out.println("2");
    			target = ps.get(i);
    			
    		}
    		
    		else {
    			//System.out.println("i = " + i);
    		}
    		
    		
    		i++;
    		
    	}
    	if(highprefTarget == null) {
    		return target;
    	}
    	return highprefTarget;
    	
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
        // TBC: change this if you want the grazer to behave differently  
        return false;
    }

    /**
     * Specialized target method: helps define chasing behavior
     * Default version copied directly from superclass Follower
     */
    @Override
    public double targetCost(Percept p) {
        // TBC: change this if you want the grazer to behave differently  
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
        // TBC: change this if you want the grazer to behave differently 
    	
    	/**
    	 * There are only certain situations the reactive grazer should be in
    	 * 1.  Grazer may or may not be in threshold, and not facing any light
    	 * 		-implies grazer is starting out, should target closest light, and proceed only forward
    	 * 
    	 * 2. Grazer is not in any threshold, but is facing a light
    	 * 		-Grazer is on the way to a light, proceed forward only
    	 * 
    	 * 3. grazer is in threshold and facing light
    	 * 		-grazer has successfully reached a source, point grazer towards next light, and send directly forward
    	 * 		-basically, if a grazer is in a threshold, and the light is a certian color, constantly steer the grazer in the correct direction
    	 * 		  untill it has left the threshold, then condition 2 will take over.
    	 * 
    	 * 
    	 * 
    	 * THEREFORE
    	 * 
    	 * deliberate will call bestTarget, which will analyze the 3 above conditions based on the state
    	 *  of stricly the enviornment
    	 *  
    	 *  then simply steer and go there
    	 *
    	 */
        Percept reactiveLoc = bestTarget(ps);
        
        todo = new LinkedList<Intention>();

        if (reactiveLoc != null) {
            steerTo(reactiveLoc);
        }
    }
}
