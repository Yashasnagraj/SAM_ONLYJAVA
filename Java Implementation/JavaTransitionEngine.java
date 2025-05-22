import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.io.File;
import java.util.Random;

/**
 * JavaTransitionEngine - A Java class for creating video transitions without FFmpeg
 * 
 * This class provides static methods that implement various video transitions using
 * JavaCV (OpenCV for Java). Each method processes two input videos frame-by-frame
 * and creates a new video with the transition applied.
 */
public class JavaTransitionEngine {

    /**
     * Creates a simple fade transition (crossfade) between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @throws Exception If an error occurs during processing
     */
    public static void applyFade(String input1, String input2, String outputPath) throws Exception {
        applyFade(input1, input2, outputPath, 1.0);
    }

    /**
     * Creates a simple fade transition (crossfade) between two videos with specified duration
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @throws Exception If an error occurs during processing
     */
    public static void applyFade(String input1, String input2, String outputPath, double duration) throws Exception {
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Blend frames from both videos
            for (int i = 0; i < transitionFrames; i++) {
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();
                
                if (frame1 == null || frame2 == null) break;
                
                // Convert frames to OpenCV Mat
                OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
                Mat mat1 = converter.convert(frame1);
                Mat mat2 = converter.convert(frame2);
                
                // Calculate alpha for blending
                double alpha = (double) i / transitionFrames;
                
                // Create output Mat
                Mat outputMat = new Mat();
                
                // Blend the two frames
                addWeighted(mat1, 1.0 - alpha, mat2, alpha, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame blendedFrame = converter.convert(outputMat);
                recorder.record(blendedFrame);
                
                // Release resources
                mat1.release();
                mat2.release();
                outputMat.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }

    /**
     * Creates a glitch transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @param glitchStrength Intensity of the glitch effect (1-100)
     * @throws Exception If an error occurs during processing
     */
    public static void applyGlitch(String input1, String input2, String outputPath, double duration, int glitchStrength) throws Exception {
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        // Scale glitch strength to parameters
        int maxRowShift = Math.min(width / 4, Math.max(5, glitchStrength));
        int rgbShift = Math.min(20, Math.max(1, glitchStrength / 5));
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Apply glitch effect and blend
            Random random = new Random();
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            for (int i = 0; i < transitionFrames; i++) {
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();
                
                if (frame1 == null || frame2 == null) break;
                
                // Convert frames to OpenCV Mat
                Mat mat1 = converter.convert(frame1);
                Mat mat2 = converter.convert(frame2);
                
                // Calculate alpha for blending
                double alpha = (double) i / transitionFrames;
                
                // Apply glitch effects
                Mat glitchedMat1 = applyGlitchEffect(mat1, maxRowShift, rgbShift, random, alpha);
                Mat glitchedMat2 = applyGlitchEffect(mat2, maxRowShift, rgbShift, random, 1.0 - alpha);
                
                // Create output Mat
                Mat outputMat = new Mat();
                
                // Blend the two glitched frames
                addWeighted(glitchedMat1, 1.0 - alpha, glitchedMat2, alpha, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame blendedFrame = converter.convert(outputMat);
                recorder.record(blendedFrame);
                
                // Release resources
                mat1.release();
                mat2.release();
                glitchedMat1.release();
                glitchedMat2.release();
                outputMat.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }
    
    /**
     * Helper method to apply glitch effect to a frame
     */
    private static Mat applyGlitchEffect(Mat input, int maxRowShift, int rgbShift, Random random, double intensity) {
        Mat output = input.clone();
        
        // Split the channels
        java.util.List<Mat> channels = new java.util.ArrayList<>(3);
        split(output, channels);
        
        // Apply RGB shift
        int actualRgbShift = (int)(rgbShift * intensity);
        if (actualRgbShift > 0) {
            // Shift red channel
            Mat redShifted = new Mat(channels.get(2).size(), channels.get(2).type());
            Mat translationMatrix = new Mat(2, 3, CV_32F);
            translationMatrix.put(0, 0, 1, 0, actualRgbShift, 0, 1, 0);
            warpAffine(channels.get(2), redShifted, translationMatrix, redShifted.size());
            channels.set(2, redShifted);
            
            // Shift blue channel
            Mat blueShifted = new Mat(channels.get(0).size(), channels.get(0).type());
            translationMatrix.put(0, 2, -actualRgbShift);
            warpAffine(channels.get(0), blueShifted, translationMatrix, blueShifted.size());
            channels.set(0, blueShifted);
            
            translationMatrix.release();
        }
        
        // Merge channels back
        merge(channels, output);
        
        // Apply row shifting (random rows are shifted horizontally)
        int numRowsToShift = (int)(10 * intensity);
        for (int i = 0; i < numRowsToShift; i++) {
            int rowToShift = random.nextInt(input.rows());
            int shiftAmount = random.nextInt(maxRowShift) - maxRowShift/2;
            
            Mat row = output.row(rowToShift);
            Mat shiftedRow = new Mat();
            
            // Create translation matrix for the row
            Mat translationMatrix = new Mat(2, 3, CV_32F);
            translationMatrix.put(0, 0, 1, 0, shiftAmount, 0, 1, 0);
            
            // Apply shift to the row
            warpAffine(row, shiftedRow, translationMatrix, row.size());
            
            // Copy shifted row back to output
            shiftedRow.copyTo(output.row(rowToShift));
            
            // Release resources
            row.release();
            shiftedRow.release();
            translationMatrix.release();
        }
        
        // Release channel resources
        for (Mat channel : channels) {
            channel.release();
        }
        
        return output;
    }

    /**
     * Creates a zoom transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @param zoomFactor Maximum zoom factor (1.0-2.0 recommended)
     * @throws Exception If an error occurs during processing
     */
    public static void applyZoom(String input1, String input2, String outputPath, double duration, double zoomFactor) throws Exception {
        // Ensure zoom factor is within reasonable bounds
        double zoom = Math.min(3.0, Math.max(1.1, zoomFactor));
        
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Apply zoom effect
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            for (int i = 0; i < transitionFrames; i++) {
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();
                
                if (frame1 == null || frame2 == null) break;
                
                // Convert frames to OpenCV Mat
                Mat mat1 = converter.convert(frame1);
                Mat mat2 = converter.convert(frame2);
                
                // Calculate alpha for blending and zoom factors
                double alpha = (double) i / transitionFrames;
                double zoom1 = 1.0 + (zoom - 1.0) * alpha;
                double zoom2 = zoom - (zoom - 1.0) * alpha;
                
                // Apply zoom to both frames
                Mat zoomedMat1 = new Mat();
                Mat zoomedMat2 = new Mat();
                
                // Calculate new dimensions for zoom
                Size zoomSize1 = new Size((int)(width * zoom1), (int)(height * zoom1));
                Size zoomSize2 = new Size((int)(width * zoom2), (int)(height * zoom2));
                
                // Resize images
                resize(mat1, zoomedMat1, zoomSize1);
                resize(mat2, zoomedMat2, zoomSize2);
                
                // Calculate crop region to get back to original size
                Rect cropRect1 = new Rect(
                    (int)((zoomedMat1.cols() - width) / 2),
                    (int)((zoomedMat1.rows() - height) / 2),
                    width,
                    height
                );
                
                Rect cropRect2 = new Rect(
                    (int)((zoomedMat2.cols() - width) / 2),
                    (int)((zoomedMat2.rows() - height) / 2),
                    width,
                    height
                );
                
                // Crop the zoomed images
                Mat croppedMat1 = new Mat(zoomedMat1, cropRect1);
                Mat croppedMat2 = new Mat(zoomedMat2, cropRect2);
                
                // Create output Mat
                Mat outputMat = new Mat();
                
                // Blend the two zoomed frames
                addWeighted(croppedMat1, 1.0 - alpha, croppedMat2, alpha, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame blendedFrame = converter.convert(outputMat);
                recorder.record(blendedFrame);
                
                // Release resources
                mat1.release();
                mat2.release();
                zoomedMat1.release();
                zoomedMat2.release();
                croppedMat1.release();
                croppedMat2.release();
                outputMat.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }

    /**
     * Creates a blur transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @param maxBlur Maximum blur amount (5-50 recommended)
     * @throws Exception If an error occurs during processing
     */
    public static void applyBlur(String input1, String input2, String outputPath, double duration, int maxBlur) throws Exception {
        // Ensure blur amount is within reasonable bounds
        int blur = Math.min(100, Math.max(5, maxBlur));
        
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Apply blur effect and blend
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            for (int i = 0; i < transitionFrames; i++) {
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();
                
                if (frame1 == null || frame2 == null) break;
                
                // Convert frames to OpenCV Mat
                Mat mat1 = converter.convert(frame1);
                Mat mat2 = converter.convert(frame2);
                
                // Calculate alpha for blending and blur amounts
                double alpha = (double) i / transitionFrames;
                int blurAmount1 = (int)(blur * alpha);
                int blurAmount2 = (int)(blur * (1.0 - alpha));
                
                // Apply blur to both frames
                Mat blurredMat1 = new Mat();
                Mat blurredMat2 = new Mat();
                
                // Ensure blur kernel size is odd
                int kernelSize1 = blurAmount1 * 2 + 1;
                int kernelSize2 = blurAmount2 * 2 + 1;
                
                // Apply Gaussian blur
                if (kernelSize1 > 1) {
                    GaussianBlur(mat1, blurredMat1, new Size(kernelSize1, kernelSize1), 0);
                } else {
                    mat1.copyTo(blurredMat1);
                }
                
                if (kernelSize2 > 1) {
                    GaussianBlur(mat2, blurredMat2, new Size(kernelSize2, kernelSize2), 0);
                } else {
                    mat2.copyTo(blurredMat2);
                }
                
                // Create output Mat
                Mat outputMat = new Mat();
                
                // Blend the two blurred frames
                addWeighted(blurredMat1, 1.0 - alpha, blurredMat2, alpha, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame blendedFrame = converter.convert(outputMat);
                recorder.record(blendedFrame);
                
                // Release resources
                mat1.release();
                mat2.release();
                blurredMat1.release();
                blurredMat2.release();
                outputMat.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }

    /**
     * Creates a whip pan transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @param direction Direction of the whip ("left", "right", "up", "down")
     * @param blurStrength Strength of the motion blur (10-100 recommended)
     * @throws Exception If an error occurs during processing
     */
    public static void applyWhipPan(String input1, String input2, String outputPath, double duration, String direction, int blurStrength) throws Exception {
        // Validate direction
        if (!direction.equals("left") && !direction.equals("right") && 
            !direction.equals("up") && !direction.equals("down")) {
            direction = "left"; // Default to left if invalid
        }
        
        // Ensure blur strength is within reasonable bounds
        int blur = Math.min(100, Math.max(10, blurStrength));
        
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Apply whip pan effect
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            // Create a larger canvas for the whip effect
            int canvasWidth = width * 2;
            int canvasHeight = height * 2;
            
            for (int i = 0; i < transitionFrames; i++) {
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();
                
                if (frame1 == null || frame2 == null) break;
                
                // Convert frames to OpenCV Mat
                Mat mat1 = converter.convert(frame1);
                Mat mat2 = converter.convert(frame2);
                
                // Calculate progress for the transition
                double progress = (double) i / transitionFrames;
                
                // Create a canvas for the whip effect
                Mat canvas = new Mat(canvasHeight, canvasWidth, mat1.type(), new Scalar(0, 0, 0, 0));
                
                // Calculate positions for both videos based on direction and progress
                int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                
                switch (direction) {
                    case "left":
                        x1 = (int)(width - width * progress);
                        y1 = height / 2;
                        x2 = (int)(width * 2 - width * progress);
                        y2 = height / 2;
                        break;
                    case "right":
                        x1 = (int)(width * progress);
                        y1 = height / 2;
                        x2 = (int)(width * progress - width);
                        y2 = height / 2;
                        break;
                    case "up":
                        x1 = width / 2;
                        y1 = (int)(height - height * progress);
                        x2 = width / 2;
                        y2 = (int)(height * 2 - height * progress);
                        break;
                    case "down":
                        x1 = width / 2;
                        y1 = (int)(height * progress);
                        x2 = width / 2;
                        y2 = (int)(height * progress - height);
                        break;
                }
                
                // Create regions of interest for placing the videos
                Rect roi1 = new Rect(x1, y1, width, height);
                Rect roi2 = new Rect(x2, y2, width, height);
                
                // Place videos on the canvas
                Mat canvasRoi1 = new Mat(canvas, roi1);
                Mat canvasRoi2 = new Mat(canvas, roi2);
                
                mat1.copyTo(canvasRoi1);
                mat2.copyTo(canvasRoi2);
                
                // Apply motion blur
                Mat blurredCanvas = new Mat();
                
                // Calculate blur kernel size based on progress (max at middle of transition)
                double blurFactor = 1.0 - Math.abs(progress - 0.5) * 2.0;
                int kernelSize = (int)(blur * blurFactor) * 2 + 1;
                
                // Apply directional motion blur
                if (kernelSize > 1) {
                    Point anchor;
                    Size ksize;
                    
                    if (direction.equals("left") || direction.equals("right")) {
                        ksize = new Size(kernelSize, 1);
                        anchor = new Point(-1, 0);
                    } else {
                        ksize = new Size(1, kernelSize);
                        anchor = new Point(0, -1);
                    }
                    
                    Mat kernel = getMotionBlurKernel(ksize, anchor);
                    filter2D(canvas, blurredCanvas, -1, kernel);
                    kernel.release();
                } else {
                    canvas.copyTo(blurredCanvas);
                }
                
                // Crop the result to original size
                Rect cropRect = new Rect(width / 2, height / 2, width, height);
                Mat result = new Mat(blurredCanvas, cropRect);
                
                // Convert back to Frame and record
                Frame outputFrame = converter.convert(result);
                recorder.record(outputFrame);
                
                // Release resources
                mat1.release();
                mat2.release();
                canvas.release();
                canvasRoi1.release();
                canvasRoi2.release();
                blurredCanvas.release();
                result.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }
    
    /**
     * Helper method to create a motion blur kernel
     */
    private static Mat getMotionBlurKernel(Size ksize, Point anchor) {
        Mat kernel = new Mat(ksize, CV_32F);
        kernel.put(0, 0, 0);
        
        int size = (int)(ksize.width() > ksize.height() ? ksize.width() : ksize.height());
        for (int i = 0; i < size; i++) {
            kernel.put(0, i, 1.0f / size);
        }
        
        return kernel;
    }

    /**
     * Creates a spin transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @throws Exception If an error occurs during processing
     */
    public static void applySpin(String input1, String input2, String outputPath, double duration) throws Exception {
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Apply spin effect
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            for (int i = 0; i < transitionFrames; i++) {
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();
                
                if (frame1 == null || frame2 == null) break;
                
                // Convert frames to OpenCV Mat
                Mat mat1 = converter.convert(frame1);
                Mat mat2 = converter.convert(frame2);
                
                // Calculate progress for the transition
                double progress = (double) i / transitionFrames;
                
                // Calculate rotation angles
                double angle1 = progress * 90.0; // First video rotates 0 to 90 degrees
                double angle2 = (1.0 - progress) * -90.0; // Second video rotates -90 to 0 degrees
                
                // Get rotation matrices
                Point center = new Point(width / 2, height / 2);
                Mat rotationMatrix1 = getRotationMatrix2D(center, angle1, 1.0);
                Mat rotationMatrix2 = getRotationMatrix2D(center, angle2, 1.0);
                
                // Apply rotation
                Mat rotatedMat1 = new Mat();
                Mat rotatedMat2 = new Mat();
                warpAffine(mat1, rotatedMat1, rotationMatrix1, new Size(width, height), INTER_LINEAR, BORDER_CONSTANT, new Scalar(0, 0, 0, 0));
                warpAffine(mat2, rotatedMat2, rotationMatrix2, new Size(width, height), INTER_LINEAR, BORDER_CONSTANT, new Scalar(0, 0, 0, 0));
                
                // Create output Mat
                Mat outputMat = new Mat();
                
                // Blend the two rotated frames
                addWeighted(rotatedMat1, 1.0 - progress, rotatedMat2, progress, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame blendedFrame = converter.convert(outputMat);
                recorder.record(blendedFrame);
                
                // Release resources
                mat1.release();
                mat2.release();
                rotationMatrix1.release();
                rotationMatrix2.release();
                rotatedMat1.release();
                rotatedMat2.release();
                outputMat.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }

    /**
     * Creates a light flash transition between two videos
     * 
     * @param input1 Path to the first video
     * @param input2 Path to the second video
     * @param outputPath Path for the output video
     * @param duration Duration of the transition in seconds
     * @throws Exception If an error occurs during processing
     */
    public static void applyLightFlash(String input1, String input2, String outputPath, double duration) throws Exception {
        // Create frame grabbers for input videos
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(input1);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(input2);
        
        // Start the grabbers
        grabber1.start();
        grabber2.start();
        
        // Get video properties
        int width = grabber1.getImageWidth();
        int height = grabber1.getImageHeight();
        double frameRate = grabber1.getVideoFrameRate();
        
        // Calculate transition frames
        int transitionFrames = (int)(duration * frameRate);
        int halfTransitionFrames = transitionFrames / 2;
        
        // Create frame recorder for output video
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(grabber1.getVideoCodec());
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setVideoBitrate(grabber1.getVideoBitrate());
        recorder.start();
        
        try {
            // First part: Copy frames from first video (except last 'transitionFrames')
            int firstVideoTotalFrames = grabber1.getLengthInFrames();
            int firstVideoFramesToKeep = firstVideoTotalFrames - transitionFrames;
            
            // Process first video frames before transition
            for (int i = 0; i < firstVideoFramesToKeep; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
            
            // Transition part: Apply light flash effect
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            // First half: Fade first video to white
            for (int i = 0; i < halfTransitionFrames; i++) {
                Frame frame = grabber1.grab();
                if (frame == null) break;
                
                // Convert frame to OpenCV Mat
                Mat mat = converter.convert(frame);
                
                // Calculate progress for the fade to white
                double progress = (double) i / halfTransitionFrames;
                
                // Create white mat
                Mat whiteMat = new Mat(height, width, mat.type(), new Scalar(255, 255, 255, 255));
                
                // Blend with white
                Mat outputMat = new Mat();
                addWeighted(mat, 1.0 - progress, whiteMat, progress, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame outputFrame = converter.convert(outputMat);
                recorder.record(outputFrame);
                
                // Release resources
                mat.release();
                whiteMat.release();
                outputMat.release();
            }
            
            // Second half: Fade from white to second video
            // Skip frames from the first video that we won't use
            for (int i = 0; i < halfTransitionFrames; i++) {
                grabber1.grab();
            }
            
            for (int i = 0; i < halfTransitionFrames; i++) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                
                // Convert frame to OpenCV Mat
                Mat mat = converter.convert(frame);
                
                // Calculate progress for the fade from white
                double progress = (double) i / halfTransitionFrames;
                
                // Create white mat
                Mat whiteMat = new Mat(height, width, mat.type(), new Scalar(255, 255, 255, 255));
                
                // Blend from white
                Mat outputMat = new Mat();
                addWeighted(whiteMat, 1.0 - progress, mat, progress, 0.0, outputMat);
                
                // Convert back to Frame and record
                Frame outputFrame = converter.convert(outputMat);
                recorder.record(outputFrame);
                
                // Release resources
                mat.release();
                whiteMat.release();
                outputMat.release();
            }
            
            // Last part: Copy remaining frames from second video
            while (true) {
                Frame frame = grabber2.grab();
                if (frame == null) break;
                recorder.record(frame);
            }
        } finally {
            // Release resources
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        }
    }
}