import subprocess
import json

def get_video_info(filename):
    """Get duration, width, and height of a video file using ffprobe."""
    cmd = [
        "ffprobe", "-v", "error",
        "-select_streams", "v:0",
        "-show_entries", "format=duration",
        "-show_entries", "stream=width,height",
        "-of", "json",
        filename
    ]
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    info = json.loads(result.stdout)
    
    duration = float(info["format"]["duration"])
    width = int(info["streams"][0]["width"])
    height = int(info["streams"][0]["height"])
    
    return duration, width, height

def build_ffmpeg_command(video1, video2, output, transition_duration):
    # Get info for both videos
    duration1, width1, height1 = get_video_info(video1)
    duration2, width2, height2 = get_video_info(video2)

    # Use resolution of video1 for both videos
    common_width, common_height = width1, height1

    trim_duration = duration1 - transition_duration

    # filter_complex with scaling both videos to the resolution of video1
    filter_complex = rf"""
    [0:v]scale={common_width}:{common_height},setpts=PTS-STARTPTS,trim=duration={trim_duration}[v0];
    [0:v]scale={common_width}:{common_height},setpts=PTS-STARTPTS,trim=start={trim_duration},setpts=PTS-STARTPTS[v0rot];
    [1:v]scale={common_width}:{common_height},setpts=PTS-STARTPTS,trim=duration={transition_duration}[v1rot];
    [1:v]scale={common_width}:{common_height},setpts=PTS-STARTPTS,trim=start={transition_duration},setpts=PTS-STARTPTS[v1];

    [v0rot]rotate='PI/2*min(1,t/{transition_duration})':c=none:ow=iw:oh=ih:fillcolor=black,format=rgba,crop={common_width}:{common_height}[v0r];
    [v1rot]rotate='-PI/2*(1-min(1,t/{transition_duration}))':c=none:ow=iw:oh=ih:fillcolor=black,format=rgba,crop={common_width}:{common_height}[v1r];

    [v0r][v1r]blend=all_expr='if(between(T,0,{transition_duration}),A*(1-(T/{transition_duration}))+B*(T/{transition_duration}),B)'[vblend];

    [v0][vblend][v1]concat=n=3:v=1:a=0,format=yuv420p[out]
"""



    cmd = [
        "ffmpeg",
        "-i", video1,
        "-i", video2,
        "-filter_complex", filter_complex,
        "-map", "[out]",
        "-c:v", "libx264",
        "-preset", "fast",
        "-crf", "18",
        "-y",
        output
    ]
    return cmd

def run_transition(video1, video2, output, transition_duration=2):
    print(f"Processing transition: {video1} + {video2} with {transition_duration}s spin transition...")
    cmd = build_ffmpeg_command(video1, video2, output, transition_duration)
    # For debug: print the full command
    print("Running FFmpeg command:")
    print(" ".join(cmd))

    subprocess.run(cmd, check=True)
    print(f"Output saved to {output}")

if __name__ == "__main__":
    video1 = "video1.mp4"  # Change this to your first video filename
    video2 = "video2.mp4"  # Change this to your second video filename
    output = "spin_output.mp4"
    transition_duration = 2  # seconds
    
    run_transition(video1, video2, output, transition_duration)
