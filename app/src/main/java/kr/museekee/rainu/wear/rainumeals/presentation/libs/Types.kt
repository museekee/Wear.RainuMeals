package kr.museekee.rainu.wear.rainumeals.presentation.libs

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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

@Serializable
data class TSchool(
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
    @SerialName("schoolCode")
    val schoolCode: Int,
)

@OptIn(ExperimentalSerializationApi::class)
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