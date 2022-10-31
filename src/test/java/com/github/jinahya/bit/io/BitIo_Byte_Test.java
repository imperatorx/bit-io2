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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.DefaultArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class BitIo_Byte_Test {

    private static Stream<Arguments> getUnsignedAndSizeArgumentsStream() {
        return Stream.of(Boolean.TRUE, Boolean.FALSE)
                .flatMap(u -> IntStream.range(0, 16)
                        .map(i -> BitIoRandom.nextSizeForByte(u))
                        .mapToObj(s -> Arguments.of(u, s))
                );
    }

    private static Stream<Arguments> getUnsignedSizeAndValueArgumentsStream() {
        return getUnsignedAndSizeArgumentsStream()
                .map(a -> {
                    final var accessor = new DefaultArgumentsAccessor(a.get());
                    final var unsigned = accessor.get(0, Boolean.class);
                    final var size = accessor.get(1, Integer.class);
                    return Arguments.of(unsigned, size, BitIoRandom.nextValueForByte(unsigned, size));
                });
    }

    @MethodSource({"getUnsignedSizeAndValueArgumentsStream"})
    @ParameterizedTest
    void wr__(final boolean unsigned, final int size, final byte expected) throws IOException {
        try (MockedStatic<BitIoConstraints> constraints
                     = Mockito.mockStatic(BitIoConstraints.class, Mockito.CALLS_REAL_METHODS)) {
            final var actual = BitIoTestUtils.wr1au(o -> {
                o.writeByte(unsigned, size, expected);
                return (a, i) -> {
                    assertThat(a).hasSizeLessThanOrEqualTo(Byte.SIZE);
                    return i.readByte(unsigned, size);
                };
            });
            assertThat(actual).isEqualTo(expected);
            constraints.verify(() -> BitIoConstraints.requireValidSizeForByte(unsigned, size), times(2));
        }
    }
}
