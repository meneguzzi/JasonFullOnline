package example;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * PDDLGenerator class for generating PDDL problem files.
 */
public class PDDLGenerator {

    // Generates a PDDL problem file based on given beliefs and predicates.
    public static void generate(String agName, List<String> beliefs, List<String> predicates) {
        try {
            // Specify the file path for the PDDL file
            String filePath = agName+"problem.pddl";
            //System.out.println(filePath);

            // Create a FileWriter
            FileWriter writer = new FileWriter(filePath);

            // Define your custom data for each section
            String domainName = "shopping-domain"; // Can be anything really
            String[] objectDeclarations = {"agent - person"}; // Not sure this is necessary but
            String[] initialState = beliefs.toArray(new String[0]);

            // Write PDDL content to the file using custom data
            writePDDLHeader(writer, "texting-problem", domainName);
            writePDDLObjects(writer, objectDeclarations);
            writePDDLInitialState(writer, initialState, preprocessPredicates(predicates));
            writePDDLGoalState(writer, preprocessPredicates(predicates));
            writePDDLFooter(writer);

            // Close the FileWriter
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Writes the PDDL header to the file.
    private static void writePDDLHeader(FileWriter writer, String problemName, String domainName) throws IOException {
        writer.write("(define (problem " + problemName + ")\n");
        writer.write("  (:domain " + domainName + ")\n");
    }

    // Writes the PDDL objects to the file.
    private static void writePDDLObjects(FileWriter writer, String[] objectDeclarations) throws IOException {
        writer.write("  (:objects\n");
        for (String objectDeclaration : objectDeclarations) {
            writer.write("    " + objectDeclaration + "\n");
        }
        writer.write("  )\n");
    }

    // Writes the PDDL initial state to the file.
    private static void writePDDLInitialState(FileWriter writer, String[] initialState, List<String> goalState) throws IOException {
        writer.write("  (:init\n");
        if(initialState.length >= 1) {
            for (String initialStateFact : initialState) {
                writer.write("    (" + initialStateFact + ")\n");
            }
        } else {
            for (String goalStateFact :goalState) {
                writer.write("    (not (" + goalStateFact + "))\n");
            }
        }
        writer.write("  )\n");
    }

    // Writes the PDDL goal state to the file.
    private static void writePDDLGoalState(FileWriter writer, List<String> predicates) throws IOException {
        writer.write("  (:goal\n");
        writer.write("    (and\n");

        for (String predicate : predicates) {
            writer.write("      (" + predicate + ")\n");
        }

        writer.write("    )\n");
        writer.write("  )\n");
    }

    // Writes the PDDL footer to the file.
    private static void writePDDLFooter(FileWriter writer) throws IOException {
        writer.write(")\n");
    }

    // Preprocesses the list of predicates, removing parentheses and splitting on '&'.
    // If using complex predicates in .asl may need modification
    private static List<String> preprocessPredicates(List<String> predicates) {
        List<String> formattedPredicates = new ArrayList<>();
    
        for (String predicate : predicates) {
            // Remove parentheses only if they are around the entire predicate
            String withoutParentheses = predicate.replaceAll("^\\((.*)\\)$", "$1").trim();
    
            // Split the predicate if it contains "&" and trim each part
            if (withoutParentheses.contains("&")) {
                String[] parts = withoutParentheses.split("&");
                for (String part : parts) {
                    formattedPredicates.add(part.trim());
                }
            } else {
                // If there is no "&", add the predicate directly
                formattedPredicates.add(withoutParentheses);
            }
        }
        return formattedPredicates;
    }
}
