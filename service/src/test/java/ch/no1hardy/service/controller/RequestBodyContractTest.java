package ch.no1hardy.service.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tools.jackson.databind.JavaType;

/**
 * Guards against request body types Jackson cannot construct (issue #455: a Lombok @Builder DTO without
 * a no-args constructor made the endpoint answer 500). Every @RequestBody parameter is discovered from
 * Spring MVC, so new endpoints are covered automatically.
 */
class RequestBodyContractTest extends ApiTestBase {
    private static final int MIN_REQUEST_BODIES = 14;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void everyRequestBodyTypeCanBeConstructedByJackson() {
        List<String> failures = new ArrayList<>();
        int found = 0;

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handler = entry.getValue();
            for (MethodParameter param : handler.getMethodParameters()) {
                if (!param.hasParameterAnnotation(RequestBody.class)) {
                    continue;
                }
                found++;
                String endpoint = entry.getKey() + " -> " + handler.getShortLogMessage();
                ResolvableType type = ResolvableType.forMethodParameter(param);
                if (type.isAssignableFrom(List.class) || type.asCollection() != ResolvableType.NONE) {
                    type = type.asCollection().getGeneric(0);
                }
                Type target = type.getType();
                try {
                    JavaType javaType = objectMapper.constructType(target);
                    objectMapper.readValue("{}", javaType);
                } catch (Exception e) {
                    failures.add(endpoint + " [" + target.getTypeName() + "]: " + e.getMessage());
                }
            }
        }

        assertThat(found).as("request body parameters found").isGreaterThanOrEqualTo(MIN_REQUEST_BODIES);
        assertThat(failures).as("request body types Jackson cannot construct").isEmpty();
    }
}
