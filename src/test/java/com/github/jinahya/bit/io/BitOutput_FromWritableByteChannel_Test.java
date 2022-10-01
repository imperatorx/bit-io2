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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.WritableByteChannel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link BitOutput#from(WritableByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BitInput_FromReadableByteChannel_Test
 */
class BitOutput_FromWritableByteChannel_Test {

    @Test
    void __Mock() {
        final var byteChannel = Mockito.mock(WritableByteChannel.class);
        final var bitOutput = BitOutput.from(byteChannel);
        assertThat(bitOutput)
                .isNotNull();
    }
}
