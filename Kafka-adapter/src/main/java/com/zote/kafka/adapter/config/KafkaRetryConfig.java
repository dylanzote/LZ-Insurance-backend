//package com.zote.kafka.adapter.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
//import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//@Configuration
//@EnableKafkaRetryTopic
//@EnableScheduling
//public class KafkaRetryConfig extends RetryTopicConfigurationSupport {
//
//    @Override
//    protected void configureBlockingRetries(BlockingRetriesConfigurer blocking) {
//        // Configure blocking retries (immediate retries)
//        blocking
//            .retryOn(org.springframework.kafka.listener.ListenerExecutionFailedException.class)
//            .backOff(new org.springframework.util.backoff.ExponentialBackOff(1000, 2));
//    }
//
////    @Override
////    protected void configureNonBlockingRetries(DefaultDestinationTopicResolver nonBlocking) {
////        // Configure non-blocking retries (topic-based retries)
////        nonBlocking
////            .suffixTopicsWithIndexValues()
////            .maxAttempts(4)
////            .useSingleTopicForFixedDelays()
////            .retryTopicSuffix("-retry")
////            .dltSuffix("-dlt");
////    }
//}
