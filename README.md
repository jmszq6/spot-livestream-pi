# spot-livestream-pi

## Background Info and Unsuccessful Attempts with the Pi ##

Research was focused on planning and implementing "on the fly" Spot livestreaming capabilities to Twitch servers. This means, rather than having a dedicated laptop/workstation have to follow around Spot, a small device could be mounted to Spot and triggered for immediate livestreaming to www.twitch.tv servers.

Twitch is by far the most popular livestreaming service on the web, and a successful on the fly implementation of Spot's cam to this service would be great for highlighting Mizzou's work with Spot. If successfully implemented, the project could be taken further by running a chat bot system on the mounted device that could elevate and give certain twitch chat users privileges to control spot functionalities such as movement or camera options offered in the development kit. Integration for these things should be easy into to students' work such as https://github.com/sfbowen4.

Hardware: 
Raspberry Pi 4B:
A group of students has already successfully mounted a pi to spot and successfully powered it through one of spot's interfaces. It can be used as a networked "brain" for spot. Read more about this here: https://github.com/MURobotics/Spot-Pi

While using the Raspberry Pi 4B would be the ideal scenario for this project, much research and planning was put into this without success. Along with Kyle Malisos, another research student, we attempted a variety of different methods of getting Spot's cam feed to twitch using the pi.

Spot cam uses H264 encoding and twitch servers require RTMP encoding.

The first problem we encountered with using the pi is that Spot automatically self-enodes it's camera feed using H.264 with a very high bitrate (although the bitrate can be changed). While documentation shows that the pi can decode up to H264 1080@60, we were never able to get the pi to succesffuly playback Spot's video feed when authenticated to https://10.0.0.4:31102/h264.sdp/. Spot cam indicates it is 4k, so if internally we were able to change the resolution we may be able to get this to work, however the only capabilities we were able to find was to change the fps and the bitrate. In the case that somebody was able to get the pi to decode spot's stream, it is unknown whether it also has the hardware capable of the RTMP encoding required to push the feed to twitch. However, this should be looked into because neither me nor Kyle were very knowledgeable about video transcoding and this may very well be possible, we were just hitting too many issues with it.

Transmuxing using ffmpeg may be possible to avoid decoding the stream, however it was not tested.

'''
class StreamQualitySetStreamParamsCommand(Command):
    """Set image quality and postprocessing settings"""

    NAME = 'set'
    AWB_MODE = ['off', 'auto', 'incandescent', 'fluorescent', 'warm_fluorescent', 'daylight', 'cloudy', 'twilight', 'shade', 'dark']

    def __init__(self, subparsers, command_dict):
        super(StreamQualitySetStreamParamsCommand, self).__init__(subparsers, command_dict)
        self._parser.add_argument('--target-bitrate', type=int, help='video compression (bits per second)')
        self._parser.add_argument('--refresh-interval', type=int, help='how often to update feed (in frames)')
        self._parser.add_argument('--idr-interval', type=int, help='how often to send IDR message (in frames)')
        self._parser.add_argument('--awb-mode', default='auto', const='auto', nargs='?', choices=StreamQualitySetStreamParamsCommand.AWB_MODE)

    def _run(self, robot, options):
        result = robot.ensure_client(StreamQualityClient.default_service_name).set_stream_params(options.target_bitrate, options.refresh_interval, options.idr_interval,
                                                                                                 StreamQualitySetStreamParamsCommand.AWB_MODE.index(options.awb_mode))

        return result
'''

