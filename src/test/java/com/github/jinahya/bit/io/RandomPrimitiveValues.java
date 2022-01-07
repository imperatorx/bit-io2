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

import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

import static java.util.concurrent.ThreadLocalRandom.current;

final class RandomPrimitiveValues {

    static byte nextRandomByte(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        return (byte) ((current().nextInt() & 0xFF) >> (Byte.SIZE - size));
    }

    static <R> R applyRandomByte(final boolean unsigned, final int size, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextRandomByte(unsigned, size));
    }

    static short nextRandomShort(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForShort(unsigned, size);
        return (short) ((current().nextInt() & 0xFFFF) >> (Short.SIZE - size));
    }

    static <R> R applyRandomShort(final boolean unsigned, final int size, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextRandomShort(unsigned, size));
    }

    static int nextRandomInt(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        if (unsigned) {
            return (current().nextInt() >>> (Integer.SIZE - size));
        } else {
            return (current().nextInt() >> (Integer.SIZE - size));
        }
    }

    static <R> R applyRandomInt(final boolean unsigned, final int size, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextRandomInt(unsigned, size));
    }

    static long nextRandomLong(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForLong(unsigned, size);
        if (unsigned) {
            return (current().nextLong() >>> (Long.SIZE - size));
        } else {
            return (current().nextLong() >> (Long.SIZE - size));
        }
    }

    static <R> R applyRandomLong(final boolean unsigned, final int size, final LongFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextRandomLong(unsigned, size));
    }

    private RandomPrimitiveValues() {
        throw new AssertionError("instantiation is not allowed");
    }
}
