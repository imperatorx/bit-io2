package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
 * %%
 * Copyright (C) 2020 - 2022 Jinahya, Inc.
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

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

import static java.util.concurrent.ThreadLocalRandom.current;

public final class BitIoTestUtils {

    static int getRandomSizeForByte(final boolean unsigned) {
        final var size = current().nextInt(1, Byte.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForByte(unsigned, size);
    }

    static <R> R applyRandomSizeForByte(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomSizeForByte(unsigned));
    }

    static byte getRandomValueForByte(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        final byte value;
        if (unsigned) {
            value = (byte) (current().nextInt() >>> (Integer.SIZE - size));
            assert value >= 0;
            assert value >> size == 0;
        } else {
            value = (byte) (current().nextInt() >> (Integer.SIZE - size));
            if (size < Byte.SIZE) {
                assert value >> size == 0 || value >> size == -1;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForByte(final boolean unsigned, final int size,
                                         final IntFunction<? extends R> function) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomValueForByte(unsigned, size));
    }

    static <R> R applyRandomValueForByteUnchecked(final boolean unsigned, final int size,
                                                  final CheckedFunction1<? super Integer, ? extends R> function) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        Objects.requireNonNull(function, "function is null");
        return applyRandomValueForByte(unsigned, size, v -> function.unchecked().apply(v));
    }

