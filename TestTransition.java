/**
 * TestTransition - A simple program to test a video transition
 */
public class TestTransition {
    public static void main(String[] args) {
        try {
            // Define input and output paths
            String input1 = "../input_videos/clip_a.mp4";
            String input2 = "../input_videos/clip_b.mp4";
            String output = "../output_videos/fade_output.mp4";
            
            // Generate a fade transition command
            String fadeCommand = TransitionEngine.getFadeCommand(input1, input2, output, 1.0);
            
            System.out.println("Executing FFmpeg command:");
            System.out.println(fadeCommand);
            
            // Execute the command
            int exitCode = FFmpegExecutor.executeCommand(fadeCommand);
            
            if (exitCode == 0) {
                System.out.println("\nTransition created successfully!");
                System.out.println("Output video saved to: " + output);
            } else {
                System.out.println("\nError creating transition. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
