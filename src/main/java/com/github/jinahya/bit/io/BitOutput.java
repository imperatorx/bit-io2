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
import java.util.Objects;

/**
 * An interface for writing values of an arbitrary number of bits.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see BitInput
 */
public interface BitOutput {

    /**
     * Writes specified {@code boolean} value.
     *
     * @param value the value to write.
     * @throws IOException if an I/O error occurs.
     * @implSpec The default implementation writes a {@code 1}-bit unsigned {@code int} value({@code 0b1} for
     * {@code true}, {@code 0b0} for {@code false}) for the {@code value}.
     */
    default void writeBoolean(final boolean value) throws IOException {
        writeInt(true, 1, value ? 0b01 : 0b00);
    }

    /**
     * Writes specified {@code byte} value of specified number of bits.
     *
     * @param unsigned a flag for indicating unsigned value; {@code true} for unsigned, {@code false} for signed.
     * @param size     the number of bits to write; between {@code 1} and ({@value java.lang.Byte#SIZE} -
     *                 ({@code unsigned ? 1: 0})), both inclusive.
     * @param value    the value to write.
     * @throws IOException if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #writeInt(boolean, int, int)} with given arguments.
     */
    default void writeByte(final boolean unsigned, final int size, final byte value) throws IOException {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        writeInt(unsigned, size, value);
    }

    /**
     * Writes specified {@code short} value of specified number of bits.
     *
     * @param unsigned a flag for indicating unsigned value; {@code true} for unsigned, {@code false} for signed.
     * @param size     the number of bits to write; between {@code 1} and ({@value java.lang.Short#SIZE} -
     *                 ({@code unsigned ? 1 : 0})), both inclusive.
     * @param value    the value to write.
     * @throws IOException if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #writeInt(boolean, int, int)} method with given arguments.
     */
    default void writeShort(final boolean unsigned, final int size, final short value) throws IOException {
        BitIoConstraints.requireValidSizeForShort(unsigned, size);
        writeInt(unsigned, size, value);
    }

    /**
     * Writes specified {@code int} value of specified number of bits.
     *
     * @param unsigned a flag for indicating an unsigned value; {@code true} for unsigned, {@code false} for signed.
     * @param size     the number of bits to write; between {@code 1} and ({@value java.lang.Integer#SIZE} -
     *                 ({@code unsigned ? 1 : 0})), both inclusive.
     * @param value    the value to write.
     * @throws IOException if an I/O error occurs.
     */
    void writeInt(boolean unsigned, int size, int value) throws IOException;

    /**
     * Writes specified {@code long} value of specified number of bits.
     *
     * @param unsigned a flag for indicating unsigned value; {@code true} for unsigned, {@code false} for signed.
     * @param size     the number of bits to write; between {@code 1} and ({@value java.lang.Long#SIZE} -
     *                 ({@code unsigned ? 1 : 0})), both inclusive.
     * @param value    the value to write.
     * @throws IOException if an I/O error occurs.
     */
    default void writeLong(final boolean unsigned, int size, final long value) throws IOException {
        BitIoConstraints.requireValidSizeForLong(unsigned, size);
        if (!unsigned) {
            writeInt(true, 1, value < 0L ? 0x01 : 0x00); // the most significant (sign) bit
            if (--size > 0) {
                writeLong(true, size, value);
            }
            return;
        }
        assert unsigned; // NOSONAR
        if (size >= Integer.SIZE) {
            writeInt(false, Integer.SIZE, (int) (value >> (size - Integer.SIZE)));
            size -= Integer.SIZE;
        }
        if (size > 0) {
            writeInt(true, size, (int) value);
        }
    }

    /**
     * Writes specified {@code char} value of specified number of bits.
     *
     * @param size  the number of bits to write; between {@code 1} and {@value java.lang.Character#SIZE}, both
     *              inclusive.
     * @param value the value to write.
     * @throws IOException if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #writeInt(boolean, int, int)} method with given arguments.
     */
    default void writeChar(final int size, final char value) throws IOException {
        BitIoConstraints.requireValidSizeForChar(size);
        writeInt(true, size, value);
    }

    /**
     * Writes specified {@code float} value with specified {@code exponent} size and {@code significand} size.
     *
     * @param exponentSize    the number of bit for wring the {@code exponent} part of the {@code value}; between
     *                        {@code 1} and {@value FloatConstants#SIZE_EXPONENT}, both inclusive.
     * @param significandSize the number of bit for writing the {@code significand} part of the {@code value}; between
     *                        {@code 1} and {@value FloatConstants#SIZE_SIGNIFICAND}, both inclusive.
     * @param value           the value to write.
     * @throws IOException if an I/O error occurs.
     */
    default void writeFloat(final int exponentSize, final int significandSize, final float value) throws IOException {
        FloatConstraints.requireValidExponentSize(exponentSize);
        FloatConstraints.requireValidSignificandSize(significandSize);
        if (exponentSize == FloatConstants.SIZE_EXPONENT
            && significandSize == FloatConstants.SIZE_SIGNIFICAND) {
            writeInt(false, Integer.SIZE, Float.floatToRawIntBits(value));
            return;
        }
        final int bits = Float.floatToRawIntBits(value);
        writeInt(true, 1, bits >> FloatConstants.SHIFT_SIGN_BIT);
        FloatWriter.writeExponent(this, exponentSize, bits);
        FloatWriter.writeSignificand(this, significandSize, bits);
    }

