package ru.netology.diploma.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.diploma.R
import ru.netology.diploma.dto.AttachmentType
import java.io.File
import java.io.InputStream
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneOffset
import java.time.LocalDateTime


fun numberRepresentation(number: Int): String {

    return when (number) {
        in 0 until 1000 -> number.toString()
        in 1000 until 1100 -> (number / 1000).toString() + "K"
        in 1100 until 10_000 -> {
            if (number % 1000 == 0) (number / 1000).toString() + "K" else
                ((number.toDouble()) / 1000).toString().take(3) + "K"
        }

        in 10_000 until 1000_000 -> (number / 1000).toString() + "K"
        in 1000_000 until 1100_000 -> (number / 1000_000).toString() + "M"
        else -> {
            if (number % 1000_000 == 0) (number / 1000_000).toString() + "M" else
                ((number.toDouble()) / 1000_000).toString().take(3) + "M"
        }
    }
}

fun ImageView.loadCircle(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_downloading_24)
        .error(R.drawable.ic_error_24)
        .timeout(10_000)
        .circleCrop()
        .into(this)
}

fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_downloading_24)
        .error(R.drawable.ic_error_24)
        .timeout(10_000)
        .into(this)
}

fun formatDateTime(date: String): String {

    return try {
        val serverDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        serverDateTime.format(formatter)
    } catch (_: Exception) {
        ""
    }

}

fun published(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    return currentDateTime.format(formatter)
}


fun publishedEvent(): String {
    val currentDateTime = LocalDateTime.now().atOffset(ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    return currentDateTime.format(formatter)
}

fun formatDateTimeEvent (inputDateTime: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
    val dateTime = LocalDateTime.parse(inputDateTime, formatter)
    val date =  dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    val originalDateTime = LocalDateTime.parse(date)
    val newDateTime = originalDateTime.withSecond(46).withNano(668000000).atOffset(ZoneOffset.UTC)
    return newDateTime.toString()
}

fun formatDateTimeJob (inputDate: String): String {
    val inputTime = "00:00:00"
    val inputDateString = "$inputDate $inputTime"
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    val localDateTime = LocalDateTime.parse(inputDateString, formatter)
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    return localDateTime.format(outputFormatter)
}

fun formatDateTimeJobBinding(dateTimeString: String): String {
    val serverDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return serverDateTime.format(formatter)
}


fun checkMediaType(input: String): AttachmentType {
    return when (File(input).extension.lowercase(Locale.ROOT)) {
        "mp3", "wav", "ogg" -> AttachmentType.AUDIO
        "mp4", "avi", "mov" -> AttachmentType.VIDEO
        "jpg", "jpeg", "png", "gif" -> AttachmentType.IMAGE
        else -> AttachmentType.Unknown
    }
}

fun getInputStreamFromUri(context: Context?, uri: Uri): InputStream? {
    return context?.contentResolver?.openInputStream(uri)
}

fun createFileFromInputStream(inputStream: InputStream): File {
    val tempFile = File.createTempFile("temp", ".tmp")
    tempFile.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
    return tempFile
}






