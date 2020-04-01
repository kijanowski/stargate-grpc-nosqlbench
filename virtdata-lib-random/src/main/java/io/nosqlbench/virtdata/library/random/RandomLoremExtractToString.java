/*
 *
 *       Copyright 2015 Jonathan Shook
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.pache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.nosqlbench.virtdata.library.random;

import io.nosqlbench.virtdata.api.annotations.DeprecatedFunction;

import java.util.function.LongFunction;

@DeprecatedFunction("random mappers are not deterministic. They will be replaced with hash-based functions.")
public class RandomLoremExtractToString implements LongFunction<String> {

    private final RandomFileExtractToString randomFileExtractMapper;

    public RandomLoremExtractToString(int minsize, int maxsize, long seed) {
        randomFileExtractMapper = new RandomFileExtractToString("lorem-ipsum.txt", minsize, maxsize, seed);
    }

    public RandomLoremExtractToString(int minsize, int maxsize) {
        this(minsize,maxsize,System.nanoTime());
    }

    @Override
    public String apply(long input) {
        return randomFileExtractMapper.apply(input);
    }
}
