import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toTimestamp(): Long {
    return this.atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}