package ru.suhotrub.webrtcsignallingserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebRtcSignallingServerApplication

fun main(args: Array<String>) {
	runApplication<WebRtcSignallingServerApplication>(*args)
}
