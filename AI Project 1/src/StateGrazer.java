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
 * Uses memory to make sure that it follows first 
 * red lights, then green lights, then blue lights.
 * Skeleton code.
 * 
 * @version 1.0
 *
 */
public class StateGrazer extends Follower {
    /**
     * Class information
     */
    
	
	
	
    /** Tag for XML element */
    static final String XML_NAME = "state-grazer";
    static final Color red = new Color(255, 100,100);
    static final Color green = new Color(100, 255,100);
    static final Color blue = new Color(100, 100,255);
    
 
    /** Record that allows XML files to set StateGrazer defaults */
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

    /** Record that allows XML files to set StateGrazer defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /** XML attribute for how close agent comes to lights @see threshold */
    static final String THRESHOLD_PARAM = "threshold";
    
    /** How close agent comes to lights by default @see threshold */
    static double defaultThreshold = 30;

    /**
     * Use the XML data given by atts to update the default
     * parameters for creating new instances of the StateGrazer
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
    double threshold = 5;

    /********************************************************************
     /**
     * GLOBAL STATE INFORMATION
     * 
     */
    
    Color currentColor;
    boolean hasStarted = false;
	

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
    public StateGrazer(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);
        threshold = FlockingReader.getDoubleParam(atts, THRESHOLD_PARAM, defaultThreshold, loc);
		// TBC: Initialize the memory of the grazer
    }

    /********************************************************************
     * 
     * Code block for solution to behavior generation.
     * 
     */
    
    protected Percept colorTarget(List<Percept> ps) {
        //Figure out which boid is the closest, then go after it.
    	
    	int i = 0;
    	Percept target = null; // this is the best target to go after
    	while (i < ps.size()) {
    		//System.out.println("stats: " + ps.get(i).getAngle() + ", " + ps.get(i).getOrientation());
    		if((ps.get(i).getObjectCategory() == Percept.ObjectCategory.LIGHT)
    				&&
    				(ps.get(i).getColor().getRGB() == currentColor.getRGB())
    				&&
    				(target == null || (ps.get(i).getDistance() < target.getDistance()))) {
    			
    			target = ps.get(i);
    		}
    		i++;
    		
    	}
    	
    	
    	return target;
    }
    protected Percept bestTarget(List<Percept> ps) {
       
    	
    	int i = 0;
    	Percept target = null; // this is the best target to go after
    	while (i < ps.size()) {
    		//System.out.println("stats: " + ps.get(i).getAngle() + ", " + ps.get(i).getOrientation());
    		if((target == null || (ps.get(i).getDistance() < target.getDistance()))
    				&&
    				(ps.get(i).getObjectCategory() == Percept.ObjectCategory.LIGHT)) {
    			
    			target = ps.get(i);
    		}
    		i++;
    		
    	}
    	currentColor = target.getColor();
    	return target;
    	
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
    	
    	//check for nearest color first, then pattern off of that choice
    	Percept goalLight = null;
    	
    	if(!hasStarted) {
    		goalLight = bestTarget(ps);
    		hasStarted = true;
    	}
    	else {
    		goalLight = colorTarget(ps);
    	}
    	
    	// TBC: change the state of the grazer memory here 
        
        todo = new LinkedList<Intention>();

        if (goalLight != null) {
            steerTo(goalLight);
            if(goalLight.getDistance() <= threshold) {
            	//System.out.println(goalLight.getColor());
            	if(goalLight.getColor().getRGB()==red.getRGB()) {
            		currentColor = green;
            	}
            	else if(goalLight.getColor().getRGB()==green.getRGB()) {
            		currentColor = blue;
            	}
            	else {
            		currentColor = red;
            	}
            }
        }

		
        
        
        

    }

}
