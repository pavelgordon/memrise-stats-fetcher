package dev.pgordon.kotless

import io.kotless.dsl.spring.Kotless
import kotlinx.serialization.json.JsonObjectBuilder
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass

@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}

// In-memory storage per date.
val storage = mutableMapOf<LocalDate, ApiResponse>()


@RestController
open class Pages {
    //Inject value from application.properties
    //Unfortunately it doesn't work with Kotless deployed on AWS :(
    @Value("\${memrise.cookie}")
    val cookie: String = ""

    @GetMapping("/")
    fun main(): ApiResponse {
        val now = LocalDate.now()
        if (storage.containsKey(now)) {
            return storage[now]!!
        }

        val url = "https://app.memrise.com/course/2022111/italian-1-32/"
        val headers = HttpHeaders()

        //insert your cookie here for deploy
        headers.set("cookie", cookie);

        //other cookies for the request
        headers.set("autority", "app.memrise.com");
        headers.set("cache-control", "max-age=0");
        headers.set("sec-ch-ua-mobile", "?0");
        headers.set("dnt", "1");
        headers.set("upgrade-insecure-requests", "1");
        headers.set("sec-fetch-site", "same-origin");
        headers.set("referer", "https://app.memrise.com/course/2022111/italian-1-32/leaderboard/");
        headers.set("accept-language", "en-US,en;q=0.9,de-DE;q=0.8,de;q=0.7,ru;q=0.6")
        headers.set(
            "accept",
            """text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"""
        );
        headers.set(
            "user-agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"
        );
        headers.set("sec-ch-ua", """"Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87"""");
        val entity: HttpEntity<String> = HttpEntity(headers)
        val response = RestTemplate().exchange<String>(url, HttpMethod.GET, entity)

        val body = response.body!!
        //memrise returns html in response, so we parse it and extract necessary data.
        //no API </3
        val (learned, total, longMemory) = """(\d{1,5}) / (\d{1,5}) words learned \((\d{1,5}) in long term memory""".toRegex()
            .find(body)!!.destructured

        return ApiResponse(learned, total, longMemory, LocalDateTime.now()).also {
            storage.putIfAbsent(now, it)
        }
    }

    @GetMapping("/storage")
    fun storage() = storage
}


data class ApiResponse(
    val learnedWords: String,
    val totalWords: String,
    val longMemoryWords: String,
    val createdAt: LocalDateTime,
    val error: Throwable? = null
)