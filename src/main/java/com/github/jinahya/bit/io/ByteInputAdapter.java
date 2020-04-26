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
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class implements {@link ByteInput} adapting various byte sources.
 *
 * @param <T> byte source parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ByteOutputAdapter
 */
public abstract class ByteInputAdapter<T> implements ByteInput {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified source supplier.
     *
     * @param sourceSupplier the source supplier.
     */
    public ByteInputAdapter(final Supplier<? extends T> sourceSupplier) {
        super();
        this.sourceSupplier = requireNonNull(sourceSupplier, "sourceSupplier is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return read(source());
    }

    /**
     * Reads an unsigned {@value Byte#SIZE}-bit integer from specified source.
     *
     * @param source the source from which a byte is read.
     * @return an unsigned {@value Byte#SIZE}-bit integer from specified source.
     * @throws IOException if an I/O error occurs.
     */
    protected abstract int read(T source) throws IOException;

    // -----------------------------------------------------------------------------------------------------------------
    private T source() {
        if (source == null) {
            source = sourceSupplier.get();
        }
        return source;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Supplier<? extends T> sourceSupplier;

    private transient T source;
}
