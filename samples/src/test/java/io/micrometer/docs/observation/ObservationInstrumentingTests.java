/*
 * Copyright 2017 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.docs.observation;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.micrometer.common.KeyValue;
import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.docs.context.ObservationThreadLocalAccessor;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.transport.ReceiverContext;
import io.micrometer.observation.transport.RequestReplyReceiverContext;
import io.micrometer.observation.transport.RequestReplySenderContext;
import io.micrometer.observation.transport.SenderContext;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.observability.DefaultSignalListener;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * Sources for tracing-instrumenting.adoc
 */
@WireMockTest
class ObservationInstrumentingTests {

    private static final Logger log = LoggerFactory.getLogger(ObservationInstrumentingTests.class);

    // tag::executor[]
    // Example of an Executor Service
    ExecutorService executor = Executors.newCachedThreadPool();

    // end::executor[]

    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    ObservationRegistry registry;

    @BeforeEach
    void setup() {
        // tag::setup[]
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new DefaultMeterObservationHandler(meterRegistry));
        // end::setup[]

        this.registry = registry;
    }

    @AfterEach
    void clean() {
        executor.shutdown();
    }

    @Test
    void should_instrument_thread_switching() throws ExecutionException, InterruptedException {
        // tag::thread_switching[]
        // This snippet shows an example of how to wrap in an observation code that would
        // be executed in a separate thread

        // Let's assume that we have a parent observation
        Observation parent = Observation.createNotStarted("parent", registry);
        // Observation is put in scope via the <observe()> method
        Future<Boolean> child = parent.observe(() -> {
            // [Thread 1] Current Observation is the same as <parent>
            then(registry.getCurrentObservation()).isSameAs(parent);
            // [Thread 1] We're wrapping the executor in a Context Propagating version.
            // <ContextExecutorService> comes from Context Propagation library
            return ContextExecutorService.wrap(executor).submit(() -> {
                // [Thread 2] Current Observation is same as <parent> - context got
                // propagated
                then(registry.getCurrentObservation()).isSameAs(parent);
                // Wraps the code that should be run in a separate thread in an
                // observation
                return Observation.createNotStarted("child", registry).observe(this::yourCodeToMeasure);
            });
        });
        // end::thread_switching[]
        then(child.get()).isTrue();
    }

    @Test
    void should_instrument_reactor() {
        // tag::reactor[]
        // This snippet shows an example of how to wrap code that is using Reactor

        // Let's assume that we have a parent observation
        Observation parent = Observation.start("parent", registry);

        // We want to create a child observation for a Reactor stream
        Observation child = Observation.start("child", registry)
                // There's no thread local entry, so we will pass parent observation
                // manually. If we put the Observation in scope we could then call
                // <.contextCapture()> method from Reactor to capture all thread locals
                // and store them in Reactor Context.
                .parentObservation(parent);
        Integer block = Mono.just(1)
                // Example of not propagating context by default
                .doOnNext(integer -> {
                    log.info(
                            "No context propagation happens by default in Reactor - there will be no Observation in thread local here ["
                                    + registry.getCurrentObservation() + "]");
                    then(registry.getCurrentObservation()).isNull();
                })
                // Example of having entries in thread local for <tap()> operator
                .tap(() -> new DefaultSignalListener<Integer>() {
                    @Override
                    public void doFirst() throws Throwable {
                        log.info("We're using tap() -> there will be Observation in thread local here ["
                                + registry.getCurrentObservation() + "]");
                        then(registry.getCurrentObservation()).isNotNull();
                    }
                }).flatMap(integer -> Mono.just(integer).map(monoInteger -> monoInteger + 1))
                // Example of retrieving ThreadLocal entris via ReactorContext
                .transformDeferredContextual((integerMono, contextView) -> integerMono.doOnNext(integer -> {
                    try (ContextSnapshot.Scope scope = ContextSnapshot.setAllThreadLocalsFrom(contextView)) {
                        log.info(
                                "We're retrieving thread locals from Reactor Context - there will be Observation in thread local here ["
                                        + registry.getCurrentObservation() + "]");
                        then(registry.getCurrentObservation()).isNotNull();
                    }
                }))
                // Example of having entries in thread local for <handle()> operator
                .handle((BiConsumer<Integer, SynchronousSink<Integer>>) (integer, synchronousSink) -> {
                    log.info("We're using handle() -> There will be Observation in thread local here ["
                            + registry.getCurrentObservation() + "]");
                    then(registry.getCurrentObservation()).isNotNull();
                    synchronousSink.next(integer);
                })
                // Remember to stop the child Observation!
                .doFinally(signalType -> child.stop())
                // When using Reactor we ALWAYS search for
                // ObservationThreadLocalAccessor.KEY entry in the Reactor Context to
                // search for an Observation
                .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, child))
                // If there were ThreadLocal entries that are using Micrometer Context
                // Propagation they would be caught here. All implementations of
                // <ThreadLocalAccessor> will store their thread local entries under their
                // keys in Reactor Context
                .contextCapture().block();

        // We didn't have any observations in thread local
        then(registry.getCurrentObservation()).isNull();

        // We need to stop the parent
        parent.stop();

        then(block).isEqualTo(2);
        // end::reactor[]
    }

    @Test
    void should_instrument_http_client(WireMockRuntimeInfo info) throws IOException {
        // tag::http_client[]
        // This example can be combined with the idea of ObservationConvention to allow
        // users to easily customize the key values. Please read the rest of the
        // documentation on how to do it.

        // In Micrometer Tracing we would have predefined
        // PropagatingSenderTracingObservationHandler but for the sake of this demo we
        // create our own handler that puts "foo":"bar" headers into the request
        registry.observationConfig().observationHandler(new HeaderPropagatingHandler());

        // We're using WireMock to stub the HTTP GET call to "/foo" with a response "OK"
        stubFor(get("/foo").willReturn(ok().withBody("OK")));

        // RequestReplySenderContext is a special type of context used for request-reply
        // communication. It requires to define what the Request type is and how we can
        // instrument it. It also needs to know what the Response type is
        RequestReplySenderContext<HttpUriRequestBase, ClassicHttpResponse> context = new RequestReplySenderContext<>(
                (carrier, key, value) -> Objects.requireNonNull(carrier).addHeader(key, value));

        // We're instrumenting the Apache HTTPClient
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // The HttpGet is our carrier (we can mutate it to instrument the headers)
            HttpGet httpget = new HttpGet("http://localhost:" + info.getHttpPort() + "/foo");
            // We must set the carrier BEFORE we run <Observation#start>
            context.setCarrier(httpget);
            // You can set the remote service address to provide more debugging
            // information
            context.setRemoteServiceAddress(info.getHttpBaseUrl() + ":" + info.getHttpPort());
            // Examples of setting key values from the request
            Observation observation = Observation.createNotStarted("http.client.requests", () -> context, registry)
                    .contextualName("HTTP " + httpget.getMethod())
                    .lowCardinalityKeyValue("http.url", info.getHttpBaseUrl() + ":" + info.getHttpPort() + "/{name}")
                    .highCardinalityKeyValue("http.full-url", httpget.getRequestUri());
            observation.observeChecked(() -> {
                String response = httpclient.execute(httpget, classicHttpResponse -> {
                    // We should set the response before we stop the observation
                    context.setResponse(classicHttpResponse);
                    // Example of setting key values from the response
                    observation.highCardinalityKeyValue("http.content.length",
                            String.valueOf(classicHttpResponse.getEntity().getContentLength()));
                    return EntityUtils.toString(classicHttpResponse.getEntity());
                });

                then(response).isEqualTo("OK");
            });
        }

        // We want to be sure that we have successfully enriched the HTTP headers
        verify(getRequestedFor(urlEqualTo("/foo")).withHeader("foo", equalTo("bar")));
        // end::http_client[]
    }

    @Test
    void should_instrument_http_server() throws IOException {
        // tag::http_server[]
        // This example can be combined with the idea of ObservationConvention to allow
        // users to easily customize the key values. Please read the rest of the
        // documentation on how to do it.

        // In Micrometer Tracing we would have predefined
        // PropagatingReceiverTracingObservationHandler but for the sake of this demo we
        // create our own handler that will reuse the <foo> header from the request as a
        // low cardinality key value
        registry.observationConfig().observationHandler(new HeaderReadingHandler());

        try (Javalin javalin = Javalin.create().before("/hello/{name}", ctx -> {
            // We're creating the special RequestReplyReceiver context that will reuse the
            // information from the HTTP headers
            RequestReplyReceiverContext<Context, Context> receiverContext = new RequestReplyReceiverContext<>(
                    Context::header);
            // Remember to set the carrier!!!
            receiverContext.setCarrier(ctx);
            receiverContext.setRemoteServiceAddress(ctx.scheme() + "://" + ctx.host());
            // We're starting an Observation with the context
            Observation observation = Observation
                    .createNotStarted("http.server.requests", () -> receiverContext, registry)
                    .contextualName("HTTP " + ctx.method() + " " + ctx.matchedPath())
                    .lowCardinalityKeyValue("http.url", ctx.scheme() + "://" + ctx.host() + ctx.matchedPath())
                    .highCardinalityKeyValue("http.full-url", ctx.scheme() + "://" + ctx.host() + ctx.path())
                    .lowCardinalityKeyValue("http.method", ctx.method()).start();
            // Let's be consistent and always set the Observation related objects under
            // the same key
            ctx.attribute(ObservationThreadLocalAccessor.KEY, observation);
        }).get("/hello/{name}", ctx -> {
            // We need to be thread-safe - we're not using ThreadLocals, we're retrieving
            // information from the attributes
            Observation observation = ctx.attribute(ObservationThreadLocalAccessor.KEY);
            observation.scoped(() -> {
                // If we need thread locals (e.g. MDC entries) we can use <scoped()>
                log.info("We're using scoped - Observation in thread local here [" + registry.getCurrentObservation()
                        + "]");
                then(registry.getCurrentObservation()).isNotNull();
            });
            // We're returning body
            ctx.result("Hello World [" + observation.getContext().getLowCardinalityKeyValue("foo").getValue() + "]");
        }).after("/hello/{name}", ctx -> {
            // After sending the response we want to stop the Observation
            Observation observation = ctx.attribute(ObservationThreadLocalAccessor.KEY);
            observation.stop();
        }).start()) {

            // We're sending an HTTP request with a <foo:bar> header. We're expecting that
            // it will be reused in the response
            String response = sendRequestToHelloEndpointWithHeader(javalin.port(), "foo", "bar");

            // The response must contain the value from the header
            then(response).isEqualTo("Hello World [bar]");
        }
        // end::http_server[]
    }

    private String sendRequestToHelloEndpointWithHeader(int port, String headerName, String headerValue) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // The HttpGet is our carrier (we can mutate it to instrument the headers)
            HttpGet httpget = new HttpGet("http://localhost:" + port + "/hello/baz");
            httpget.addHeader(headerName, headerValue);
            return httpclient.execute(httpget,
                    classicHttpResponse -> EntityUtils.toString(classicHttpResponse.getEntity()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean yourCodeToMeasure() {
        return true;
    }

    // tag::header_propagating_handler[]
    static class HeaderPropagatingHandler implements ObservationHandler<SenderContext<Object>> {

        @Override
        public void onStart(SenderContext<Object> context) {
            context.getSetter().set(context.getCarrier(), "foo", "bar");
        }

        @Override
        public boolean supportsContext(Observation.Context context) {
            return context instanceof SenderContext;
        }

    }
    // end::header_propagating_handler[]

    // tag::header_receiving_handler[]
    static class HeaderReadingHandler implements ObservationHandler<ReceiverContext<Context>> {

        @Override
        public void onStart(ReceiverContext<Context> context) {
            String fooHeader = context.getGetter().get(context.getCarrier(), "foo");
            // We're setting the value of the <foo> header as a low cardinality key value
            context.addLowCardinalityKeyValue(KeyValue.of("foo", fooHeader));
        }

        @Override
        public boolean supportsContext(Observation.Context context) {
            return context instanceof ReceiverContext;
        }

    }
    // end::header_receiving_handler[]

}
