package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
 * %%
 * Copyright (C) 2020 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A byte output writes bytes to a {@link ByteBuffer}.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see BufferByteInput
 */
public class BufferByteOutput
        extends ByteOutputAdapter<ByteBuffer> {

    /**
     * Creates a new instance which writes bytes to the channel supplied by specified supplier.
     *
     * @param channelSupplier the supplier of the channel.
     * @return a new instance.
     * @apiNote Closing the result output also closes the {@code channel}.
     */
    public static BufferByteOutput adapting(final Supplier<? extends WritableByteChannel> channelSupplier) {
        final Supplier<ByteBuffer> bufferSupplier = () -> ByteBuffer.allocate(1);
        return new BufferByteOutputChannelAdapter(bufferSupplier, channelSupplier);
    }

    /**
     * Creates a new instance which writes bytes to specified channel.
     *
     * @param channel the source channel to which bytes are written.
     * @return a new instance.
     * @apiNote Closing the result output does not close the {@code channel}.
     */
    public static BufferByteOutput adapting(final WritableByteChannel channel) {
        Objects.requireNonNull(channel, "channel is null");
        final Supplier<ByteBuffer> bufferSupplier = () -> ByteBuffer.allocate(1);
        final BufferByteOutputChannelAdapter adapter
                = new BufferByteOutputChannelAdapter(bufferSupplier, BitIoUtils.empty());
        adapter.channel(channel);
        return adapter;
    }

    /**
     * Returns a new instance which writes bytes to specified path.
     *
     * @param path    the path to which bytes are written.
     * @param options an array of open options.
     * @return a new instance.
     */
    static ByteOutput from(final Path path, final OpenOption... options) {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(options, "options is null");
        return adapting(() -> {
            try {
                return FileChannel.open(path, options);
            } catch (final IOException ioe) {
                throw new RuntimeException("failed to open " + path, ioe);
            }
        });
    }

    /**
     * Returns a new instance which writes bytes to specified path.
     *
     * @param path the path to which bytes are written.
     * @return a new instance.
     * @implNote The {@code from(Path)} method invokes {@link #from(Path, OpenOption...)} method with {@code path} and
     * {@link StandardOpenOption#WRITE}.
     */
    static ByteOutput from(final Path path) {
        return from(path, StandardOpenOption.WRITE);
    }

    /**
     * Creates a new instance which writes bytes from specified buffer.
     *
     * @param target the buffer to which bytes are written.
     * @return a new instance.
     */
    public static BufferByteOutput from(final ByteBuffer target) {
        Objects.requireNonNull(target, "target is null");
        final BufferByteOutput instance = new BufferByteOutput(BitIoUtils.empty());
        instance.target(target);
        return instance;
    }

    /**
     * Creates a new instance with specified target supplier.
     *
     * @param targetSupplier the target supplier.
     */
    public BufferByteOutput(final Supplier<? extends ByteBuffer> targetSupplier) {
        super(targetSupplier);
    }

    /**
     * {@inheritDoc} The {@code write(ByteBuffer, int)} method of {@code BufferByteOutput} class invokes {@link
     * ByteBuffer#put(byte)} method on specified byte buffer with specified value as a {@code byte} value.
     *
     * @param target {@inheritDoc}
     * @param value  {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    protected void write(final ByteBuffer target, final int value) throws IOException {
        target.put((byte) value);
    }

    @Override
    void target(final ByteBuffer target) {
        if (Objects.requireNonNull(target, "target is null").capacity() == 0) {
            throw new IllegalArgumentException("target.capacity is zero");
        }
        super.target(target);
    }
}
