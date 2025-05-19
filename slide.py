import subprocess
import json

def get_video_duration(filename):
    """Returns the duration of a video file in seconds."""
    result = subprocess.run(
        ["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "json", filename],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    duration = json.loads(result.stdout)["format"]["duration"]
    return float(duration)

def get_video_resolution(filename):
    """Returns width and height of a video."""
    result = subprocess.run(
        ["ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width,height", "-of", "json", filename],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    info = json.loads(result.stdout)
    width = info["streams"][0]["width"]
    height = info["streams"][0]["height"]
    return width, height

# Inputs
video1 = "video1.mp4"
video2 = "video2.mp4"
output = "output_transition.mp4"
transition_duration = 2

# Get duration and resolution
video1_duration = get_video_duration(video1)
transition_offset = video1_duration - transition_duration
width, height = get_video_resolution(video1)

# Compose FFmpeg command with scaling of video2
filter_complex = (
    f"[0:v]fps=30,scale={width}:{height}[v0];"
    f"[1:v]fps=30,scale={width}:{height}[v1];"
    f"[v0][v1]xfade=transition=slideleft:duration={transition_duration}:offset={transition_offset},format=yuv420p[v]"
)


cmd = [
    "ffmpeg",
    "-i", video1,
    "-i", video2,
    "-filter_complex", filter_complex,
    "-map", "[v]",
    "-map", "0:a?",  # Optional audio from video1
    "-c:v", "libx264",
    "-c:a", "aac",
    "-preset", "fast",
    "-crf", "18",
    output
]

# Run FFmpeg
subprocess.run(cmd)
