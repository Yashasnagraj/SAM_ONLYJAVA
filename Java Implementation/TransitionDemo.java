import java.io.File;

/**
 * TransitionDemo - A demonstration class for the JavaTransitionEngine
 * 
 * This class provides a simple way to test the various video transitions
 * implemented in the JavaTransitionEngine class.
 */
public class TransitionDemo {
    
    // Default paths for input and output videos
    private static final String DEFAULT_INPUT1 = "input_videos/clip_a.mp4";
    private static final String DEFAULT_INPUT2 = "input_videos/clip_b.mp4";
    private static final String OUTPUT_DIR = "output_videos";
    
    public static void main(String[] args) {
        // Create output directory if it doesn't exist
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // Get input paths from arguments or use defaults
        String input1 = args.length > 0 ? args[0] : DEFAULT_INPUT1;
        String input2 = args.length > 1 ? args[1] : DEFAULT_INPUT2;
        
        // Validate input files
        File file1 = new File(input1);
        File file2 = new File(input2);
        
        if (!file1.exists() || !file2.exists()) {
            System.err.println("Input video files not found!");
            System.err.println("Looking for:");
            System.err.println("  " + file1.getAbsolutePath());
            System.err.println("  " + file2.getAbsolutePath());
            System.exit(1);
        }
        
        try {
            // Apply fade transition
            System.out.println("\n=== Applying Fade Transition ===");
            String fadeOutput = OUTPUT_DIR + "/java_fade_output.mp4";
            JavaTransitionEngine.applyFade(input1, input2, fadeOutput, 1.5);
            System.out.println("Fade transition completed: " + fadeOutput);
            
            // Apply glitch transition
            System.out.println("\n=== Applying Glitch Transition ===");
            String glitchOutput = OUTPUT_DIR + "/java_glitch_output.mp4";
            JavaTransitionEngine.applyGlitch(input1, input2, glitchOutput, 1.5, 30);
            System.out.println("Glitch transition completed: " + glitchOutput);
            
            // Apply zoom transition
            System.out.println("\n=== Applying Zoom Transition ===");
            String zoomOutput = OUTPUT_DIR + "/java_zoom_output.mp4";
            JavaTransitionEngine.applyZoom(input1, input2, zoomOutput, 1.5, 1.5);
            System.out.println("Zoom transition completed: " + zoomOutput);
            
            // Apply blur transition
            System.out.println("\n=== Applying Blur Transition ===");
            String blurOutput = OUTPUT_DIR + "/java_blur_output.mp4";
            JavaTransitionEngine.applyBlur(input1, input2, blurOutput, 1.5, 25);
            System.out.println("Blur transition completed: " + blurOutput);
            
            // Apply whip pan transition
            System.out.println("\n=== Applying Whip Pan Transition ===");
            String whipOutput = OUTPUT_DIR + "/java_whip_output.mp4";
            JavaTransitionEngine.applyWhipPan(input1, input2, whipOutput, 1.0, "left", 50);
            System.out.println("Whip pan transition completed: " + whipOutput);
            
            // Apply spin transition
            System.out.println("\n=== Applying Spin Transition ===");
            String spinOutput = OUTPUT_DIR + "/java_spin_output.mp4";
            JavaTransitionEngine.applySpin(input1, input2, spinOutput, 1.5);
            System.out.println("Spin transition completed: " + spinOutput);
            
            // Apply light flash transition
            System.out.println("\n=== Applying Light Flash Transition ===");
            String flashOutput = OUTPUT_DIR + "/java_flash_output.mp4";
            JavaTransitionEngine.applyLightFlash(input1, input2, flashOutput, 1.0);
            System.out.println("Light flash transition completed: " + flashOutput);
            
            System.out.println("\nAll transitions completed successfully!");
            System.out.println("Output videos are in the '" + OUTPUT_DIR + "' directory.");
            
        } catch (Exception e) {
            System.err.println("Error applying transitions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Prints usage information
     */
    private static void printUsage() {
        System.out.println("Usage: java TransitionDemo [input1.mp4] [input2.mp4]");
        System.out.println();
        System.out.println("If no arguments are provided, the program will look for:");
        System.out.println("  " + DEFAULT_INPUT1);
        System.out.println("  " + DEFAULT_INPUT2);
    }
}
