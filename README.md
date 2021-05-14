# spot-livestream-pi

Research was focused on planning and implementing "on the fly" Spot livestreaming capabilities to Twitch servers. This means, rather than having a dedicated laptop/workstation have to follow around Spot, a small device could be mounted to Spot and triggered for immediate livestreaming to www.twitch.tv servers.

Twitch is by far the most popular livestreaming service on the web, and a successful on the fly implementation of Spot's cam to this service would be great for highlighting Mizzou's work with Spot. If successfully implemented, the project could be taken further by running a chat bot system on the mounted device that could elevate and give certain twitch chat users privileges to control spot functionalities such as movement or camera options offered in the development kit. Integration for these things should be easy into to students' work such as https://github.com/sfbowen4.

Hardware: 
Raspberry Pi 4B:
A group of students has already successfully mounted a pi to spot and successfully powered it through one of spot's interfaces. It can be used as a networked "brain" for spot. Read more about this here: https://github.com/MURobotics/Spot-Pi

While using the Raspberry Pi 4B would be the ideal scenario for this project, much research and planning was put into this without success. Along with Kyle Malisos, another research student, we attempted a variety of different methods of getting Spot's cam feed to twitch using the pi.

Spot cam uses H264 encoding while twitch servers require 

The first problem we encountered with using the pi is that Spot automatically self-enodes it's camera feed using H.264 with a very high bitrate (although the bitrate can be changed). While documentation shows that the pi can decode up to H264 1080@60, we were never able to get the pi to succesffuly playback Spot's video feed when authenticated to https://10.0.0.4:31102/h264.sdp/. Spot cam indicates it is 4k, so if internally we were able to change the resolution we may be able to get this to work, however the only capabilities we were able to find was to change the fps and the bitrate. In the case that somebody was able to get the pi to decode spot's stream, it is unknown whether it also has the hardware capable of the RTMP encoding required to push the feed to twitch. However, this should be looked into because neither me nor Kyle were very knowledgeable about video encoding and this may very well be possible, we were just hitting too many issues with it.
