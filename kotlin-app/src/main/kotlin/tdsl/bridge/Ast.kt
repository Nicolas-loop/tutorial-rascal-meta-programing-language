package tdsl.bridge

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class Planning(val people: List<PersonTasks> = emptyList())

@Serializable
data class PersonTasks(val name: String, val tasks: List<Task> = emptyList())

@Serializable
data class Task(
    val name: String,
    val priority: Int,
    val action: Action,
    val duration: List<Duration> = emptyList()
)

@Serializable
data class Duration(val amount: Int, val unit: String)

@Serializable(with = ActionSerializer::class)
sealed interface Action

@Serializable
data class Lunch(val action: LunchAction) : Action

@Serializable
data class Meeting(val action: MeetingAction) : Action

@Serializable
data class Paper(val action: PaperAction) : Action

@Serializable
data class Payment(val action: PaymentAction) : Action

@Serializable
data class LunchAction(val location: String)

@Serializable
data class MeetingAction(val topic: String)

@Serializable
data class PaperAction(val report: String)

@Serializable
data class PaymentAction(val amount: Int)

object ActionSerializer : JsonContentPolymorphicSerializer<Action>(Action::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Action> =
        when (val node = element.jsonObject["node"]?.jsonPrimitive?.content) {
            "lunch" -> Lunch.serializer()
            "meeting" -> Meeting.serializer()
            "paper" -> Paper.serializer()
            "payment" -> Payment.serializer()
            else -> throw SerializationException("Action desconocida: node=$node")
        }
}
