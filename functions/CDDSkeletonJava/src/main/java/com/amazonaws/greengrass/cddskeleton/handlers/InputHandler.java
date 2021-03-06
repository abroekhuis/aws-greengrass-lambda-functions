package com.amazonaws.greengrass.cddskeleton.handlers;

import com.amazonaws.greengrass.cddskeleton.data.Topics;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.awslabs.aws.iot.greengrass.cdd.events.GreengrassLambdaEvent;
import com.awslabs.aws.iot.greengrass.cdd.events.ImmutablePublishMessageEvent;
import com.awslabs.aws.iot.greengrass.cdd.handlers.interfaces.GreengrassLambdaEventHandler;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;

public class InputHandler implements GreengrassLambdaEventHandler {
    @Inject
    EventBus eventBus;
    @Inject
    Topics topics;

    @Inject
    public InputHandler() {
    }

    /**
     * Only care about messages on the topics.getInputTopic() topic
     *
     * @param topic
     * @return
     */
    @Override
    public boolean isTopicExpected(String topic) {
        return topic.equals(topics.getInputTopic());
    }

    /**
     * Send a message on the topics.getOutputTopic() topic showing that we received a message
     *
     * @param greengrassLambdaEvent
     */
    @Override
    public void execute(GreengrassLambdaEvent greengrassLambdaEvent) {
        String topic = greengrassLambdaEvent.getTopic().get();
        LambdaLogger logger = greengrassLambdaEvent.getLogger();

        String message = "Inbound message on topic [" + topic + "]";

        message += getInputDescription(greengrassLambdaEvent);

        logger.log(message);

        eventBus.post(ImmutablePublishMessageEvent.builder().topic(topics.getOutputTopic()).message(message).build());
    }

    @Override
    public void executeInvoke(GreengrassLambdaEvent greengrassLambdaEvent) {
        LambdaLogger logger = greengrassLambdaEvent.getLogger();

        String message = "Function invoked directly as a Lambda";

        message += getInputDescription(greengrassLambdaEvent);

        logger.log(message);

        eventBus.post(ImmutablePublishMessageEvent.builder().topic(topics.getOutputTopic()).message(message).build());
    }

    private String getInputDescription(GreengrassLambdaEvent greengrassLambdaEvent) {
        if (greengrassLambdaEvent.getBinaryInput().isPresent()) {
            return ", with BINARY input";
        }

        if (greengrassLambdaEvent.getJsonInput().isPresent()) {
            return ", with JSON input";
        }

        return ", WITHOUT input";
    }
}
