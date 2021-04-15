package io.nosqlbench.virtdata.library.basics.shared.from_long.to_collection;

import io.nosqlbench.virtdata.api.annotations.Categories;
import io.nosqlbench.virtdata.api.annotations.Category;
import io.nosqlbench.virtdata.api.annotations.Example;
import io.nosqlbench.virtdata.api.annotations.ThreadSafeMapper;
import io.nosqlbench.virtdata.api.bindings.VirtDataConversions;

import java.util.HashSet;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

/**
 * Create a Set from a long input based on a set of provided functions.
 *
 * As a 'Sized' function, the first argument is a function which determines the size of the resulting set.
 * Additional functions provided are used to generate the elements to add to the collection. If the size
 * is larger than the number of provided functions, the last provided function is used repeatedly as needed.
 *
 * As a 'Stepped' function, the input value is incremented before being used by each element function.
 */
@Categories({Category.collections})
@ThreadSafeMapper
public class SetSizedStepped implements LongFunction<java.util.Set<Object>> {

    private final List<LongFunction> valueFuncs;
    private final LongToIntFunction sizeFunc;

    @Example({
        "SetSizedStepped(Mod(3),NumberNameToString(),NumberNameToString())",
        "Create a set, like ['three','four']"
    })
    public SetSizedStepped(Object sizeFunc, Object... funcs) {
        if (sizeFunc instanceof Number) {
            int size = ((Number)sizeFunc).intValue();
            this.sizeFunc = s -> size;
        } else {
            this.sizeFunc = VirtDataConversions.adaptFunction(sizeFunc, LongToIntFunction.class);
        }
        this.valueFuncs = VirtDataConversions.adaptFunctionList(funcs, LongFunction.class, Object.class);
    }

    public SetSizedStepped(int size, Object... funcs) {
        this.sizeFunc = s -> size;
        this.valueFuncs = VirtDataConversions.adaptFunctionList(funcs, LongFunction.class, Object.class);
    }

    @Override
    public java.util.Set<Object> apply(long value) {
        int size = sizeFunc.applyAsInt(value);
        java.util.Set<Object> list = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            int selector = Math.min(i, valueFuncs.size() - 1);
            LongFunction<?> func = valueFuncs.get(selector);
            list.add(func.apply(i+value));
        }
        return list;
    }
}
