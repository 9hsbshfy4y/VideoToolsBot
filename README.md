# Video Converter Bot

A Telegram bot that converts **video files to GIFs** and **GIFs to video files**. 
Supports formats like MP4, AVI, MOV, MKV, and GIF. 

Started bot - @VideoTool_sBot

---

## Features

* Convert videos to GIF and GIFs to MP4.
* Supported formats:
  * Video → GIF: MP4, AVI, MOV, MKV
  * GIF → Video: MP4

* Conversion settings:
  * 15 FPS
  * Max duration: 10 seconds
  * Width: 480px (aspect ratio preserved)

* Handles files up to 50 MB.

---

## How to Use

1. Start the bot with `/start`.
2. Send a video or GIF file.
3. Select the conversion type.
4. Receive the converted file directly in Telegram.

Use `/help` for detailed instructions and tips.

---

## Installation & Setup

1. Clone this repository:

```bash
git clone https://github.com/9hsbshfy4y/VideoToolsBot.git
cd video-converter-bot
```

2. Build the project with Maven.
3. Set your **Telegram Bot API token** in `BotApplication.java`.
4. Make sure `ffmpeg` is installed and available in your system PATH:

```bash
ffmpeg -version
```

5. Run the bot:

```bash
java -jar target/video-converter-bot.jar
```

---

## Dependencies

* [TelegramBots](https://github.com/rubenlagus/TelegramBots) — Telegram Bot API
* [ffmpeg](https://ffmpeg.org/) — for video/GIF conversion
* Java 21+
