package example;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jason.asSemantics.Agent;
import jason.asSemantics.GoalListener;
import jason.asSyntax.Plan;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;

public class CustomAgent extends Agent {
    private Logger recoveryLogger = Logger.getLogger("t1."+"Recovery");

    // Adds a goal listener to the agents, if a goal fails we can then try to recover (no applicable plans found)
    @Override
    public void initAg() {
        super.initAg();
        getTS().addGoalListener((GoalListener) new GoalListener() {
            @Override
            public void goalFailed(Trigger goal, Term result) {
                boolean success = recoveryOperation(goal);
                if(!success) {System.out.println("Recovery Failed in CustomAgent.java");}
            }
        });
    }

    // The recovery operation for no applicable plans
    private boolean recoveryOperation(Trigger goal) {
        // Get Agent
        Agent ag = getTS().getAg();
        String agName = getTS().getAgArch().getAgName();
        Action action = new Action();

        // Construct goal string for extraction of relevant plans
        String goalString = "+!" + removeSource(goal.getLiteral().toString());
        
        // Extract all plans and filter to relevant failed goal
        List<Plan> plans = extractPlans(ag);
        List<Plan> filteredPlans = new ArrayList<>();
        for (Plan p : plans) {if (p.getTrigger().toString().startsWith(goalString)) {filteredPlans.add(p);}}

        // Choose the first plan to fix the context <-- Could be adapted
        Plan planToFix = filteredPlans.get(0);

        // If plan null... tell developer an error occured
        if (planToFix.getContext() == null) {System.out.println("* Plan Context was null... Cannot recover context");return false;}

        // If multiple predicates...
        List<String> contextString = new ArrayList<>();
        contextString.add(planToFix.getContext().toString()); // This is the predicate
        List<String> context = preprocessPredicates(contextString);

        // Extract agent's beliefs and prepare a list for the planner
        List<String> beliefs = action.extractBeliefs(ag);
        List<String> goalStates = findPredicates(beliefs, context);

        // While all goalStates have not been achieved... Continue Recovery
        while (goalStates.size() != 0) {
            // Run the planner to find actions to fulfill the context
            List<String> plan = RunPlanner.run(agName, beliefs, goalStates, 1);

            // Debugging information if plan produced by planner is empty...
            if(plan.isEmpty()){
                System.out.println("An error occured with the planner");
                System.out.println("To debug: Go to RunPlanner.java and print the output.");
                return false;
            }

            // If plan is not empty... Execute the action returned by the planner
            if(!plan.isEmpty()) {
                recoveryLogger.info(agName+" --> Context Not Fulfilled, Running Action --> "+plan.get(0).toString());
                boolean success = action.startAction(ag, plan.get(0).toLowerCase()); // check output of bool
                if (!success) {recoveryLogger.info("Recovery failure");}
            }

            // Recheck Beliefs 
            beliefs = action.extractBeliefs(ag);
            goalStates = findPredicates(beliefs, context); //goalstates just fancy word for relevant predicates
        }
        recoveryLogger.info(agName +" --> Context Recovered, Readding Event...(ignore next msg)");

        // Re-add the failed goal as an external event
        ag.getTS().getC().addExternalEv(goal);
        return true;
    }

    // Extracts plans which do not contain kqml.
    private List<Plan> extractPlans(Agent ag) {
        List<Plan> filteredPlans = new ArrayList<>();
        for (Plan p : ag.getPL().getPlans()) {
            if (!p.toString().startsWith("@kqml")) {
                filteredPlans.add(p);
            }
        }
        return filteredPlans;
    }

    // Checks what predicates have been achieved/are missing
    private static List<String> findPredicates(List<String> beliefs, List<String> predicates) {
        List<String> missingPredicates = new ArrayList<>();
        for (String pred : predicates) {
            if (!beliefs.contains(pred)) {missingPredicates.add(pred);}
        }
        return missingPredicates;
    }

    // Remove [source(self)] or [source(percepts)] using regular expression
    public static String removeSource(String input) {
        return input.replaceAll("\\[source\\((self|percepts)\\)\\]", "");
    }

    // If there is multiple predicates I remove the & between them and the () that get added 
    private static List<String> preprocessPredicates(List<String> predicates) {
        List<String> formattedPredicates = new ArrayList<>();
    
        for (String predicate : predicates) {
            // Remove all brackets from the predicate
            String withoutBrackets = predicate.replaceAll("[\\[\\]()]", "").trim();
    
            // Split the predicate if it contains "&" and trim each part
            if (withoutBrackets.contains("&")) {
                String[] parts = withoutBrackets.split("&");
                for (String part : parts) {formattedPredicates.add(part.trim());}
            } else {
                // If there is no "&", add the predicate directly
                formattedPredicates.add(withoutBrackets);
            }
        }
        return formattedPredicates;
    }
}
