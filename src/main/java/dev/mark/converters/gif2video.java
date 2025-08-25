package dev.mark.converters;

import dev.mark.abstracts.Converter;
import java.io.File;
import java.io.IOException;

public class gif2video extends Converter {

    @Override
    public void convert(String inputPath, String outputPath) throws IOException {
        validateInput(inputPath, "gif");
        validateFileSize(inputPath, MAX_FILE_SIZE);
        ensureOutputDirectory(outputPath);

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        if (!inputFile.exists()) {
            throw new IOException("GIF file not found");
        }

        String cmd = String.format(
                "ffmpeg -y -i \"%s\" -movflags faststart -pix_fmt yuv420p -c:v libx264 -crf 23 \"%s\"",
                inputFile.getAbsolutePath(),
                outputFile.getAbsolutePath()
        );

        execute(cmd);
    }

    private void execute(String command) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Error converting GIF to MP4 via ffmpeg. Exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Conversion interrupted", e);
        }
    }

    @Override
    public boolean isSupported(String inputPath) {
        return inputPath.toLowerCase().endsWith(".gif");
    }
}