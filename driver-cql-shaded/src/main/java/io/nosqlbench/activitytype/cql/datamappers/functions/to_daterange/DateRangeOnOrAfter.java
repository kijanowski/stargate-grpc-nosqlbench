package io.nosqlbench.activitytype.cql.datamappers.functions.to_daterange;

import com.datastax.driver.dse.search.DateRange;
import io.nosqlbench.virtdata.api.annotations.Categories;
import io.nosqlbench.virtdata.api.annotations.Category;
import io.nosqlbench.virtdata.api.annotations.Example;
import io.nosqlbench.virtdata.api.annotations.ThreadSafeMapper;

import java.util.Date;
import java.util.function.Function;
import java.util.function.LongFunction;

/**
 * Takes an input as a reference point in epoch time, and converts it to a DateRange,
 * with the lower bounds set to the lower bound of the precision and millisecond
 * provided, and with no upper bound.
 * You can use any of these precisions to control the bounds
 * around the provided timestamp: millisecond, second, minute, hour, day, month, or year.
 */
@ThreadSafeMapper
@Categories(Category.datetime)
public class DateRangeOnOrAfter implements LongFunction<DateRange> {

    private final DateRange.DateRangeBound.Precision precision;

    @Example({"DateRangeOnOrAfter('millisecond')}","Convert the incoming millisecond to an match any time on or after"})
    @Example({"DateRangeOnOrAfter('minute')}","Convert the incoming millisecond to mach any time on or after the" +
        " minute in which the " +
        "millisecond falls"})
    public DateRangeOnOrAfter(String precision) {
        this.precision = DateRange.DateRangeBound.Precision.valueOf(precision.toUpperCase());
    }

    @Override
    public DateRange apply(long value) {
        Date date = new Date(value);
        DateRange.DateRangeBound lower = DateRange.DateRangeBound.lowerBound(date, precision);
        DateRange.DateRangeBound upper = DateRange.DateRangeBound.UNBOUNDED;
        DateRange dateRange = new DateRange(lower, upper);
        return dateRange;
    }
}
