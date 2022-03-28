package com.github.rtejada.kafka.stream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ColorCounterApp {

    private final static List<String> DESIRED_COLOURS =  Arrays.asList("green", "blue", "red");

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "color-count");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // disable cache to demonstrate "steps" involved, not recommended in prod
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        StreamsBuilder builder = new StreamsBuilder();

        //Create topic for user keys to colours
        KStream<String, String> textLines = builder.stream("colours-input");


        //expected input will be "user,colour"
        KStream<String, String> usersAndColours = textLines
                //filter values that do not contain the line separator
                .filter((key, value) -> value.contains(","))
                //select user as key
                //mark stream for repartition because of the key change
                .selectKey((key, value) -> value.split(",")[0].toLowerCase())
                //get the colour from the value
                .mapValues(value -> value.split(",")[1].toLowerCase())
                //filter only desired colours
                .filter((user, colour) -> DESIRED_COLOURS.contains(colour));

        //no serde specification because the data type will not change
        usersAndColours.to("user-keys-and-colours");

        //using a Ktable to changes on the same key be treated as an update not an insert
        KTable<String, String> userAndColoursTable = builder.table("user-keys-and-colours");

        KTable<String, Long> favoritesColours = userAndColoursTable
                //group by colour
                .groupBy((user, colour) -> new KeyValue<>(colour, colour))
                //count occurrences
                .count(Named.as("CountByColours"));

        //output into a kafka topic
        //configure serdes because of data type change
        favoritesColours
                .toStream()
                .to("favorite-colour-output", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);

        //streams.cleanUp();
        streams.start();

        System.out.println(streams.toString());

        //shutdown hook to gracefully shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
