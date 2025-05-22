/**
 * TransitionEngine - A Java class for creating video transitions
 *
 * This class provides static methods for applying various video transitions
 * using a pure Java implementation with JavaCV. Each method processes the input
 * videos and creates a new video with the transition applied.
 */
public class TransitionEngine {

    /**
     * Creates a simple fade transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applyFadeTransition(String input1, String input2, String output, double duration) {
        try {
            JavaTransitionEngine.applyFade(input1, input2, output, duration);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying fade transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a glitch transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param glitchStrength Intensity of the glitch effect (1-100)
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applyGlitchTransition(String input1, String input2, String output, double duration, int glitchStrength) {
        try {
            JavaTransitionEngine.applyGlitch(input1, input2, output, duration, glitchStrength);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying glitch transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a zoom transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param zoomFactor Maximum zoom factor (1.0-2.0 recommended)
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applyZoomTransition(String input1, String input2, String output, double duration, double zoomFactor) {
        try {
            JavaTransitionEngine.applyZoom(input1, input2, output, duration, zoomFactor);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying zoom transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a blur transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param maxBlur Maximum blur amount (5-50 recommended)
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applyBlurTransition(String input1, String input2, String output, double duration, int maxBlur) {
        try {
            JavaTransitionEngine.applyBlur(input1, input2, output, duration, maxBlur);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying blur transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a whip pan transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param direction Direction of the whip ("left", "right", "up", "down")
     * @param blurStrength Strength of the motion blur (10-100 recommended)
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applyWhipPanTransition(String input1, String input2, String output, double duration, String direction, int blurStrength) {
        try {
            JavaTransitionEngine.applyWhipPan(input1, input2, output, duration, direction, blurStrength);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying whip pan transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a spin transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applySpinTransition(String input1, String input2, String output, double duration) {
        try {
            JavaTransitionEngine.applySpin(input1, input2, output, duration);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying spin transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a light flash transition between two videos
     *
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @return true if the transition was applied successfully, false otherwise
     */
    public static boolean applyLightFlashTransition(String input1, String input2, String output, double duration) {
        try {
            JavaTransitionEngine.applyLightFlash(input1, input2, output, duration);
            return true;
        } catch (Exception e) {
            System.err.println("Error applying light flash transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applyFadeTransition instead
     */
    public static String getFadeCommand(String input1, String input2, String output, double duration) {
        System.out.println("WARNING: getFadeCommand is deprecated. Use applyFadeTransition instead.");
        applyFadeTransition(input1, input2, output, duration);
        return "Transition applied using JavaCV implementation";
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applyGlitchTransition instead
     */
    public static String getGlitchCommand(String input1, String input2, String output, double duration, int glitchStrength) {
        System.out.println("WARNING: getGlitchCommand is deprecated. Use applyGlitchTransition instead.");
        applyGlitchTransition(input1, input2, output, duration, glitchStrength);
        return "Transition applied using JavaCV implementation";
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applyZoomTransition instead
     */
    public static String getZoomCommand(String input1, String input2, String output, double duration, double zoomFactor) {
        System.out.println("WARNING: getZoomCommand is deprecated. Use applyZoomTransition instead.");
        applyZoomTransition(input1, input2, output, duration, zoomFactor);
        return "Transition applied using JavaCV implementation";
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applyBlurTransition instead
     */
    public static String getBlurCommand(String input1, String input2, String output, double duration, int maxBlur) {
        System.out.println("WARNING: getBlurCommand is deprecated. Use applyBlurTransition instead.");
        applyBlurTransition(input1, input2, output, duration, maxBlur);
        return "Transition applied using JavaCV implementation";
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applyWhipPanTransition instead
     */
    public static String getWhipPanCommand(String input1, String input2, String output, double duration, String direction, int blurStrength) {
        System.out.println("WARNING: getWhipPanCommand is deprecated. Use applyWhipPanTransition instead.");
        applyWhipPanTransition(input1, input2, output, duration, direction, blurStrength);
        return "Transition applied using JavaCV implementation";
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applySpinTransition instead
     */
    public static String getSpinCommand(String input1, String input2, String output, double duration) {
        System.out.println("WARNING: getSpinCommand is deprecated. Use applySpinTransition instead.");
        applySpinTransition(input1, input2, output, duration);
        return "Transition applied using JavaCV implementation";
    }

    /**
     * For backward compatibility with old code
     * @deprecated Use applyLightFlashTransition instead
     */
    public static String getLightFlashCommand(String input1, String input2, String output, double duration) {
        System.out.println("WARNING: getLightFlashCommand is deprecated. Use applyLightFlashTransition instead.");
        applyLightFlashTransition(input1, input2, output, duration);
        return "Transition applied using JavaCV implementation";
    }
}
