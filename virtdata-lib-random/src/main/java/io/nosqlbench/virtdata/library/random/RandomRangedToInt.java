/*
 *
 *       Copyright 2015 Jonathan Shook
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.nosqlbench.virtdata.library.random;

import io.nosqlbench.virtdata.annotations.DeprecatedFunction;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.function.LongFunction;

@DeprecatedFunction("random mappers are not deterministic. They will be replaced with hash-based functions.")
public class RandomRangedToInt implements LongFunction<Integer> {
    private final MersenneTwister theTwister;
    private final long min;
    private final long max;
    private final long length;

    public RandomRangedToInt(long min, long max) {
        this(min,max,System.nanoTime());
    }

    public RandomRangedToInt(long min, long max, long seed) {
        this.theTwister = new MersenneTwister(seed);
        if (max<=min) {
            throw new RuntimeException("max must be >= min");
        }
        this.min = min;
        this.max = max;
        this.length = max - min;
    }

    public String toString() {
        return getClass().getSimpleName() + ":" + min + ":" + max;
    }

    @Override
    public Integer apply(long operand) {
        long value = Math.abs(theTwister.nextLong());
        value %= length;
        value += min;
        return (int) value;
    }
}
