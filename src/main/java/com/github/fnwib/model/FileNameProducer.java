package com.github.fnwib.model;

@FunctionalInterface
public interface FileNameProducer {

	String getFilename(String filename, String suffix);
}
