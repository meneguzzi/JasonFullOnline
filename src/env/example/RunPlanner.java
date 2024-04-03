package example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunPlanner {

    public static long startTime;
    public static List<String> run(String agName, List<String> beliefs, List<String> predicate, int choiceOfPlanner) {
        PDDLGenerator.generate(agName, beliefs, predicate); 

        if (choiceOfPlanner == 1) {
            try {
                // Command to execute
                String command = "./ff -o domain.pddl -f " + agName + "problem.pddl";
    
                // Create ProcessBuilder
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
    
                // Redirect error stream to output stream
                processBuilder.redirectErrorStream(true);
    
                // Start the process
                Process process = processBuilder.start();
                String output;
                try (// Read the output of the process
                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A")) {
                    output = scanner.hasNext() ? scanner.next() : "";}
    
                // Wait for the process to finish
                process.waitFor();
                
                //System.out.println(output);
                List<String> plan = extractSteps(output);
                //System.out.println(plan);
                return plan;
            } catch (IOException | InterruptedException e) {e.printStackTrace();}
            return null;
        }
        if (choiceOfPlanner == 2) {
            // Implementation of Other planner or alternatively override FF implementation, and remove choiceOfPlanner
        }
        return null;
    }
    
    private static List<String> extractSteps(String output) {
        List<String> steps = new ArrayList<>();

        // Define a pattern to match lines starting with a number followed by a colon
        Pattern pattern = Pattern.compile("\\d+:\\s+([A-Z]+)");

        // Create a matcher with the input string
        Matcher matcher = pattern.matcher(output);

        // Find all matches
        while (matcher.find()) {
            String step = matcher.group(1);
            steps.add(step);
        }
        return steps;
    }
}
