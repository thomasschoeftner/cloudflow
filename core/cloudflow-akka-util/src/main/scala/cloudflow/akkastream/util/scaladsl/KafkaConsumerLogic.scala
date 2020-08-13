package cloudflow.akkastream.util.scaladsl

import cloudflow.akkastream.{AkkaStreamletContext, AkkaStreamletLogic}

abstract class KafkaConsumerLogic(implicit context: AkkaStreamletContext) extends AkkaStreamletLogic {
  def run(): Unit = {
    // TODO - configure ConsumerSettings
    //    - Kafka Bootstrap servers
    //    - de-serializers for key and value (String, byte[], etc.)
    //    - consumer-group ID
    //    - auto-offset-reset-config (where to start consuming if no previous offset is available or offset is gone) - either "earliest", or "latest"
    // TODO - pass Kafka topic name
    // TODO - streaming
    //    - subscribe to topic
    //    - transform to FlowWithContext[(Key, Value), Committable]
    //    - processing flow
    //      - decode key & value
    // TODO - support cancelling/shutdown via Control
    // 2. subscribe to topic

  }
}
