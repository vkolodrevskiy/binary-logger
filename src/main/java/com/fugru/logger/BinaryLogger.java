package com.fugru.logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * BinaryLogger logs serialized instances of {@link BinaryLoggable} into file. It does so in
 * such a way that it is possible to stream these instances back in an iterative fashion via
 * the {@link #read(File, Class)} method.
 */
public interface BinaryLogger<T extends BinaryLoggable> extends Closeable {
    /**
     * Writes the serialized instance.
     *
     * @param loggable an instance of {@link BinaryLoggable} that needs to be logged
     * @throws IOException if any IO operation fails
     */
    void write(T loggable) throws IOException;

    /**
     * Read and iterate through instances persisted in the given file.
     *
     * @param file  a file instance from which to read from
     * @param clazz a class of the type T, clazz should have a public no-arg constructor
     * @throws IOException if any IO operation fails
     */
    Iterator<T> read(File file, Class<T> clazz) throws IOException;
}