The usage of cloud transcoding in an AWS EC2 instance triggered by a Lambda was also looked into upon realizing that the pi may not be sufficient for this project. 
We wanted to use the pi as TURN server so that our AWS instance could have a path to network to spot's cam using the WebRTC protocol, however we needed the pi to be port forwarded on Mizzou's network to make this a simple task (which simply wasn't going to happen). While there is likely a solution around this using some fancy SSH reverse SOCKS proxies with AWS, we thought it best to explore other hardware for the job that could simplify the whole process.

Luckily for us, Mizzou Robotics offered to let us borrow their Jetson Nano 2gb, which is a similar size to the pi and could be just as easily mounted on to spot.

## Using Jetson Nano 2GB to Stream to Twitch From Spot ##
### Requirements: Jetson Nano 2GB, USB WiFi adapter, UHS MicroSD card + writer, 5V 3A power to Jetson, ethernet cable, Twitch account ###

#### Theoretical workflow considering we also ran into dumb issues with this ####

Write Jetson Nano 2GB image to microSD card by following Nvidia documentation:
https://developer.nvidia.com/embedded/learn/get-started-jetson-nano-2gb-devkit#write

Connect USB Wifi adapter and install drivers (required because we will be networked to Spot via ethernet to get the WebRTC stream and using the WiFi adapter to be connected to the internet)

Install apache on the nano (requirement to locally host our WebRTC client which will retrieve the livestream from Spot's cam server)

> sudo apt-get update

> sudo apt-get install apache2

Set up routing tables so all calls to Spot's IP range route through the wired network interface and everything else goes through the USB adapter

Host the webrtc.html locally so it can be accessed via loaclhost (127.0.0.1) (this file will authenticate to spot's cam server and host a video feed)

Install the Nvidia FFMPEG package which supports GPU accelerated decoding
> sudo apt install ffmpeg

To get source files for the ffmpeg package
> apt source ffmpeg

To include the ffmpeg library in L4T builds
1.Add the following lines to /etc/apt/sources.list:
$ echo "deb https://repo.download.nvidia.com/jetson/ffmpeg main main" |  sudo tee -a /etc/apt/sources.list
$ echo "deb-src https://repo.download.nvidia.com/jetson/ffmpeg main main" |  sudo tee -a /etc/apt/sources.list
2.Enter the command:
> sudo apt update

Create a twitch account and locate your unique stream key from the dashboard

Select one of the following twitch ingest endpoints:
#### US East: Chicago, IL (3) rtmp://ord03.contribute.live-video.net/app/{stream_key} ####
#### US Central: Dallas, TX rtmp://dfw.contribute.live-video.net/app/{stream_key} ####
#### US East: Chicago, IL (2) rtmp://ord02.contribute.live-video.net/app/{stream_key} ####

A list of 10 servers can be found here, select the one closest to you: https://stream.twitch.tv/ingests/

FFMPEG to twitch ingest endpoint using the file we are hosting at 127.0.0.1 as the input and our ingest endpoint as the output file.

Reference the following document for selecting the proper ffmpeg settings for RTMP.
https://trac.ffmpeg.org/wiki/EncodingForStreamingSites


## Problems encountered ##
While transmuxing still needed to be tested using the pi (which may work by avoiding the decoding process altogether), we switched to using the Jetson Nano 2gb as we thought it had better hardware capabilities for this project and would inevitibly be used with Spot at some point in the future for other projects.

While we did not have a Nano on hand, MURobotics was nice enough to let us use theirs as long as it stayed locked in Dale's office.
However, even after many many hours of debugging and consulting outside help, we could not actually get the Nano to properly boot.
What we experienced was that upon giving it the proper power via micro-usb and inserting the MicroSD card with the Jetson image on it, it would simply boot to the Nvidia splash screen and then either bootloop and stay frozen on the splash screen.

We did a lot of research on why this was happening (this is actually a super common problem with these boards) and concluded it was likely due to one of three scenarios and tried all combinations of the following:

1) Problems with the MicroSD card (many users suggested they tried with up to 7 MicroSD cards and were only able to boot off of one of them)
    ### Sandisk Edge 16 GB ###
    ### Kingston 32 GB ###

2) Problems with the image we were using on the card (we tried several different versions straight from the Nvidia website specifically for the Nano 2GB)
    ### Jetson Nano 2GB Developer Kit SD Card Image 2021/02/24 4.5.1 (https://developer.nvidia.com/jetson-nano-2gb-sd-card-image) ###
    ### Jetson Nano 2GB Developer Kit SD Card Image 2021/01/21 4.5 (https://developer.nvidia.com/jetson-nano-2gb-sd-card-image-45) ###
    ### Jetson Nano 2GB Developer Kit SD Card Image 2020/10/21 4.4.1 (https://developer.nvidia.com/jetson-nano-2gb-jp441-sd-card-image) ###

3) Various power supplies ranging from 5V 2A to 5V 4A (many users noted increasing 2-3A up to 4A solved this issue, it did not for us)

We concluded that the board is either killed or we need to more various SD cards (we didn't have any more available).
Doing a quick google search reveals many hundreds of users experiencing the same exact problem as us. We attempted all solutions.

https://forums.developer.nvidia.com/t/nvidia-splash-screen-frozen/72000
https://forums.developer.nvidia.com/t/jetson-nano-2gb-stuck-on-boot-screen-with-nvidia-logo/169601
https://forums.balena.io/t/stuck-at-boot-menu-with-jetson-nano/236429
... and many more.

Due to these issues, the workflow posted under the Nano section is completely theoretical and untested. We plan to use our free time this summer and next semester to test out transmuxing on the pi and solving the problem with the Nano so that we can fully implement on the fly livestreaming from Spot and integrate it into other students' work.
