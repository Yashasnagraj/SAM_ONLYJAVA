/**
 * TransitionEngine - A Java class for generating FFmpeg commands to create video transitions
 * 
 * This class provides static methods that generate FFmpeg command strings for various
 * video transitions. Each method returns a complete FFmpeg command that can be executed
 * to create a transition between two videos.
 */
public class TransitionEngine {

    /**
     * Creates a simple fade transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @return FFmpeg command string
     */
    public static String getFadeCommand(String input1, String input2, String output, double duration) {
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v][1:v]xfade=transition=fade:duration=" + duration + ":offset=3,format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
    }

    /**
     * Creates a glitch transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param glitchStrength Intensity of the glitch effect (1-100)
     * @return FFmpeg command string
     */
    public static String getGlitchCommand(String input1, String input2, String output, double duration, int glitchStrength) {
        // Scale glitch strength to FFmpeg parameters
        int noiseAmount = Math.min(100, Math.max(5, glitchStrength));
        int rgbShift = Math.min(20, Math.max(1, glitchStrength / 5));
        
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v][1:v]xfade=transition=fade:duration=" + duration + ":offset=3," +
               "noise=c0s=" + noiseAmount + ":allf=t+u*" + (noiseAmount/10) + "," +
               "rgbashift=rh=" + rgbShift + ":bh=" + rgbShift + ":gh=" + (rgbShift/2) + "," +
               "format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
    }

    /**
     * Creates a zoom transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param zoomFactor Maximum zoom factor (1.0-2.0 recommended)
     * @return FFmpeg command string
     */
    public static String getZoomCommand(String input1, String input2, String output, double duration, double zoomFactor) {
        // Ensure zoom factor is within reasonable bounds
        double zoom = Math.min(3.0, Math.max(1.1, zoomFactor));
        
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v]split[v0a][v0b];" +
               "[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];" +
               "[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];" +
               "[v0trim]scale=iw*" + zoom + ":ih*" + zoom + ",zoompan=z='min(zoom," + zoom + ")':d=1:x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':s=iw:ih:fps=25[v0zoom];" +
               "[1:v]scale=iw*" + zoom + ":ih*" + zoom + ",zoompan=z='" + zoom + "-min(" + (zoom-1.0) + ",t/" + duration + "*" + (zoom-1.0) + ")':d=1:x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':s=iw:ih:fps=25[v1zoom];" +
               "[v0main][v0zoom][v1zoom]concat=n=3:v=1:a=0,format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
    }

    /**
     * Creates a blur transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @param maxBlur Maximum blur amount (5-50 recommended)
     * @return FFmpeg command string
     */
    public static String getBlurCommand(String input1, String input2, String output, double duration, int maxBlur) {
        // Ensure blur amount is within reasonable bounds
        int blur = Math.min(100, Math.max(5, maxBlur));
        
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v]split[v0a][v0b];" +
               "[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];" +
               "[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];" +
               "[v0trim]boxblur=luma_radius=min(" + blur + ",t*" + blur + "/" + duration + "):luma_power=1:enable='between(t,0," + duration + ")'[v0blur];" +
               "[1:v]trim=duration=" + duration + ",setpts=PTS-STARTPTS,boxblur=luma_radius=max(0," + blur + "-t*" + blur + "/" + duration + "):luma_power=1:enable='between(t,0," + duration + ")'[v1blur];" +
               "[v0blur][v1blur]blend=all_expr='A*(1-T/" + duration + ")+B*(T/" + duration + ")'[vblend];" +
               "[v0main][vblend][1:v]concat=n=3:v=1:a=0,format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
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
     * @return FFmpeg command string
     */
    public static String getWhipPanCommand(String input1, String input2, String output, double duration, String direction, int blurStrength) {
        // Validate direction
        if (!direction.equals("left") && !direction.equals("right") && 
            !direction.equals("up") && !direction.equals("down")) {
            direction = "left"; // Default to left if invalid
        }
        
        // Ensure blur strength is within reasonable bounds
        int blur = Math.min(100, Math.max(10, blurStrength));
        
        // Determine transition type based on direction
        String transitionType;
        switch (direction) {
            case "left":
                transitionType = "slideright";
                break;
            case "right":
                transitionType = "slideleft";
                break;
            case "up":
                transitionType = "slidedown";
                break;
            case "down":
                transitionType = "slideup";
                break;
            default:
                transitionType = "slideright";
        }
        
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v]split[v0a][v0b];" +
               "[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];" +
               "[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];" +
               "[v0trim][1:v]xfade=transition=" + transitionType + ":duration=" + duration + ":offset=0," +
               "boxblur=luma_radius=min(" + blur + ",max(0,1-abs(t-" + (duration/2) + ")/" + (duration/2) + ")*" + blur + "):luma_power=1:enable='between(t,0," + duration + ")'[vblend];" +
               "[v0main][vblend]concat=n=2:v=1:a=0,format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
    }

    /**
     * Creates a spin transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @return FFmpeg command string
     */
    public static String getSpinCommand(String input1, String input2, String output, double duration) {
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v]split[v0a][v0b];" +
               "[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];" +
               "[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];" +
               "[v0trim]rotate='PI/2*min(1,t/" + duration + ")':c=none:ow=iw:oh=ih:fillcolor=black,format=rgba[v0r];" +
               "[1:v]trim=duration=" + duration + ",setpts=PTS-STARTPTS,rotate='-PI/2*(1-min(1,t/" + duration + "))':c=none:ow=iw:oh=ih:fillcolor=black,format=rgba[v1r];" +
               "[v0r][v1r]blend=all_expr='if(between(T,0," + duration + "),A*(1-(T/" + duration + "))+B*(T/" + duration + "),B)'[vblend];" +
               "[v0main][vblend][1:v]concat=n=3:v=1:a=0,format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
    }

    /**
     * Creates a light flash transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param output Path for the output video
     * @param duration Duration of the transition in seconds
     * @return FFmpeg command string
     */
    public static String getLightFlashCommand(String input1, String input2, String output, double duration) {
        return "ffmpeg -y " +
               "-i \"" + input1 + "\" " +
               "-i \"" + input2 + "\" " +
               "-filter_complex " +
               "\"[0:v]split[v0a][v0b];" +
               "[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];" +
               "[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];" +
               "[v0trim]fade=t=out:st=0:d=" + (duration/2) + ":alpha=1,format=rgba[v0fade];" +
               "[1:v]trim=duration=" + duration + ",setpts=PTS-STARTPTS,fade=t=in:st=0:d=" + (duration/2) + ":alpha=1,format=rgba[v1fade];" +
               "color=white:s=1920x1080:d=" + duration + ",format=rgba,split[white1][white2];" +
               "[white1][v0fade]overlay[whitefade1];" +
               "[white2][v1fade]overlay[whitefade2];" +
               "[whitefade1][whitefade2]blend=all_expr='if(gte(T," + (duration/2) + "),B,A)'[vblend];" +
               "[v0main][vblend][1:v]concat=n=3:v=1:a=0,format=yuv420p\" " +
               "-c:v libx264 -preset fast -crf 18 " +
               "\"" + output + "\"";
    }
}
