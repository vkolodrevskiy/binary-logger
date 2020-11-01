package com.fugru.logger.writer;

import java.io.File;

public class FileSizeRolloverStrategy implements RolloverStrategy {
    private long maxFileSize;
    private String originalFilePath;
    private File currentFile;

    private FileSizeRolloverStrategy() {
    }

    public FileSizeRolloverStrategy(String filePath, long maxFileSize) {
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("maxFileSize must be positive");
        }
        this.maxFileSize = maxFileSize;
        this.originalFilePath = filePath;
        this.currentFile = new File(filePath);
    }

    @Override
    public File getCurrentFile() {
        return currentFile;
    }

    @Override
    public boolean rollover() {
        if (currentFile.length() < maxFileSize) {
            return false;
        } else {
            currentFile = rolloverFileName();
            return true;
        }
    }

    private File rolloverFileName() {
        File file = new File(originalFilePath);
        return new File(file.getAbsoluteFile() + "." + System.currentTimeMillis());
    }
}
