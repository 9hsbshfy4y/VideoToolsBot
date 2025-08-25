package dev.mark.interfaces;

import java.io.IOException;

@SuppressWarnings("unused")
public interface IMediaConverter {
    void convert(String inputPath, String outputPath) throws IOException;
    boolean isSupported(String inputPath);
}