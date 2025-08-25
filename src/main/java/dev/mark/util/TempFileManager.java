package dev.mark.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TempFileManager {
    private static final String TEMP_DIR = "temp";
    private static final List<String> tempFiles = new ArrayList<>();

    static {
        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create directory for temporary files: " + e.getMessage());
        }
    }

    public static String createTempFile(String originalFileName) throws IOException {
        String extension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        String filePath = TEMP_DIR + File.separator + fileName;

        File file = new File(filePath);
        if (!file.createNewFile()) {
            throw new IOException("Failed to create temporary file: " + filePath);
        }

        synchronized (tempFiles) {
            tempFiles.add(filePath);
        }

        return filePath;
    }

    public static String generateOutputPath(String inputPath, String outputExtension) {
        String baseName = getBaseName(inputPath);
        String outputFileName = baseName + "_converted." + outputExtension;
        String outputPath = TEMP_DIR + File.separator + outputFileName;

        synchronized (tempFiles) {
            tempFiles.add(outputPath);
        }

        return outputPath;
    }

    public static void cleanup() {
        synchronized (tempFiles) {
            tempFiles.forEach(TempFileManager::deleteFile);
            tempFiles.clear();
        }
    }

    private static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                System.out.println("Deleted temporary file: " + file.getName());
            }
        } catch (Exception e) {
            System.err.println("Failed to delete temporary file: " + filePath + ", error: " + e.getMessage());
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    private static String getBaseName(String filePath) {
        String fileName = new File(filePath).getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }
}