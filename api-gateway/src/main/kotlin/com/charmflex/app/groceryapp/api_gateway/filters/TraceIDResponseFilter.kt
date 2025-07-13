import io.micrometer.tracing.Tracer
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class TraceIDResponseFilter(
    private val tracer: Tracer
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId = tracer.currentSpan()?.context()?.traceId() ?: "unknown"
        response.addHeader("X-Trace-Id", traceId)
        logger.info("TraceIDResponseFilter triggered. TraceID: $traceId for path: ${request.requestURI}")

        filterChain.doFilter(request, response)
    }
}
