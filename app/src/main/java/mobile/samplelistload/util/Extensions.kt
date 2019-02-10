package mobile.samplelistload.util

import java.text.SimpleDateFormat
import java.util.*

val formatter  = SimpleDateFormat("MMMM dd, yyyy hh:mm", Locale.getDefault())

fun Date.format(): String {
    return formatter.format(this.time)
}