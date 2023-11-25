package kr.museekee.rainu.wear.rainumeals.presentation.libs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class TMeal(
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("date")
    val date: LocalDate,
    @SerialName("cooks")
    val cooks: List<String>,
    @SerialName("kiloCalories")
    val kiloCalories: Double,
    @SerialName("allergies")
    val allergies: List<List<Int>>
)

@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}