@echo off
echo Creating glitch transition video...

ffmpeg -y -i "input_videos/clip_a.mp4" -i "input_videos/clip_b.mp4" -filter_complex "[0:v][1:v]xfade=transition=fade:duration=1.2:offset=3,noise=c0s=25:allf=t+u*2.5,rgbashift=rh=5:bh=5:gh=2,format=yuv420p" -c:v libx264 -preset fast -crf 18 "output_videos/glitch_output.mp4"

echo.
echo If the command was successful, the output video is saved at: output_videos/glitch_output.mp4
echo.
echo To view the video, open it with your default video player.
