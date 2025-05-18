import cv2
import numpy as np

def resize_frame(frame, size):
    return cv2.resize(frame, size, interpolation=cv2.INTER_LINEAR)

def ease_in_out(t):
    return 0.5 * (1 - np.cos(np.pi * t))

def blur_blend_frames(f1, f2, t, max_blur):
    t = ease_in_out(t)
    blended = cv2.addWeighted(f1, 1 - t, f2, t, 0)

    blur = int(t * max_blur)
    blur = max(1, blur | 1)  # Make sure blur size is odd and >= 1
    if blur > 1:
        blended = cv2.GaussianBlur(blended, (blur, blur), 0)

    return blended

def blur_transition_video(input_a, input_b, output_path, transition_duration=1.0, max_blur=25):
    cap_a = cv2.VideoCapture(input_a)
    cap_b = cv2.VideoCapture(input_b)

    if not cap_a.isOpened() or not cap_b.isOpened():
        print("Error opening one of the video files.")
        return

    fps = int(cap_a.get(cv2.CAP_PROP_FPS) or 30)
    width = int(max(cap_a.get(3), cap_b.get(3)))
    height = int(max(cap_a.get(4), cap_b.get(4)))
    size = (width, height)

    out = cv2.VideoWriter(output_path, cv2.VideoWriter_fourcc(*'mp4v'), fps, size)

    frames_a, frames_b = [], []

    while True:
        ret, frame = cap_a.read()
        if not ret:
            break
        frames_a.append(resize_frame(frame, size))

    while True:
        ret, frame = cap_b.read()
        if not ret:
            break
        frames_b.append(resize_frame(frame, size))

    cap_a.release()
    cap_b.release()

    transition_frames = int(fps * transition_duration)

    # Write initial part of video A
    for i in range(len(frames_a) - transition_frames):
        out.write(frames_a[i])

    # Perform blur transition
    for i in range(transition_frames):
        t = i / transition_frames
        f1 = frames_a[-transition_frames + i]
        f2 = frames_b[i]
        blended = blur_blend_frames(f1, f2, t, max_blur)
        out.write(blended)

    
    for i in range(transition_frames, len(frames_b)):
        out.write(frames_b[i])

    out.release()
    print("Blur transition complete! Output saved to:", output_path)

# Example usage
if __name__ == "__main__":
    input_a = 'input_videos/clip_a.mp4'
    input_b = 'input_videos/clip_b.mp4'
    output = 'output_videos/blur_output.mp4'
    blur_transition_video(input_a, input_b, output, transition_duration=1.5, max_blur=35)
