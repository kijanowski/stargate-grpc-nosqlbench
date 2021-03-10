package io.nosqlbench.driver.pulsar;

import io.nosqlbench.driver.pulsar.util.PulsarActivityUtil;
import io.nosqlbench.driver.pulsar.util.PulsarNBClientConf;
import org.apache.commons.lang.StringUtils;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PulsarProducerSpace extends PulsarSpace {

    private final ConcurrentHashMap<String, Producer<?>> producers = new ConcurrentHashMap<>();

    public PulsarProducerSpace(String name, PulsarNBClientConf pulsarClientConf) {
        super(name, pulsarClientConf);
    }

    // Producer name is NOT mandatory
    // - It can be set at either global level or cycle level
    // - If set at both levels, cycle level setting takes precedence
    private String getEffectiveProducerName(String cycleProducerName) {
        if (!StringUtils.isBlank(cycleProducerName)) {
            return cycleProducerName;
        }

        String globalProducerName = pulsarNBClientConf.getProducerName();
        if (!StringUtils.isBlank(globalProducerName)) {
            return globalProducerName;
        }

        // Default Producer name when it is not set at either cycle or global level
        return "default-prod";
    }

    // Topic name IS mandatory
    // - It must be set at either global level or cycle level
    // - If set at both levels, cycle level setting takes precedence
    private String getEffectiveTopicName(String cycleTopicName) {
        if (!StringUtils.isBlank(cycleTopicName)) {
            return cycleTopicName;
        }

        String globalTopicName = pulsarNBClientConf.getProducerTopicName();
        if (!StringUtils.isBlank(globalTopicName)) {
            throw new RuntimeException("Topic name must be set at either global level or cycle level!");
        }

        return globalTopicName;
    }

    public Producer<?> getProducer(String cycleProducerName, String cycleTopicName) {
        String producerName = getEffectiveProducerName(cycleProducerName);
        String topicName = getEffectiveTopicName(cycleTopicName);

        String encodedStr = PulsarActivityUtil.encode(cycleProducerName, cycleTopicName);
        Producer<?> producer = producers.get(encodedStr);

        if (producer == null) {
            PulsarClient pulsarClient = getPulsarClient();

            // Get other possible producer settings that are set at global level
            Map<String, Object> producerConf = pulsarNBClientConf.getProducerConfMap();
            producerConf.put(PulsarActivityUtil.PRODUCER_CONF_STD_KEY.topicName.label, topicName);
            producerConf.put(PulsarActivityUtil.PRODUCER_CONF_STD_KEY.producerName.label, producerName);

            try {
                producer = pulsarClient.newProducer(pulsarSchema).loadConf(producerConf).create();
            } catch (PulsarClientException ple) {
                throw new RuntimeException("Unable to create a Pulsar producer!");
            }

            producers.put(encodedStr, producer);
        }

        return producer;
    }
}