package dev.mark.converters;

import dev.mark.abstracts.Converter;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class video2gif extends Converter {
    private static final int MAX_WIDTH = 480;
    private static final int DEFAULT_FPS = 15;
    private static final int MAX_DURATION_SECONDS = 10;

    @Override
    public void convert(String inputPath, String outputPath) throws IOException {
        validateInput(inputPath, "mp4", "avi", "mov", "mkv");
        validateFileSize(inputPath, MAX_FILE_SIZE);
        ensureOutputDirectory(outputPath);

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        if (!inputFile.exists()) {
            throw new IOException("Video file not found");
        }

        String palettePath = outputFile.getAbsolutePath().replace(".gif", "_palette.png");

        String paletteCmd = String.format(
                "ffmpeg -y -i \"%s\" -t %d -vf \"fps=%d,scale=%d:-1:flags=lanczos,palettegen\" -f image2 \"%s\"",
                inputFile.getAbsolutePath(), MAX_DURATION_SECONDS, DEFAULT_FPS, MAX_WIDTH, palettePath
        );

        String convertCmd = String.format(
                "ffmpeg -y -i \"%s\" -i \"%s\" -t %d -filter_complex \"fps=%d,scale=%d:-1:flags=lanczos[x];[x][1:v]paletteuse\" \"%s\"",
                inputFile.getAbsolutePath(), palettePath, MAX_DURATION_SECONDS, DEFAULT_FPS, MAX_WIDTH, outputFile.getAbsolutePath()
        );

        execute(paletteCmd);
        execute(convertCmd);

        File paletteFile = new File(palettePath);
        if (paletteFile.exists()) {
            if (!paletteFile.delete()) {
                System.err.println("Failed to delete file: " + palettePath);
            }
        }
    }

    private void execute(String command) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Error converting video to GIF via ffmpeg. Exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Converting canceled", e);
        }
    }

    @Override
    public boolean isSupported(String inputPath) {
        return inputPath.toLowerCase().matches(".*\\.(mp4|avi|mov|mkv)$");
    }
}