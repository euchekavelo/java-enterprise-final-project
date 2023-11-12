package ru.skillbox.gateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RouterValidator {

    public static final List<Pattern> openEndpoints = List.of(
            Pattern.compile("/auth-service/user/signup"),
            Pattern.compile("/auth-service/token/generate"),
            Pattern.compile("/auth-service/v3/api-docs.*"),
            Pattern.compile("/order-service/v3/api-docs.*"),
            Pattern.compile("/payment-service/v3/api-docs.*"),
            Pattern.compile("/inventory-service/v3/api-docs.*"),
            Pattern.compile("/delivery-service/v3/api-docs.*")
    );

    public static final Predicate<ServerHttpRequest> isSecured =
            request -> openEndpoints
                    .stream()
                    .noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());

}