    /**
     * Writes a zoro value whose sign bit is same as the left most bit of specified sign mask. Only the left most bit of
     * the sing mask is written.
     * <p>
     * e.g.
     * <blockquote><pre>{@code
     * writeFloatOfZero(+0); // intends to writing 0b0__0000000__00000000_00000000_0000_000
     * writeFloatOfZero(-1); // intends to writing 0b1__0000000__00000000_00000000_0000_000
     * }</pre></blockquote>
     *
     * @param signMask the sign mask whose left most bit is written.
     * @throws IOException if an I/O error occurs.
     * @see BitInput#readFloatOfZero()
     */
    default void writeFloatOfZero(final int signMask) throws IOException {
        FloatWriter.writeZeroBits(this, signMask);
    }

    /**
     * Writes an infinity value whose sign bit is same as the left most bit of specified sign mask. Only the left most
     * bit of the sign mask is written.
     * <p>
     * e.g.
     * <blockquote><pre>{@code
     * writeFloatOfInfinity(+0); // intends to writing 0b0__11111111__00000000_00000000_0000_000
     * writeFloatOfInfinity(-1); // intends to writing 0b1__11111111__00000000_00000000_0000_000
     * }</pre></blockquote>
     *
     * @param signMask the sign mask value whose left most bit is used for the sign bit.
     * @throws IOException if an I/O error occurs.
     * @see BitInput#readFloatOfInfinity()
     */
    default void writeFloatOfInfinity(final int signMask) throws IOException {
        FloatWriter.writeInfinityBits(this, signMask);
    }

    /**
     * Writes specified {@code float} value with specified {@code exponent} size and {@code significand} size.
     *
     * @param exponentSize    the number of bit for wring the {@code exponent} part of the {@code value}; between
     *                        {@code 1} and {@value DoubleConstants#SIZE_EXPONENT}, both inclusive.
     * @param significandSize the number of bit for writing the {@code significand} part of the {@code value}; between
     *                        {@code 1} and {@value DoubleConstants#SIZE_SIGNIFICAND}, both inclusive.
     * @param value           the value to write.
     * @throws IOException if an I/O error occurs.
     */
    default void writeDouble(final int exponentSize, final int significandSize, final double value) throws IOException {
        DoubleConstraints.requireValidExponentSize(exponentSize);
        DoubleConstraints.requireValidSignificandSize(significandSize);
        if (exponentSize == DoubleConstants.SIZE_EXPONENT
            && significandSize == DoubleConstants.SIZE_SIGNIFICAND) {
            writeLong(false, Long.SIZE, Double.doubleToRawLongBits(value));
            return;
        }
        final long bits = Double.doubleToRawLongBits(value);
        writeLong(true, 1, bits >> DoubleConstants.SHIFT_SIGN_BIT);
        DoubleWriter.writeExponent(this, exponentSize, bits);
        DoubleWriter.writeSignificand(this, significandSize, bits);
    }

    /**
     * Writes a zoro value whose sign bit is same as the left most bit of specified value.
     *
     * @param signMask the sign mask value whose left most bit is used for the sign bit.
     * @throws IOException if an I/O error occurs.
     * @see BitInput#readDoubleOfZero()
     */
    default void writeDoubleOfZero(final long signMask) throws IOException {
        DoubleWriter.writeZeroBits(this, signMask);
    }

    /**
     * Writes an infinity value whose sign bit is same as the left most bit of specified value.
     *
     * @param signMask the sign mask value whose left most bit is used for the sign bit.
     * @throws IOException if an I/O error occurs.
     * @see BitInput#readDoubleOfInfinity()
     */
    default void writeDoubleOfInfinity(final long signMask) throws IOException {
        DoubleWriter.writeInfinityBits(this, signMask);
    }

    /**
     * Writes specified value using specified writer.
     *
     * @param writer the writer.
     * @param value  the value to write.
     * @param <T>    value type parameter
     * @throws IOException if an I/O error occurs.
     * @implSpec The default implementation invokes {@link BitWriter#write(BitOutput, Object)} method on {@code writer}
     * with {@code this} and {@code value}.
     */
    default <T> void writeObject(final BitWriter<? super T> writer, final T value) throws IOException {
        Objects.requireNonNull(writer, "writer is null");
        writer.write(this, value);
    }

    /**
     * Skips by writing specified number of zero-bits.
     *
     * @param bits the number of bit to skip; must be positive.
     * @throws IllegalArgumentException if {@code bits} is not positive.
     * @throws IOException              if an I/O error occurs.
     */
    default void skip(int bits) throws IOException {
        if (bits <= 0) {
            throw new IllegalArgumentException("bits(" + bits + ") is not positive");
        }
        for (; bits >= Integer.SIZE; bits -= Integer.SIZE) {
            writeInt(false, Integer.SIZE, 0);
        }
        if (bits > 0) {
            writeInt(true, bits, 0);
        }
    }

    /**
     * Aligns to specified number of bytes by padding required number of zero-bits.
     *
     * @param bytes the number of bytes to align; must be positive.
     * @return the number of zero-bits padded while aligning; non-negative, always.
     * @throws IllegalArgumentException if {@code bytes} is not positive.
     * @throws IOException              if an I/O error occurs.
     */
    long align(int bytes) throws IOException;
}
