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

/**
 * A byte output writes bytes to an instance of {@link DataOutput}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see DataByteInput
 */
public class DataByteOutput
        extends AbstractByteOutput<DataOutput> {

    /**
     * Creates a new instance with specified target supplier.
     *
     * @param target the target supplier.
     */
    public DataByteOutput(final DataOutput target) {
        super(target);
    }

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @implSpec The {@code write( int)} method of {@code DataByteOutput} class invokes
     * {@link DataOutput#writeByte(int)} method on the {@link #target} with specified value.
     * @see DataOutput#writeByte(int)
     */
    @Override
    public void write(final int value) throws IOException {
        target.writeByte(value);
    }
}
