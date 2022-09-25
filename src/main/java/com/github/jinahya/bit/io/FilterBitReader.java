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

import java.util.Objects;

/**
 * A abstract reader class for filtering values read from other readers.
 *
 * @param <T> value type parameter
 * @param <U> filtered value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilterBitWriter
 */
public abstract class FilterBitReader<T, U>
        implements BitReader<T> {

    /**
     * Creates a new instance which wraps specified reader.
     *
     * @param reader the reader to wrap.
     */
    protected FilterBitReader(final BitReader<U> reader) {
        super();
        this.reader = Objects.requireNonNull(reader, "reader is null");
    }

    /**
     * The reader wrapped within this reader.
     */
    protected final BitReader<U> reader;
}
