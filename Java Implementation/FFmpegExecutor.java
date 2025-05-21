import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * FFmpegExecutor - A utility class for executing FFmpeg commands
 * 
 * This class provides methods to execute FFmpeg commands and handle their output.
 */
public class FFmpegExecutor {
    
    /**
     * Executes an FFmpeg command and returns the process exit code
     * 
     * @param command The FFmpeg command string to execute
     * @return The process exit code (0 for success)
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    public static int executeCommand(String command) throws IOException, InterruptedException {
        // Convert the command string to a list of arguments
        List<String> commandList = parseCommand(command);
        
        // Create process builder
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.redirectErrorStream(true); // Merge stdout and stderr
        
        // Start the process
        Process process = processBuilder.start();
        
        // Read the output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        
        // Wait for the process to complete and return exit code
        return process.waitFor();
    }
    
    /**
     * Parses a command string into a list of arguments
     * 
     * @param command The command string to parse
     * @return A list of command arguments
     */
    private static List<String> parseCommand(String command) {
        List<String> commandList = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    commandList.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
            } else {
                currentArg.append(c);
            }
        }
        
        if (currentArg.length() > 0) {
            commandList.add(currentArg.toString());
        }
        
        return commandList;
    }
    
    /**
     * Executes an FFmpeg command asynchronously and returns immediately
     * 
     * @param command The FFmpeg command string to execute
     * @return The Process object
     * @throws IOException If an I/O error occurs
     */
    public static Process executeCommandAsync(String command) throws IOException {
        List<String> commandList = parseCommand(command);
        
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.redirectErrorStream(true);
        
        return processBuilder.start();
    }
    
    /**
     * Checks if FFmpeg is installed and available
     * 
     * @return true if FFmpeg is available, false otherwise
     */
    public static boolean isFFmpegAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("ffmpeg -version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
