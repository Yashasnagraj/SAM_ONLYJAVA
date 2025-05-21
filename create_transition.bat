@echo off
echo Creating fade transition video...

ffmpeg -y -i "input_videos/clip_a.mp4" -i "input_videos/clip_b.mp4" -filter_complex "[0:v][1:v]xfade=transition=fade:duration=1.0:offset=3,format=yuv420p" -c:v libx264 -preset fast -crf 18 "output_videos/fade_output.mp4"

echo.
echo If the command was successful, the output video is saved at: output_videos/fade_output.mp4
echo.
echo To view the video, open it with your default video player.
