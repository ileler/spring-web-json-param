package org.ileler.swjp;

import com.jayway.jsonpath.JsonPath;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.ui.Model;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageReaderArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: kerwin612@qq.com
 */
public class JsonParamConfigForWebFlux implements WebFluxConfigurer {

    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new HandlerMethodArgumentResolverForFlux());
    }


    class HandlerMethodArgumentResolverForFlux extends AbstractMessageReaderArgumentResolver {

        private static final String JSONBODYATTRIBUTE = "JSON_REQUEST_BODY";

        public HandlerMethodArgumentResolverForFlux() {
            super(ServerCodecConfigurer.create().getReaders(), ReactiveAdapterRegistry.getSharedInstance());
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return JsonParamResolver.supportsParameter(parameter);
        }

        public Mono<Object> resolveArgument(MethodParameter methodParameter, BindingContext bindingContext, ServerWebExchange serverWebExchange) {
            JsonParamBean jsonParamBean = JsonParamResolver.resolveArgument(methodParameter);
            Model model = bindingContext.getModel();
            return Mono.defer(() -> {
                if (model.asMap().containsKey(JSONBODYATTRIBUTE)) {
                    return Mono.create(
                            (callback -> ((Map<String, MonoSink<Object>>) model.asMap().get(JSONBODYATTRIBUTE)).put(jsonParamBean.getParamName(), callback))
                    ).defaultIfEmpty(jsonParamBean.getParamDefaultValue());
                }
                model.addAttribute(JSONBODYATTRIBUTE, new HashMap<String, MonoSink<Object>>());
                return readBody(methodParameter, false, bindingContext, serverWebExchange).map(json -> {
                    ((Map<String, MonoSink<Object>>) model.asMap().get(JSONBODYATTRIBUTE)).forEach(
                            (String paramName, MonoSink<Object> callback) -> callback.success(JsonPath.read((String) json, paramName)));
                    return JsonPath.read((String) json, jsonParamBean.getParamName());
                }).defaultIfEmpty(jsonParamBean.getParamDefaultValue());
            });
        }

    }


}
