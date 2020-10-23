package io.nosqlbench.nb.spi;

public interface Named {
    /**
     * <p>Return the name for this function library implementation.</p>
     *
     * @return Simple lower-case canonical library name
     */
    String getName();
}
