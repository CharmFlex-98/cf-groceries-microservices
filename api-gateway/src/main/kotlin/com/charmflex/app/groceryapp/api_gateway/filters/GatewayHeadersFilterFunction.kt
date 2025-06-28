package com.charmflex.app.groceryapp.api_gateway.filters

import com.charmflex.app.groceryapp.api_gateway.constants.HeaderConstant
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.util.function.Consumer

class GatewayHeadersFilterFunction : HandlerFilterFunction<ServerResponse, ServerResponse> {
    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): ServerResponse {
        val userId = request.servletRequest().getHeader(HeaderConstant.HEADER_USERID)
        val gatewaySecret = request.servletRequest().getHeader(HeaderConstant.HEADER_GATEWAY_SECRET)
        val headers = Consumer<HttpHeaders> {
            it.add(HeaderConstant.HEADER_USERID, userId)
            it.add(HeaderConstant.HEADER_GATEWAY_SECRET, gatewaySecret)
        }

        val newRequest = ServerRequest.from(request)
            .headers(headers)
            .build()
        return next.handle(newRequest)
    }
}