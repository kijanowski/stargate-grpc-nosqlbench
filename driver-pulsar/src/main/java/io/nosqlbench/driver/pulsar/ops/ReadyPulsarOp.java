package io.nosqlbench.driver.pulsar.ops;

import io.nosqlbench.driver.pulsar.PulsarSpace;
import io.nosqlbench.driver.pulsar.PulsarSpaceCache;
import io.nosqlbench.engine.api.activityconfig.yaml.OpTemplate;
import io.nosqlbench.engine.api.scoping.ScopedSupplier;
import io.nosqlbench.engine.api.templating.CommandTemplate;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;

import java.util.function.LongFunction;
import java.util.function.Supplier;

public class ReadyPulsarOp {

    private final CommandTemplate cmdTpl;
    private final LongFunction<PulsarOp> opFunc;
    private final PulsarSpaceCache pcache;

    public ReadyPulsarOp(OpTemplate opTemplate, PulsarSpaceCache pcache) {
        this.cmdTpl = new CommandTemplate(opTemplate);
        this.pcache = pcache;
        if (cmdTpl.isDynamic("op_scope")) {
            throw new RuntimeException("op_scope must be static");
        }
        this.opFunc = resolve();

        ScopedSupplier scope = ScopedSupplier.valueOf(cmdTpl.getStaticOr("op_scope", "singleton"));
        Supplier<LongFunction<PulsarOp>> opSupplier = scope.supplier(this::resolve);

    }

    private LongFunction<PulsarOp> resolve() {

        if (cmdTpl.containsKey("topic_url")) {
            throw new RuntimeException("topic_url is not valid. Perhaps you mean topic_uri ?");
        }

        if (cmdTpl.containsKey("send") && cmdTpl.containsKey("recv")) {
            throw new RuntimeException("You must specify either send or recv, but not both.");
        }
        if (!cmdTpl.containsKey("send") && !cmdTpl.containsKey("recv")) {
            throw new RuntimeException("You must specify either send or recv, but none was provided.");
        }

        LongFunction<String> topic_uri_func;
        if (cmdTpl.containsKey("topic_uri")) {
            if (cmdTpl.containsAny("tenant", "namespace", "topic", "persistent")) {
                throw new RuntimeException("You may not specify topic_uri with any of the piece-wise components 'persistence','tenant','namespace','topic'.");
            } else if (cmdTpl.isStatic("topic_uri")) {
                String topic_uri = cmdTpl.getStatic("topic_uri");
                topic_uri_func = (l) -> topic_uri;
            } else {
                topic_uri_func = (l) -> cmdTpl.getDynamic("topic_uri", l);
            }
        } else if (
            !cmdTpl.isDynamic("persistence") // optimize topic around static piece-wise values
                && !cmdTpl.isDynamic("tenant")
                && !cmdTpl.isDynamic("namespace")
                && !cmdTpl.isDynamic("topic")
        ) {
            String persistence = cmdTpl.getStaticOr("persistence", "persistent")
                .replaceAll("true", "persistent");
            String tenant = cmdTpl.getStaticOr("tenant", "public");
            String namespace = cmdTpl.getStaticOr("namespace", "default");
            String topic = cmdTpl.getStaticOr("topic", "default");
            String composited = persistence + "://" + tenant + "/" + namespace + "/" + topic;
            topic_uri_func = (l) -> composited;
        } else { // some or all dynamic fields, composite into a single dynamic call
            topic_uri_func = (l) ->
                cmdTpl.getDynamicOr("persistent", l, "persistent").replaceAll("true", "persistent")
                    + "://" + cmdTpl.getDynamicOr("tenant", l, "public")
                    + "/" + cmdTpl.getDynamicOr("namespace", l, "default")
                    + "/" + cmdTpl.getDynamicOr("topic", l, "default");
        }


        LongFunction<PulsarSpace> spaceFunc;
        if (cmdTpl.containsKey("client")) {
            if (cmdTpl.isStatic("client")) {
                String client_name = cmdTpl.getStatic("client");
                PulsarSpace clientSpace = pcache.getClientSpace(client_name);
                spaceFunc = l -> clientSpace;
            } else {
                spaceFunc = l -> pcache.getClientSpace(cmdTpl.getDynamic("client", l));
            }
        } else {
            spaceFunc = l -> pcache.getClientSpace("default");
        }

        if (cmdTpl.containsKey("send")) {
            return resolveSend(spaceFunc, cmdTpl, topic_uri_func);
        } else if (cmdTpl.containsKey("recv")) {
            return resolveRecv(spaceFunc, cmdTpl, topic_uri_func);
        } else {
            throw new RuntimeException("Neither send nor recv were found in the op template.");
        }

    }

    private LongFunction<PulsarOp> resolveRecv(
        LongFunction<PulsarSpace> spaceFunc,
        CommandTemplate cmdTpl,
        LongFunction<String> topic_uri_func) {
        LongFunction<Consumer<?>> consumerFunc;

        if (cmdTpl.isStatic("consumer")) {
            String consumerName = cmdTpl.getStatic("consumer");
            consumerFunc = (l) -> spaceFunc.apply(l).getConsumer(consumerName, topic_uri_func.apply(l));
        } else if (cmdTpl.isDynamic("consumer")) {
            consumerFunc = (l) -> spaceFunc.apply(l)
                .getConsumer(cmdTpl.getDynamic("consumer", l), topic_uri_func.apply(l));
        } else {
            consumerFunc = (l) -> spaceFunc.apply(l)
                .getConsumer(topic_uri_func.apply(l), topic_uri_func.apply(l));
        }

        return new PulsarRecvMapper(consumerFunc, (l) -> cmdTpl.get("recv", l), cmdTpl);

    }

    private LongFunction<PulsarOp> resolveSend(
        LongFunction<PulsarSpace> spaceFunc,
        CommandTemplate cmdTpl,
        LongFunction<String> topic_uri_func
    ) {
        LongFunction<Producer<?>> producerFunc;

        if (cmdTpl.isStatic("producer")) {
            String producerName = cmdTpl.getStatic("producer");
            producerFunc = (l) -> spaceFunc.apply(l).getProducer(producerName, topic_uri_func.apply(l));
        } else if (cmdTpl.isDynamic("producer")) {
            producerFunc = (l) -> spaceFunc.apply(l)
                .getProducer(cmdTpl.getDynamic("producer", l), topic_uri_func.apply(l));
        } else {
            producerFunc = (l) -> spaceFunc.apply(l)
                .getProducer(topic_uri_func.apply(l), topic_uri_func.apply(l));
        }

        return new PulsarSendMapper(producerFunc, (l) -> cmdTpl.get("send", l), cmdTpl);
    }

    // Create a pulsarOp which can be executed.
    // The
    public PulsarOp bind(long value) {
        PulsarOp op = opFunc.apply(value);
        return op;
    }


}
