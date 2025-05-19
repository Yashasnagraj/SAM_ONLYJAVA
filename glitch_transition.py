import cv2
import numpy as np
import random
import os

def resize_frame(frame, size):
    return cv2.resize(frame, size, interpolation=cv2.INTER_LINEAR)

def glitch_blend_frames(f1, f2, t, glitch_strength=30):
    height, width, _ = f1.shape
    blended = cv2.addWeighted(f1, 1 - t, f2, t, 0)

    # Add glitch effect by shifting rows
    for _ in range(random.randint(10, 30)):
        y = random.randint(0, height - 10)
        h = random.randint(1, 10)
        dx = random.randint(-glitch_strength, glitch_strength)
        blended[y:y + h] = np.roll(blended[y:y + h], dx, axis=1)

    # Red-blue channel glitch
    b, g, r = cv2.split(blended)
    r = np.roll(r, random.randint(-5, 5), axis=1)
    b = np.roll(b, random.randint(-5, 5), axis=0)
    return cv2.merge([b, g, r])

def glitch_transition_video(input_a, input_b, output_path, transition_duration=1.0, glitch_strength=30):
    print("Opening video A:", input_a)
    print("Opening video B:", input_b)

    cap_a = cv2.VideoCapture(input_a)
    cap_b = cv2.VideoCapture(input_b)

    if not cap_a.isOpened() or not cap_b.isOpened():
        print("Error opening one of the video files.")
        return

    fps = int(cap_a.get(cv2.CAP_PROP_FPS) or 30)
    width = int(max(cap_a.get(cv2.CAP_PROP_FRAME_WIDTH), cap_b.get(cv2.CAP_PROP_FRAME_WIDTH)))
    height = int(max(cap_a.get(cv2.CAP_PROP_FRAME_HEIGHT), cap_b.get(cv2.CAP_PROP_FRAME_HEIGHT)))
    size = (width, height)

    out = cv2.VideoWriter(output_path, cv2.VideoWriter_fourcc(*'mp4v'), fps, size)

    frames_a = []
    while True:
        ret, frame = cap_a.read()
        if not ret:
            break
        frames_a.append(resize_frame(frame, size))
    cap_a.release()

    frames_b = []
    while True:
        ret, frame = cap_b.read()
        if not ret:
            break
        frames_b.append(resize_frame(frame, size))
    cap_b.release()

    transition_frames = int(fps * transition_duration)

    # Write all frames from video A except last transition frames
    for i in range(len(frames_a) - transition_frames):
        out.write(frames_a[i])

    # Glitch transition between last frames of A and first frames of B
    for i in range(transition_frames):
        t = i / transition_frames
        f1 = frames_a[len(frames_a) - transition_frames + i]
        f2 = frames_b[i]
        glitched = glitch_blend_frames(f1, f2, t, glitch_strength)
        out.write(glitched)

    # Write remaining frames from video B after transition
    for i in range(transition_frames, len(frames_b)):
        out.write(frames_b[i])

    out.release()
    print("✅ Glitch transition video saved at:", output_path)

if __name__ == "__main__":
    input_a = r"D:\Samsung\GlitchVideoProject\input_videos\a.mp4"
    input_b = r"D:\Samsung\GlitchVideoProject\input_videos\b.mp4"
    output = r"D:\Samsung\GlitchVideoProject\output_videos\glitch_output.mp4"
    glitch_transition_video(input_a, input_b, output, transition_duration=1.2, glitch_strength=25)
