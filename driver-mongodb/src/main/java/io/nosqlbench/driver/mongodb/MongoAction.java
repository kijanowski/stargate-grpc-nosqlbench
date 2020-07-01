package io.nosqlbench.driver.mongodb;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;
import io.nosqlbench.engine.api.activityapi.core.SyncAction;
import io.nosqlbench.engine.api.activityapi.planning.OpSequence;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoAction implements SyncAction {

    private final static Logger logger = LoggerFactory.getLogger(MongoAction.class);

    private final MongoActivity activity;
    private final int slot;

    private OpSequence<ReadyMongoStatement> sequencer;

    public MongoAction(MongoActivity activity, int slot) {
        this.activity = activity;
        this.slot = slot;
    }

    @Override
    public void init() {
        this.sequencer = activity.getOpSequencer();
    }

    @Override
    public int runCycle(long cycleValue) {
        ReadyMongoStatement rms;
        Bson queryBson;
        try (Timer.Context bindTime = activity.bindTimer.time()) {
            rms = sequencer.get(cycleValue);
            queryBson = rms.bind(cycleValue);

            // Maybe show the query in log/console - only for diagnostic use
            if (activity.isShowQuery()) {
                logger.info("Query(cycle={}):\n{}", cycleValue, queryBson);
            }
        }

        long nanoStartTime = System.nanoTime();
        for (int i = 1; i <= activity.getMaxTries(); i++) {
            activity.triesHisto.update(i);

            try (Timer.Context resultTime = activity.resultTimer.time()) {
                // assuming the commands are one of these in the doc:
                // https://docs.mongodb.com/manual/reference/command/nav-crud/
                Document resultDoc = activity.getDatabase().runCommand(queryBson, rms.getReadPreference());

                long resultNanos = System.nanoTime() - nanoStartTime;

                // TODO: perhaps collect the operationTime from the resultDoc if any
                // https://docs.mongodb.com/manual/reference/method/db.runCommand/#command-response
                int ok = Double.valueOf((double) resultDoc.getOrDefault("ok", 0.0d)).intValue();
                if (ok == 1) {
                    // success
                    activity.resultSuccessTimer.update(resultNanos, TimeUnit.NANOSECONDS);
                }
                activity.resultSetSizeHisto.update(resultDoc.getInteger("n", 0));

                return ok == 1 ? 0 : 1;
            } catch (Exception e) {
                logger.error("Failed to runCommand {} on cycle {}, tries {}", queryBson, cycleValue, i, e);
            }
        }

        throw new RuntimeException(String.format("Exhausted max tries (%s) on cycle %s",
                                                 activity.getMaxTries(), cycleValue));
    }
}
