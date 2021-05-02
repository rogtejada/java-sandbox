package com.rtejada.simple.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class App {
  public static void main(String[] args) {
    runProducer();
    runConsumer();
  }

  static void runConsumer() {
    Consumer<UUID, String> consumer = ConsumerCreator.createConsumer();

    int noMessageToFetch = 0;

    while (true) {
      final ConsumerRecords<UUID, String> consumerRecords = consumer.poll(1000);
      if (consumerRecords.count() == 0) {
        noMessageToFetch++;
        if (noMessageToFetch > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
          break;
        else
          continue;
      }

      consumerRecords.forEach(record -> {
        System.out.println("Record Key " + record.key());
        System.out.println("Record value " + record.value());
        System.out.println("Record partition " + record.partition());
        System.out.println("Record offset " + record.offset());
      });
      consumer.commitAsync();
    }
    consumer.close();
  }

  static void runProducer() {
    Producer<UUID, String> producer = ProducerCreator.createProducer();

    for (int index = 0; index < IKafkaConstants.MESSAGE_COUNT; index++) {
      final ProducerRecord<UUID, String> record =
          new ProducerRecord<UUID, String>(IKafkaConstants.TOPIC_NAME, UUID.randomUUID(),
                                           "This is record " + index);
      try {
        //Synchronous publish
        RecordMetadata metadata = producer.send(record).get();
        System.out.println("Record sent with key " + index + " to partition " + metadata.partition()
                               + " with offset " + metadata.offset());
      } catch (ExecutionException | InterruptedException e) {
        System.out.println("Error in sending record" + e);
      }
    }
  }
}