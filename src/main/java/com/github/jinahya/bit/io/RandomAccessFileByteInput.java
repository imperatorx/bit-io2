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
import java.io.RandomAccessFile;
import java.util.function.Supplier;

/**
 * A byte input which reads bytes from a random access file.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see RandomAccessFileByteOutput
 */
class RandomAccessFileByteInput extends ByteInputAdapter<RandomAccessFile> {

    /**
     * Creates a new instance with specified source supplier.
     *
     * @param sourceSupplier the source supplier.
     */
    public RandomAccessFileByteInput(final Supplier<? extends RandomAccessFile> sourceSupplier) {
        super(sourceSupplier);
    }

    /**
     * {@inheritDoc} The {@code read(RandomAccessFile)} method of {@code RandomAccessFileByteInput} class invokes {@link
     * RandomAccessFile#readUnsignedByte()} method on {@code source} and returns the result.
     *
     * @param source {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    protected int read(final RandomAccessFile source) throws IOException {
        return source.readUnsignedByte();
    }
}