    static <R> R applyRandomSizeAndValueForByte(final boolean unsigned,
                                                final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForByte(
                unsigned,
                s -> applyRandomValueForByte(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForByteUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeAndValueForByte(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    static int getRandomSizeForShort(final boolean unsigned) {
        final int size = current().nextInt(1, Short.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForShort(unsigned, size);
    }

    static <R> R applyRandomSizeForShort(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomSizeForShort(unsigned));
    }

    static short getRandomValueForShort(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForShort(unsigned, size);
        final short value;
        if (unsigned) {
            value = (short) (current().nextInt() >>> (Integer.SIZE - size));
            assert value >> size == 0;
        } else {
            value = (short) (current().nextInt() >> (Integer.SIZE - size));
            if (size < Short.SIZE) {
                assert value >> size == 0 || value >> size == -1;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForShort(final boolean unsigned, final int size,
                                          final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomValueForShort(unsigned, size));
    }

    static <R> R applyRandomValueForShortUnchecked(final boolean unsigned, final int size,
                                                   final CheckedFunction1<? super Integer, ? extends R> function) {
        return applyRandomValueForShort(
                unsigned, size,
                v -> function.unchecked().apply(v)
        );
    }

    static <R> R applyRandomValueForShort(final boolean unsigned,
                                          final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForShort(
                unsigned,
                s -> applyRandomValueForShort(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForShortUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        return applyRandomValueForShort(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    static int getRandomSizeForInt(final boolean unsigned) {
        final var size = current().nextInt(1, Integer.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForInt(unsigned, size);
    }

    static <R> R applyRandomSizeForInt(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomSizeForInt(unsigned));
    }

    static int getRandomValueForInt(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        final int value;
        if (unsigned) {
            value = (current().nextInt() >>> (Integer.SIZE - size));
            assert value >> size == 0;
        } else {
            value = (current().nextInt() >> (Integer.SIZE - size));
            if (size < Integer.SIZE) {
                assert value >> size == 0 || value >> size == -1;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForInt(final boolean unsigned, final int size,
                                        final IntFunction<? extends R> function) {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomValueForInt(unsigned, size));
    }

    static <R> R applyRandomValueForIntUnchecked(final boolean unsigned, final int size,
                                                 final CheckedFunction1<? super Integer, ? extends R> function) {
        return applyRandomValueForInt(unsigned, size, v -> function.unchecked().apply(v));
    }

    static <R> R applyRandomSizeAndValueForInt(final boolean unsigned,
                                               final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForInt(
                unsigned,
                s -> applyRandomValueForInt(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForIntUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        return applyRandomSizeAndValueForInt(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    static int getRandomSizeForLong(final boolean unsigned) {
        final var size = current().nextInt(1, Long.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForLong(unsigned, size);
    }

    static <R> R applyRandomSizeForLong(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomSizeForLong(unsigned));
    }

    static long getRandomValueForLong(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForLong(unsigned, size);
        final long value;
        if (unsigned) {
            value = (current().nextLong() >>> (Long.SIZE - size));
            assert value >= 0L;
            assert value >> size == 0L;
        } else {
            value = (current().nextLong() >> (Long.SIZE - size));
            if (size < Long.SIZE) {
                assert value >> size == 0L || value >> size == -1L;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForLong(final boolean unsigned, final int size,
                                         final LongFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomValueForLong(unsigned, size));
    }

    static <R> R applyRandomValueForLongUnchecked(final boolean unsigned, final int size,
                                                  final CheckedFunction1<? super Long, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomValueForLong(
                unsigned,
                size,
                v -> function.unchecked().apply(v)
        );
    }

    static <R> R applyRandomSizeAndValueForLong(final boolean unsigned,
                                                final IntFunction<? extends LongFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForLong(
                unsigned,
                s -> applyRandomValueForLong(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForLongUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<Long, ? extends R>> function) {
        return applyRandomSizeAndValueForLong(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    static int getRandomSizeForChar() {
        final int size = current().nextInt(1, Character.SIZE + 1);
        return BitIoConstraints.requireValidSizeForChar(size);
    }

    static <R> R applyRandomSizeForChar(final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomSizeForChar());
    }

    static char getRandomValueForChar(final int size) {
        BitIoConstraints.requireValidSizeForChar(size);
        final char value = (char) (current().nextInt() >>> (Integer.SIZE - size));
        assert value >> size == 0;
        return value;
    }

    static <R> R applyRandomValueForChar(final int size, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomValueForChar(size));
    }

    static <R> R applyRandomValueForCharUnchecked(final int size,
                                                  final CheckedFunction1<? super Integer, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomValueForChar(
                size,
                v -> function.unchecked().apply(v)
        );
    }

    static <R> R applyRandomValueForChar(final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForChar(s -> applyRandomValueForChar(s, v -> function.apply(s).apply(v)));
    }

    static <R> R applyRandomValueForCharUnchecked(
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        return applyRandomValueForChar(s -> v -> function.unchecked().apply(s).unchecked().apply(v));
    }

    static int getRandomExponentSizeForFloat() {
        return FloatConstraints.requireValidExponentSize(
                ThreadLocalRandom.current().nextInt(
                        FloatConstants.SIZE_MIN_EXPONENT,
                        FloatConstants.SIZE_EXPONENT + 1
                )
        );
    }

    static int getRandomExponentBitsForFloat(final int size) {
        return getRandomValueForInt(false, size)
               << FloatConstants.SIZE_SIGNIFICAND
               & FloatConstants.MASK_EXPONENT;
    }

    static int getRandomSignificandSizeForFloat() {
        return FloatConstraints.requireValidSignificandSize(
                ThreadLocalRandom.current().nextInt(
                        FloatConstants.SIZE_MIN_SIGNIFICAND,
                        FloatConstants.SIZE_SIGNIFICAND + 1
                )
        );
    }

    static int getRandomSignificandBitsForFloat(int size) {
        FloatConstraints.requireValidSignificandSize(size);
        int bits = getRandomValueForInt(true, 1) << (size - 1);
        if (--size > 0) {
            bits |= getRandomValueForInt(true, size);
        }
        return bits;
    }

    static int getRandomValueBitsForFloat(final int exponentSize, final int significandSize) {
        FloatConstraints.requireValidExponentSize(exponentSize);
        FloatConstraints.requireValidSignificandSize(significandSize);
        return (getRandomValueForInt(true, 1) << FloatConstants.SHIFT_SIGN_BIT)
               | getRandomExponentBitsForFloat(exponentSize)
               | getRandomSignificandBitsForFloat(significandSize);
    }

    static float getRandomValueForFloat(final int exponentSize, final int significandSize) {
        FloatConstraints.requireValidExponentSize(exponentSize);
        FloatConstraints.requireValidSignificandSize(significandSize);
        return Float.intBitsToFloat(getRandomValueBitsForFloat(exponentSize, significandSize));
    }

    static <R> R applyRandomValueBitsForFloat(
            final IntFunction<? extends IntFunction<? extends IntFunction<? extends R>>> function) {
        final var exponentSize = getRandomExponentSizeForFloat();
        final var significantSize = getRandomSignificandSizeForFloat();
        final var valueBits = getRandomValueBitsForFloat(exponentSize, significantSize);
        return function.apply(exponentSize)
                .apply(significantSize)
                .apply(valueBits);
    }

    static <R> R w1(final Function<? super BitOutput, Function<? super byte[], ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final BitOutput output = BitOutputFactory.from(stream);
        final Function<? super byte[], ? extends R> f2 = f1.apply(output);
        final long padded = output.align(1);
        assert padded >= 0L;
        final byte[] bytes = stream.toByteArray();
        assert f2 != null : "f2 is null";
        return f2.apply(bytes);
    }

    static <R> R w1u(final CheckedFunction1<? super BitOutput, CheckedFunction1<? super byte[], ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        return w1(o -> {
            final CheckedFunction1<? super byte[], ? extends R> f2 = f1.unchecked().apply(o);
            assert f2 != null : "f2 is null";
            return b -> f2.unchecked().apply(b);
        });
    }

    static <R> R wr1(final Function<? super BitOutput, ? extends Function<? super BitInput, ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        return w1(o -> {
            final Function<? super BitInput, ? extends R> f2 = f1.apply(o);
            assert f2 != null : "f2 is null";
            return a -> {
                final BitInput input = BitInputFactory.from(new ByteArrayInputStream(a));
                final R result = f2.apply(input);
                try {
                    final long discarded = input.align(1);
                    assert discarded >= 0L;
                    return result;
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            };
        });
    }

    public static <R> R wr1u(
            final CheckedFunction1<? super BitOutput, ? extends CheckedFunction1<? super BitInput, ? extends R>> f1)
            throws IOException {
        return wr1(o -> f1.unchecked().apply(o).unchecked());
    }

    static void w2(final Function<? super BitOutput, Consumer<? super byte[]>> f1) throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        w1(o -> {
            final var consumer = f1.apply(o);
            return i -> {
                consumer.accept(i);
                return null;
            };
        });
    }

    static void wr2(final Function<? super BitOutput, ? extends Consumer<? super BitInput>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        wr1(o -> {
            final var consumer = f1.apply(o);
            return i -> {
                consumer.accept(i);
                return null;
            };
        });
    }

    static void wr2u(
            final CheckedFunction1<? super BitOutput, ? extends CheckedConsumer<? super BitInput>> function)
            throws IOException {
        Objects.requireNonNull(function, "function is null");
        wr1u(o -> {
            final var consumer = function.apply(o);
            return i -> {
                consumer.accept(i);
                return null;
            };
        });
    }

    static String format(final float value) {
        final String string = String.format("%32s", Integer.toBinaryString(Float.floatToRawIntBits(value)));
        return string.substring(0, 1) + ' ' + string.substring(1, 9) + ' ' + string.substring(9);
    }

    static String format(final double value) {
        final String string = String.format("%64s", Long.toBinaryString(Double.doubleToRawLongBits(value)));
        return string.substring(0, 1) + ' ' + string.substring(1, 11) + ' ' + string.substring(11);
    }

    private BitIoTestUtils() {
        throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
