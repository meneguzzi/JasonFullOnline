package example;

import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.environment.*;


public class Env extends Environment {

    // Must use act.startAction() to run our custom actions, found in Action.java
    @Override
    public boolean executeAction(String agName, Structure action) { 
        Agent ag = acquireAgent(agName);     
        Action act = new Action();

        boolean success = act.startAction(ag, action.toString());

        if (true) { informAgsEnvironmentChanged();}

        if(success) {return true;}else {return false;} 
    }

    // Acquires the Agent Object
    private Agent acquireAgent(String agName) {
        Agent agent = null;
        try {agent = getEnvironmentInfraTier().getRuntimeServices().getAgentSnapshot(agName);} catch (Exception e) {e.printStackTrace();} 
        return agent;
    }

    // Called before the MAS execution with the args informed in .mas2j 
    @Override
    public void init(String[] args) {super.init(args);}

    // Called before the end of MAS execution 
    @Override
    public void stop() {super.stop();}
}
