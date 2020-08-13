package cloudflow.akkastream.util.scaladsl

import akka.NotUsed
import akka.kafka.ConsumerMessage.Committable
import akka.stream.scaladsl.Sink
import cloudflow.akkastream.scaladsl.FlowWithCommittableContext
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletContext, AkkaStreamletLogic}
import cloudflow.streamlets.{RoundRobinPartitioner, StreamletShape}
import cloudflow.streamlets.avro.AvroOutlet

import scala.reflect.ClassTag

abstract class KafkaConsumerLogic[RawKey, RawMsg](implicit context: AkkaStreamletContext) extends AkkaStreamletLogic {
  def sink(): Sink[((RawKey, RawMsg), Committable), NotUsed]

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
    //    - send to sink()
    //      - decode key & value
    // TODO - support cancelling/shutdown via Control
    FlowWithCommittableContext[(RawKey, RawMsg)]
      .asFlow
      .to(sink())
  }
}

class TestKafkaIngress[Out <: org.apache.avro.specific.SpecificRecordBase : ClassTag] extends AkkaStreamlet {
  val out = AvroOutlet[Out]("hans").withPartitioner(RoundRobinPartitioner)
  val shape = StreamletShape(out)

  override protected def createLogic(): AkkaStreamletLogic = new KafkaConsumerLogic[String, String] {

    def stringKeyValToOut(keyVal: (String, String)): Out = ???
    override def sink(): Sink[((String, String), Committable), NotUsed] = FlowWithCommittableContext[(String, String)]
      .map(stringKeyValToOut)
      .asFlow
      .to(committableSink(out))
  }
}
