package net.openhft.chronicle.queue.impl;

import net.openhft.chronicle.bytes.MappedBytes;
import net.openhft.chronicle.core.ReferenceCounted;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.wire.Demarshallable;
import net.openhft.chronicle.wire.WriteMarshallable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface CommonStore extends ReferenceCounted, Demarshallable, WriteMarshallable, Closeable {

    /**
     * Returns the file associated with this store.
     *
     * @return the file associated with this store
     */
    @Nullable
    File file();

    /**
     * Returns the MappedBytes associated with this store.
     *
     * @return the MappedBytes associated with this store
     */
    @NotNull
    MappedBytes bytes();

    /**
     * Creates and returns a new String representation of this
     * store that is human readable.
     *
     * @return a new String representation of this
     *         store that is human readable
     * @see    #shortDump()
     */
    @NotNull
    String dump();

    /**
     * Creates and returns a new short String representation of this
     * store that is human readable.
     *
     * @return a new short String representation of this
     *         store that is human readable
     * @see    #dump()
     */
    @NotNull
    String shortDump();

}
