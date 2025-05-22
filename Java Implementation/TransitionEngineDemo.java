/**
 * TransitionEngineDemo - A simple demonstration of the TransitionEngine class
 *
 * This class shows how to use the TransitionEngine to apply various video transitions
 * using a pure Java implementation with JavaCV.
 */
public class TransitionEngineDemo {

    public static void main(String[] args) {
        // Example input and output paths
        String input1 = "input_videos/clip_a.mp4";
        String input2 = "input_videos/clip_b.mp4";
        String outputDir = "output_videos/";

        System.out.println("=== Applying Video Transitions with JavaCV ===\n");

        // 1. Fade transition
        String fadeOutput = outputDir + "fade_output.mp4";
        System.out.println("1. Applying Fade Transition...");
        boolean fadeResult = TransitionEngine.applyFadeTransition(input1, input2, fadeOutput, 1.0);
        System.out.println("   Result: " + (fadeResult ? "Success" : "Failed"));
        System.out.println();

        // 2. Glitch transition
        String glitchOutput = outputDir + "glitch_output.mp4";
        System.out.println("2. Applying Glitch Transition...");
        boolean glitchResult = TransitionEngine.applyGlitchTransition(input1, input2, glitchOutput, 1.2, 25);
        System.out.println("   Result: " + (glitchResult ? "Success" : "Failed"));
        System.out.println();

        // 3. Zoom transition
        String zoomOutput = outputDir + "zoom_output.mp4";
        System.out.println("3. Applying Zoom Transition...");
        boolean zoomResult = TransitionEngine.applyZoomTransition(input1, input2, zoomOutput, 1.5, 1.5);
        System.out.println("   Result: " + (zoomResult ? "Success" : "Failed"));
        System.out.println();

        // 4. Blur transition
        String blurOutput = outputDir + "blur_output.mp4";
        System.out.println("4. Applying Blur Transition...");
        boolean blurResult = TransitionEngine.applyBlurTransition(input1, input2, blurOutput, 1.5, 35);
        System.out.println("   Result: " + (blurResult ? "Success" : "Failed"));
        System.out.println();

        // 5. Whip Pan transition
        String whipOutput = outputDir + "whip_output.mp4";
        System.out.println("5. Applying Whip Pan Transition...");
        boolean whipResult = TransitionEngine.applyWhipPanTransition(input1, input2, whipOutput, 1.0, "left", 100);
        System.out.println("   Result: " + (whipResult ? "Success" : "Failed"));
        System.out.println();

        // 6. Spin transition
        String spinOutput = outputDir + "spin_output.mp4";
        System.out.println("6. Applying Spin Transition...");
        boolean spinResult = TransitionEngine.applySpinTransition(input1, input2, spinOutput, 2.0);
        System.out.println("   Result: " + (spinResult ? "Success" : "Failed"));
        System.out.println();

        // 7. Light Flash transition
        String flashOutput = outputDir + "flash_output.mp4";
        System.out.println("7. Applying Light Flash Transition...");
        boolean flashResult = TransitionEngine.applyLightFlashTransition(input1, input2, flashOutput, 1.5);
        System.out.println("   Result: " + (flashResult ? "Success" : "Failed"));

        System.out.println("\nAll transitions have been processed. Check the output directory for results.");
    }
}
