package dev.mark.abstracts;

import dev.mark.interfaces.IMediaConverter;
import java.io.File;
import java.io.IOException;

public abstract class Converter implements IMediaConverter {
    protected static int MAX_FILE_SIZE = 50 * 1024 * 1024;

    protected void validateInput(String inputPath, String... expectedExtensions) throws IOException {
        File file = new File(inputPath);
        if (!file.exists()) {
            throw new IOException("Input file does not exist: " + inputPath);
        }

        if (!file.canRead()) {
            throw new IOException("Cannot read input file: " + inputPath);
        }

        boolean extensionMatch = false;
        String fileName = file.getName().toLowerCase();
        for (String ext : expectedExtensions) {
            if (fileName.endsWith("." + ext.toLowerCase())) {
                extensionMatch = true;
                break;
            }
        }

        if (!extensionMatch) {
            throw new IOException("Invalid file extension. Expected: " + String.join(", ", expectedExtensions));
        }
    }

    protected void ensureOutputDirectory(String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + parentDir.getAbsolutePath());
            }
        }
    }

    protected void validateFileSize(String filePath, long maxSizeInBytes) throws IOException {
        File file = new File(filePath);
        if (file.length() > maxSizeInBytes) {
            throw new IOException("File size exceeds the maximum allowed: " + (maxSizeInBytes / 1024 / 1024) + " MB");
        }
    }
}