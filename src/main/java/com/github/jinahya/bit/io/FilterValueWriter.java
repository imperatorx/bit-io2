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

import static java.util.Objects.requireNonNull;

/**
 * A wrapper class for writing a null flag before writing values.
 *
 * @param <T> value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class FilterValueWriter<T>
        implements ValueWriter<T> {

    /**
     * Creates a new instance wrapping specified writer.
     *
     * @param writer the writer to wrap.
     */
    protected FilterValueWriter(final ValueWriter<? super T> writer) {
        super();
        this.writer = requireNonNull(writer, "wrapped is null");
    }

    /**
     * {@inheritDoc} The {@code write(BitOutput, Object)} method of {@code FilterValueWriter} class invokes {@link
     * ValueWriter#write(BitOutput, Object)} method on {@link #writer} with {@code output} and {@code value}.
     *
     * @param output {@inheritDoc}
     * @param value  {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(final BitOutput output, final T value) throws IOException {
        writer.write(output, value);
    }

    /**
     * The writer wrapped by this writer.
     */
    protected final ValueWriter<? super T> writer;
}
