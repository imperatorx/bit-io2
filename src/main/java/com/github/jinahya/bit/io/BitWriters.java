package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io
 * %%
 * Copyright (C) 2014 - 2019 Jinahya, Inc.
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
import java.util.Map;
import java.util.WeakHashMap;

import static java.util.Collections.synchronizedMap;

/**
 * A utility class for {@link BitWriter} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BitReaders
 */
final class BitWriters {

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, BitWriter<?>> BIT_WRITERS;

    static {
        BIT_WRITERS = synchronizedMap(new WeakHashMap<Class<?>, BitWriter<?>>());
    }

    /**
     * Returns a cached instance of a bit writer for specified type of bit writable.
     *
     * @param clazz the type for the bit writer instance.
     * @param <T>   bit writable type parameter
     * @return the cached instance of bit writer.
     */
    public static <T extends BitWritable> BitWriter<T> cachedBitWriterFor(final Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        synchronized (BIT_WRITERS) {
            @SuppressWarnings({"unchecked"})
            BitWriter<T> value = (BitWriter<T>) BIT_WRITERS.get(clazz);
            if (value == null) {
                value = newBitWriterFor(clazz);
                BIT_WRITERS.put(clazz, value);
            }
            return value;
        }
    }

    /**
     * Returns a new bit writer instance for specified type of bit writable.
     *
     * @param clazz the type of bit writable to read.
     * @param <T>   bit writable type parameter
     * @return a new bit writer instance.
     */
    public static <T extends BitWritable> BitWriter<T> newBitWriterFor(final Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        return new BitWriter<T>() {
            @Override
            public void write(final BitOutput output, final T value) throws IOException {
                if (output == null) {
                    throw new NullPointerException("output is null");
                }
                if (value == null) {
                    throw new NullPointerException("value is null");
                }
                value.write(output);
            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private BitWriters() {
        super();
    }
}
