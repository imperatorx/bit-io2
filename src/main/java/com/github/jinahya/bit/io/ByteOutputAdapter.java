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

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

/**
 * An implementation of {@link BitOutput} writes octets to an instance of {@link ByteOutput}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ByteInputAdapter
 */
public class ByteOutputAdapter
        implements BitOutput {

    /**
     * Creates a new instance on top of specified stream.
     *
     * @param stream the stream to which bytes are written.
     * @return a new instance.
     */
    static BitOutput from(final OutputStream stream) {
        return new ByteOutputAdapter(new StreamByteOutput(stream));
    }

    /**
     * Creates a new instance on top of specified output.
     *
     * @param output the output to which bytes are written.
     * @return a new instance.
     */
    static BitOutput from(final DataOutput output) {
        return new ByteOutputAdapter(new DataByteOutput(output));
    }

    /**
     * Creates a new instance on top of specified buffer.
     *
     * @param buffer the buffer to which bytes are written.
     * @return a new instance.
     */
    static BitOutput from(final ByteBuffer buffer) {
        return new ByteOutputAdapter(new BufferByteOutput(buffer));
    }

    /**
     * Creates a new instance on top of specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @return a new instance.
     */
    static BitOutput from(final WritableByteChannel channel) {
        return new ByteOutputAdapter(new ChannelByteOutput(channel));
    }

    /**
     * Creates a new instance on top of specified byte output.
     *
     * @param output the byte output.
     */
    public ByteOutputAdapter(final ByteOutput output) {
        super();
        this.output = Objects.requireNonNull(output, "output is null");
    }

    @Override
    public void writeInt(final boolean unsigned, int size, int value) throws IOException {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        if (!unsigned) {
            writeInt(true, 1, value < 0 ? 1 : 0);
            if (--size > 0) {
                writeInt(true, size, value);
            }
            return;
        }
        final int quotient = size >> 3;
        final int remainder = size & 7;
        if (remainder > 0) {
            unsigned8(remainder, value >> (quotient << 3));
        }
        for (int i = Byte.SIZE * (quotient - 1); i >= 0; i -= Byte.SIZE) {
            unsigned8(Byte.SIZE, value >> i);
        }
    }

    @Override
    public long align(final int bytes) throws IOException {
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is not positive");
        }
        long bits = 0L; // the number of padded bits
        if (available < Byte.SIZE) {
            bits += available;
            writeInt(true, available, 0x00);
        }
        assert available == Byte.SIZE;
        if (bytes == 1) {
            return bits;
        }
        for (int i = (bytes - (int) (this.count % bytes)); i > 0; i--) {
            writeInt(true, Byte.SIZE, 0x00);
            bits += Byte.SIZE;
        }
        assert (count % bytes) == 0L;
        return bits;
    }

    /**
     * Writes specified unsigned value of specified number of bits.
     *
     * @param size  the number of bits to write; between {@code 1} and {@value java.lang.Byte#SIZE}, both inclusive.
     * @param value the value to write.
     * @throws IOException if an I/O error occurs.
     */
    private void unsigned8(final int size, final int value) throws IOException {
        assert size > 0 && size <= Byte.SIZE;
        if (size == Byte.SIZE && available == Byte.SIZE) { // write, a full 8-bit octet, directly to the output
            output.write(value);
            count++;
            assert octet == 0x00 : "'octet' should be remained as 0x00";
            assert available == Byte.SIZE : "'available' should be remained as Byte.SIZE";
            return;
        }
        final int required = size - available;
        if (required > 0) {
            unsigned8(available, value >> required);
            unsigned8(required, value);
            return;
        }
        octet <<= size;
        octet |= value & BitIoUtils.bitMaskSingle(size);
        available -= size;
        if (available == 0) {
            assert octet >= 0 && octet < 256;
            output.write(octet);
            count++;
            octet = 0x00;
            available = Byte.SIZE;
        }
    }

    private final ByteOutput output;

    /**
     * The current octet.
     */
    private int octet;

    /**
     * The number of available bits in {@link #octet}.
     */
    private int available = Byte.SIZE;

    /**
     * The number of bytes written, to the {@link #output}, so far.
     */
    private long count;
}
