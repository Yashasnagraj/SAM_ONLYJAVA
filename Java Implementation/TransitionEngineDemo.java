/**
 * TransitionEngineDemo - A simple demonstration of the TransitionEngine class
 * 
 * This class shows how to use the TransitionEngine to generate FFmpeg commands
 * for various video transitions.
 */
public class TransitionEngineDemo {
    
    public static void main(String[] args) {
        // Example input and output paths
        String input1 = "input_videos/clip_a.mp4";
        String input2 = "input_videos/clip_b.mp4";
        String outputDir = "output_videos/";
        
        // Generate and print FFmpeg commands for different transitions
        
        // 1. Fade transition
        String fadeOutput = outputDir + "fade_output.mp4";
        String fadeCommand = TransitionEngine.getFadeCommand(input1, input2, fadeOutput, 1.0);
        System.out.println("Fade Transition Command:");
        System.out.println(fadeCommand);
        System.out.println();
        
        // 2. Glitch transition
        String glitchOutput = outputDir + "glitch_output.mp4";
        String glitchCommand = TransitionEngine.getGlitchCommand(input1, input2, glitchOutput, 1.2, 25);
        System.out.println("Glitch Transition Command:");
        System.out.println(glitchCommand);
        System.out.println();
        
        // 3. Zoom transition
        String zoomOutput = outputDir + "zoom_output.mp4";
        String zoomCommand = TransitionEngine.getZoomCommand(input1, input2, zoomOutput, 1.5, 1.5);
        System.out.println("Zoom Transition Command:");
        System.out.println(zoomCommand);
        System.out.println();
        
        // 4. Blur transition
        String blurOutput = outputDir + "blur_output.mp4";
        String blurCommand = TransitionEngine.getBlurCommand(input1, input2, blurOutput, 1.5, 35);
        System.out.println("Blur Transition Command:");
        System.out.println(blurCommand);
        System.out.println();
        
        // 5. Whip Pan transition
        String whipOutput = outputDir + "whip_output.mp4";
        String whipCommand = TransitionEngine.getWhipPanCommand(input1, input2, whipOutput, 1.0, "left", 100);
        System.out.println("Whip Pan Transition Command:");
        System.out.println(whipCommand);
        System.out.println();
        
        // 6. Spin transition
        String spinOutput = outputDir + "spin_output.mp4";
        String spinCommand = TransitionEngine.getSpinCommand(input1, input2, spinOutput, 2.0);
        System.out.println("Spin Transition Command:");
        System.out.println(spinCommand);
        System.out.println();
        
        // 7. Light Flash transition
        String flashOutput = outputDir + "flash_output.mp4";
        String flashCommand = TransitionEngine.getLightFlashCommand(input1, input2, flashOutput, 1.5);
        System.out.println("Light Flash Transition Command:");
        System.out.println(flashCommand);
        
        // Note: To execute these commands, you would typically use ProcessBuilder or Runtime.exec()
        // For example:
        // try {
        //     Process process = Runtime.getRuntime().exec(fadeCommand);
        //     process.waitFor();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
}
