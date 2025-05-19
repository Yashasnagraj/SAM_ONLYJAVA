import cv2
import numpy as np

def zoom_frame(frame, zoom_factor):
    h, w = frame.shape[:2]
    center_x, center_y = w // 2, h // 2
    new_w, new_h = int(w / zoom_factor), int(h / zoom_factor)
    x1 = center_x - new_w // 2
    y1 = center_y - new_h // 2
    cropped = frame[y1:y1 + new_h, x1:x1 + new_w]
    return cv2.resize(cropped, (w, h))

def combine_videos_with_zoom(video1_path, video2_path, output_path, transition_duration=1.5):
    cap1 = cv2.VideoCapture(video1_path)
    cap2 = cv2.VideoCapture(video2_path)

    fps = int(cap1.get(cv2.CAP_PROP_FPS))
    width = int(cap1.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap1.get(cv2.CAP_PROP_FRAME_HEIGHT))
    frame_count1 = int(cap1.get(cv2.CAP_PROP_FRAME_COUNT))
    frame_count2 = int(cap2.get(cv2.CAP_PROP_FRAME_COUNT))
    transition_frames = int(fps * transition_duration)

    cap2.set(cv2.CAP_PROP_FRAME_WIDTH, width)
    cap2.set(cv2.CAP_PROP_FRAME_HEIGHT, height)

    out = cv2.VideoWriter(output_path, cv2.VideoWriter_fourcc(*'mp4v'), fps, (width, height))

    # Read frames from video1 up to zoom transition
    frames1 = []
    for _ in range(frame_count1 - transition_frames):
        ret, frame = cap1.read()
        if not ret:
            break
        out.write(frame)

    # Collect last N frames from video1 for zoom-out
    transition_clip1 = []
    for _ in range(transition_frames):
        ret, frame = cap1.read()
        if not ret:
            break
        transition_clip1.append(frame)
    cap1.release()

    # Collect first N frames from video2 for zoom-in
    transition_clip2 = []
    for _ in range(transition_frames):
        ret, frame = cap2.read()
        if not ret:
            break
        frame = cv2.resize(frame, (width, height))
        transition_clip2.append(frame)

    # Write transition frames
    for i in range(transition_frames):
        # Zoom-out from video1 (start from normal to zoomed out)
        zf1 = 1 + (i / transition_frames) * 0.5  # 1.0 to 1.5
        frame1 = zoom_frame(transition_clip1[i], zf1)

        # Zoom-in to video2 (start from zoomed in to normal)
        zf2 = 1.5 - (i / transition_frames) * 0.5  # 1.5 to 1.0
        frame2 = zoom_frame(transition_clip2[i], zf2)

        # Crossfade blend
        alpha = i / transition_frames
        blended = cv2.addWeighted(frame1, 1 - alpha, frame2, alpha, 0)
        out.write(blended)

    # Write remaining frames of video2
    for _ in range(frame_count2 - transition_frames):
        ret, frame = cap2.read()
        if not ret:
            break
        frame = cv2.resize(frame, (width, height))
        out.write(frame)

    cap2.release()
    out.release()
    print(" Output saved to:", output_path)

# Usage
combine_videos_with_zoom(
    "/Users/sachingc/Desktop/SamSung/videos/nature.mp4",
    "/Users/sachingc/Desktop/SamSung/videos/sachin.mp4",
    "zoom_output.mp4",
    transition_duration=1.5
)
