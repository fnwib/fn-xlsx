package com.github.fnwib.write.config;

@FunctionalInterface
public interface FileNameProducer {

    String getFilename(String filename, String suffix);
}
