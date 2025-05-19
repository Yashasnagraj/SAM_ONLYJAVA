import cv2
import numpy as np

def apply_zoom_effect(frame, zoom_factor):
    h, w = frame.shape[:2]
    center_x, center_y = w // 2, h // 2

    # Size of the cropping box
    radius_x, radius_y = int(w / (2 * zoom_factor)), int(h / (2 * zoom_factor))

    min_x, max_x = center_x - radius_x, center_x + radius_x
    min_y, max_y = center_y - radius_y, center_y + radius_y

    # Crop and resize
    cropped = frame[min_y:max_y, min_x:max_x]
    zoomed = cv2.resize(cropped, (w, h), interpolation=cv2.INTER_LINEAR)
    return zoomed

# Input and output video paths
input_path = '/Users/sachingc/Desktop/SamSung/videos/nature.mp4'
output_path = 'zoomed_output_video.mp4'

cap = cv2.VideoCapture(input_path)

# VideoWriter setup
fourcc = cv2.VideoWriter_fourcc(*'mp4v')
fps = cap.get(cv2.CAP_PROP_FPS)
width  = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
out = cv2.VideoWriter(output_path, fourcc, fps, (width, height))

frame_count = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))

for i in range(frame_count):
    ret, frame = cap.read()
    if not ret:
        break

    # Optional: gradually increase zoom factor for dynamic zoom
    zoom_factor = 1 + 0.005 * i  # e.g., from 1.0 to 2.0 over 200 frames
    zoomed_frame = apply_zoom_effect(frame, zoom_factor)

    out.write(zoomed_frame)

cap.release()
out.release()
print("Zoom effect applied and saved to:", output_path)
