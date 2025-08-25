package dev.mark.model;

import lombok.Getter;
import java.util.List;

@Getter
public enum ConversionType {
    MP4_TO_GIF("🎬 MP4 → GIF", "gif"),
    AVI_TO_GIF("🎥 AVI → GIF", "gif"),
    MOV_TO_GIF("📹 MOV → GIF", "gif"),
    MKV_TO_GIF("🎞️ MKV → GIF", "gif"),
    GIF_TO_MP4("🎭 GIF → MP4", "mp4");

    private final String displayName;
    private final String outputExtension;

    ConversionType(String displayName, String outputExtension) {
        this.displayName = displayName;
        this.outputExtension = outputExtension;
    }

    public static ConversionType fromString(String typeString) {
        try {
            return valueOf(typeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown type of convert: " + typeString);
        }
    }

    public static List<ConversionType> getSupportedConversions(String inputExtension) {
        return switch (inputExtension.toLowerCase()) {
            case "mp4" -> List.of(MP4_TO_GIF);
            case "avi" -> List.of(AVI_TO_GIF);
            case "mov" -> List.of(MOV_TO_GIF);
            case "mkv" -> List.of(MKV_TO_GIF);
            case "gif" -> List.of(GIF_TO_MP4);
            default -> List.of();
        };
    }
}