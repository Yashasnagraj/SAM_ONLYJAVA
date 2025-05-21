/**
 * TestTransition - A simple program to test a video transition
 */
public class TestTransition {
    public static void main(String[] args) {
        // Define input and output paths
        String input1 = "../input_videos/clip_a.mp4";
        String input2 = "../input_videos/clip_b.mp4";
        String output = "../output_videos/fade_output.mp4";

        // Generate commands for different transitions
        System.out.println("=== FFmpeg Commands for Video Transitions ===\n");

        // 1. Fade transition
        String fadeCommand = TransitionEngine.getFadeCommand(input1, input2, output, 1.0);
        System.out.println("1. Fade Transition Command:");
        System.out.println(fadeCommand);
        System.out.println();

        // 2. Glitch transition
        String glitchOutput = "../output_videos/glitch_output.mp4";
        String glitchCommand = TransitionEngine.getGlitchCommand(input1, input2, glitchOutput, 1.2, 25);
        System.out.println("2. Glitch Transition Command:");
        System.out.println(glitchCommand);
        System.out.println();

        // 3. Zoom transition
        String zoomOutput = "../output_videos/zoom_output.mp4";
        String zoomCommand = TransitionEngine.getZoomCommand(input1, input2, zoomOutput, 1.5, 1.5);
        System.out.println("3. Zoom Transition Command:");
        System.out.println(zoomCommand);
        System.out.println();

        // 4. Blur transition
        String blurOutput = "../output_videos/blur_output.mp4";
        String blurCommand = TransitionEngine.getBlurCommand(input1, input2, blurOutput, 1.5, 35);
        System.out.println("4. Blur Transition Command:");
        System.out.println(blurCommand);
        System.out.println();

        // 5. Whip Pan transition
        String whipOutput = "../output_videos/whip_output.mp4";
        String whipCommand = TransitionEngine.getWhipPanCommand(input1, input2, whipOutput, 1.0, "left", 100);
        System.out.println("5. Whip Pan Transition Command:");
        System.out.println(whipCommand);
        System.out.println();

        System.out.println("=== Instructions to Create Transition Videos ===\n");
        System.out.println("1. Install FFmpeg if not already installed");
        System.out.println("2. Copy and paste one of the above commands into a command prompt");
        System.out.println("3. The output video will be saved in the output_videos directory");
    }
}
