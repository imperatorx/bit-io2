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

import java.io.DataInput;
import java.io.IOException;

/**
 * A byte input reads bytes from an instance of {@link DataInput}.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see DataByteOutput
 */
public class DataByteInput
        extends AbstractByteInput<DataInput> {

    /**
     * Creates a new instance on top of specified data input.
     *
     * @param source the data input from which bytes are read.
     */
    public DataByteInput(final DataInput source) {
        super(source);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @implSpec The {@code read()} method of {@code DataByteInput} class invokes {@link DataInput#readUnsignedByte()}
     * method on {@link #source}, and returns the result.
     */
    @Override
    public int read() throws IOException {
        return source.readUnsignedByte();
    }
}
