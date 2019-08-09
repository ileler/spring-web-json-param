package org.ileler.swjp;

import com.jayway.jsonpath.JsonPath;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageReaderArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Author: kerwin612@qq.com
 */
public class JsonParamConfigForWebFlux implements WebFluxConfigurer {

    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new HandlerMethodArgumentResolverForFlux());
    }


    class HandlerMethodArgumentResolverForFlux extends AbstractMessageReaderArgumentResolver {

        public HandlerMethodArgumentResolverForFlux() {
            super(ServerCodecConfigurer.create().getReaders(), ReactiveAdapterRegistry.getSharedInstance());
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return JsonParamResolver.supportsParameter(parameter);
        }

        public Mono<Object> resolveArgument(MethodParameter methodParameter, BindingContext bindingContext, ServerWebExchange serverWebExchange) {
            JsonParamBean jsonParamBean = JsonParamResolver.resolveArgument(methodParameter);
//            ServerWebExchange exchange = serverWebExchange.mutate().request(new ServerHttpRequestDecorator(serverWebExchange.getRequest()) {
//
//                private DataBuffer cachedBuffer;
//
//                @Override
//                public Flux<DataBuffer> getBody() {
//                    return cachedBuffer != null ? Flux.just(cachedBuffer) : super.getBody()
//                            .publishOn(single())
//                            .doOnNext(this::cache);
//                }
//
//                private void cache(DataBuffer buffer) {
//                    cachedBuffer = buffer;
//                }
//
//            }).build();
            return readBody(methodParameter, false, bindingContext, serverWebExchange).map(json -> JsonPath.read((String) json, jsonParamBean.getParamName())).defaultIfEmpty(jsonParamBean.getParamDefaultValue());
        }

    }


}
