import cv2
import numpy as np
import os

def resize_frame(frame, size):
    return cv2.resize(frame, size, interpolation=cv2.INTER_LINEAR)

def ease_in_out(t):
    return 0.5 * (1 - np.cos(np.pi * t))

def whip_blend_frames(f1, f2, t, direction, blur_strength):
    height, width = f1.shape[:2]
    t = ease_in_out(t)

    dx = int(t * width)
    dy = int(t * height)

    if direction == 'left':
        shifted1 = np.roll(f1, -dx, axis=1)
        shifted2 = np.roll(f2, width - dx, axis=1)
    elif direction == 'right':
        shifted1 = np.roll(f1, dx, axis=1)
        shifted2 = np.roll(f2, -width + dx, axis=1)
    elif direction == 'up':
        shifted1 = np.roll(f1, -dy, axis=0)
        shifted2 = np.roll(f2, height - dy, axis=0)
    elif direction == 'down':
        shifted1 = np.roll(f1, dy, axis=0)
        shifted2 = np.roll(f2, -height + dy, axis=0)
    else:
        raise ValueError("Invalid direction")

    blend = cv2.addWeighted(shifted1, 1 - t, shifted2, t, 0)

    # Add motion blur
    if blur_strength > 0:
        blur = int((1 - abs(0.5 - t) * 2) * blur_strength)
        blur = max(1, blur | 1)  # Ensure odd number
        blend = cv2.GaussianBlur(blend, (blur, blur), 0)

    return blend

def create_transition_video(input_a, input_b, output_path, transition_duration=1.0, blur_strength=35, direction='left'):
    cap_a = cv2.VideoCapture(input_a)
    cap_b = cv2.VideoCapture(input_b)

    if not cap_a.isOpened() or not cap_b.isOpened():
        print(" Error opening one of the videos.")
        return

    fps_a = cap_a.get(cv2.CAP_PROP_FPS)
    fps_b = cap_b.get(cv2.CAP_PROP_FPS)
    fps = int(fps_a or fps_b or 30)

    width = int(max(cap_a.get(3), cap_b.get(3)))
    height = int(max(cap_a.get(4), cap_b.get(4)))
    size = (width, height)

    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    out = cv2.VideoWriter(output_path, fourcc, fps, size)

    frames_a = []
    while True:
        ret, frame = cap_a.read()
        if not ret:
            break
        frames_a.append(resize_frame(frame, size))

    frames_b = []
    while True:
        ret, frame = cap_b.read()
        if not ret:
            break
        frames_b.append(resize_frame(frame, size))

    cap_a.release()
    cap_b.release()

    transition_frames = int(fps * transition_duration)

    print(" Writing video A (before transition)")
    for i in range(len(frames_a) - transition_frames):
        out.write(frames_a[i])

    print(" Blending transition")
    for i in range(transition_frames):
        t = i / transition_frames
        f1 = frames_a[-transition_frames + i]
        f2 = frames_b[i]
        blended = whip_blend_frames(f1, f2, t, direction, blur_strength)
        out.write(blended)

    print(" Writing video B (after transition)")
    for i in range(transition_frames, len(frames_b)):
        out.write(frames_b[i])

    out.release()
    print("Done! Saved to:", output_path)

if __name__ == '__main__':
    input_a = 'input_videos/clip_a.mp4'
    input_b = 'input_videos/clip_b.mp4'
    output = 'output_videos/whip_output.mp4'

    create_transition_video(input_a, input_b, output, transition_duration=1.0, blur_strength=100, direction='left')
