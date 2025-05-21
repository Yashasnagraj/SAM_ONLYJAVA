@echo off
echo Creating whip pan transition video...

ffmpeg -y -i "input_videos/clip_a.mp4" -i "input_videos/clip_b.mp4" -filter_complex "[0:v]split[v0a][v0b];[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];[v0trim][1:v]xfade=transition=slideleft:duration=1.0:offset=0,boxblur=luma_radius=min(100,max(0,1-abs(t-0.5)/0.5)*100):luma_power=1:enable='between(t,0,1.0)'[vblend];[v0main][vblend]concat=n=2:v=1:a=0,format=yuv420p" -c:v libx264 -preset fast -crf 18 "output_videos/whip_output.mp4"

echo.
echo If the command was successful, the output video is saved at: output_videos/whip_output.mp4
echo.
echo To view the video, open it with your default video player.
