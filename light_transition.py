
import cv2
import numpy as np

def light_flash_smooth_transition(input_a_path, input_b_path, output_path,
                                  transition_duration=1.5,
                                  flash_color=(255, 255, 255)):
    cap_a = cv2.VideoCapture(input_a_path)
    cap_b = cv2.VideoCapture(input_b_path)

    if not cap_a.isOpened() or not cap_b.isOpened():
        raise IOError("Error opening one of the video files.")

    fps = int(cap_a.get(cv2.CAP_PROP_FPS) or 30)
    size = (
        int(max(cap_a.get(cv2.CAP_PROP_FRAME_WIDTH), cap_b.get(cv2.CAP_PROP_FRAME_WIDTH))),
        int(max(cap_a.get(cv2.CAP_PROP_FRAME_HEIGHT), cap_b.get(cv2.CAP_PROP_FRAME_HEIGHT)))
    )

    # Split transition duration into two phases
    total_transition_frames = int(fps * transition_duration)
    flash_frames = total_transition_frames // 2
    fade_frames = total_transition_frames - flash_frames

    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    out = cv2.VideoWriter(output_path, fourcc, fps, size)

  
    total_frames_a = int(cap_a.get(cv2.CAP_PROP_FRAME_COUNT))
    for i in range(max(0, total_frames_a - flash_frames)):
        ret, frame = cap_a.read()
        if not ret:
            break
        out.write(cv2.resize(frame, size))

  
    flash_frame = np.full((size[1], size[0], 3), flash_color, dtype=np.uint8)
    for i in range(flash_frames):
        ret, frame = cap_a.read()
        if not ret:
            frame = flash_frame
        alpha = i / flash_frames
        blended = cv2.addWeighted(cv2.resize(frame, size), 1 - alpha, flash_frame, alpha, 0)
        out.write(blended)

    
    for i in range(fade_frames):
        ret, frame_b = cap_b.read()
        if not ret:
            break
        alpha = (i + 1) / fade_frames
        blended = cv2.addWeighted(flash_frame, 1 - alpha, cv2.resize(frame_b, size), alpha, 0)
        out.write(blended)

    
    while True:
        ret, frame_b = cap_b.read()
        if not ret:
            break
        out.write(cv2.resize(frame_b, size))

    cap_a.release()
    cap_b.release()
    out.release()

    print(" Smooth light flash transition complete! Saved to:", output_path)


if __name__ == "__main__":
    input_a = 'input_videos/clip_a.mp4'
    input_b = 'input_videos/clip_b.mp4'
    output = 'output_videos/light_output.mp4'
    light_flash_smooth_transition(input_a, input_b, output, transition_duration=1.5)
