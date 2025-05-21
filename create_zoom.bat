@echo off
echo Creating zoom transition video...

ffmpeg -y -i "input_videos/clip_a.mp4" -i "input_videos/clip_b.mp4" -filter_complex "[0:v]split[v0a][v0b];[v0b]trim=end=3,setpts=PTS-STARTPTS[v0trim];[v0a]trim=start=3,setpts=PTS-STARTPTS[v0main];[v0trim]scale=iw*1.5:ih*1.5,zoompan=z='min(zoom,1.5)':d=1:x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':s=iw:ih:fps=25[v0zoom];[1:v]scale=iw*1.5:ih*1.5,zoompan=z='1.5-min(0.5,t/1.5*0.5)':d=1:x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':s=iw:ih:fps=25[v1zoom];[v0main][v0zoom][v1zoom]concat=n=3:v=1:a=0,format=yuv420p" -c:v libx264 -preset fast -crf 18 "output_videos/zoom_output.mp4"

echo.
echo If the command was successful, the output video is saved at: output_videos/zoom_output.mp4
echo.
echo To view the video, open it with your default video player.
