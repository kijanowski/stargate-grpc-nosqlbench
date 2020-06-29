package io.nosqlbench.engine.api.templating;

/**
 * Provide a way to configure a target object of type T, given an enumeration which describes the distinct property
 * types which could be configured. This method provides an efficient method for refining a template or builder object
 * with O(1) field lookup.
 * <p>
 * The field enum doesn't limit how a field may be modified. In some cases, a single field may be iterativel built-up,
 * such as headers for a request object. (Multiple headers can be added, and they are all a header type, just with
 * different values.) In other cases, there may be a single-valued property that is replaced entirely each time it is
 * set.
 *
 * @param <F> An enum type which describes distinct fields which may be modified.
 * @param <T> A target configurable type to be configured.
 */
public interface EnumSetter<F extends Enum<F>, T> {

    /**
     * Given a target configurable of type T and a field type identifier from enum type K, set or add a value to the
     * field described by K, and then return the target configurable.
     *
     * @param target The object to be augmented.
     * @param field  The enum which describes the type of field mutation.
     * @param value  The object values to assign to the named field. If a given field type can be a collection, like a
     *               map, then these might be a 2-tuple of key-value. Semantics of the value objects are always
     *               specific to the type of field being configured.
     * @return A modified object, possibly a new instance, as with functional property modifiers.
     */
    T setField(T target, F field, Object... value);
}
