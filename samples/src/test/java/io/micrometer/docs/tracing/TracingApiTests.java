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
package io.micrometer.docs.tracing;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.handler.SpanHandler;
import brave.propagation.B3Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.sampler.Sampler;
import io.micrometer.tracing.*;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import io.micrometer.tracing.otel.bridge.*;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporterBuilder;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * Sources for tracing-api.adoc
 */
class TracingApiTests {

    @Nested
    class BraveTests {

        // tag::brave_setup[]
        // [Brave component] Example of using a SpanHandler. SpanHandler is a component
        // that gets called when a span is finished. Here we have an example of setting it
        // up with sending spans
        // in a Zipkin format to the provided location via the UrlConnectionSender
        // (through the <io.zipkin.reporter2:zipkin-sender-urlconnection> dependency)
        // Another option could be to use a TestSpanHandler for testing purposes.
        SpanHandler spanHandler = ZipkinSpanHandler
            .create(AsyncReporter.create(URLConnectionSender.create("http://localhost:9411/api/v2/spans")));

        // [Brave component] CurrentTraceContext is a Brave component that allows you to
        // retrieve the current TraceContext.
        ThreadLocalCurrentTraceContext braveCurrentTraceContext = ThreadLocalCurrentTraceContext.newBuilder()
            .addScopeDecorator(MDCScopeDecorator.get()) // Example of Brave's
                                                        // automatic MDC setup
            .build();

        // [Micrometer Tracing component] A Micrometer Tracing wrapper for Brave's
        // CurrentTraceContext
        CurrentTraceContext bridgeContext = new BraveCurrentTraceContext(this.braveCurrentTraceContext);

        // [Brave component] Tracing is the root component that allows to configure the
        // tracer, handlers, context propagation etc.
        // tag::baggage_brave_setup[]
        Tracing tracing = Tracing.newBuilder()
            .currentTraceContext(this.braveCurrentTraceContext)
            .supportsJoin(false)
            .traceId128Bit(true)
            // For Baggage to work you need to provide a list of fields to propagate
            .propagationFactory(BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
                .add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("from_span_in_scope 1")))
                .add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("from_span_in_scope 2")))
                .add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("from_span")))
                .build())
            .sampler(Sampler.ALWAYS_SAMPLE)
            .addSpanHandler(this.spanHandler)
            .build();

        // end::baggage_brave_setup[]

        // [Brave component] Tracer is a component that handles the life-cycle of a span
        brave.Tracer braveTracer = this.tracing.tracer();

        // [Micrometer Tracing component] A Micrometer Tracing wrapper for Brave's Tracer
        Tracer tracer = new BraveTracer(this.braveTracer, this.bridgeContext, new BraveBaggageManager());

        // end::brave_setup[]

        @AfterEach
        void close() {
            this.tracing.close();
            ((ZipkinSpanHandler) this.spanHandler).close();
        }

        @Test
        void should_create_a_span_with_tracer() {
            String taxValue = "10";

            // tag::manual_span_creation[]
            // Create a span. If there was a span present in this thread it will become
            // the `newSpan`'s parent.
            Span newSpan = this.tracer.nextSpan().name("calculateTax");
            // Start a span and put it in scope. Putting in scope means putting the span
            // in thread local
            // and, if configured, adjust the MDC to contain tracing information
            try (Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {
                // ...
                // You can tag a span - put a key value pair on it for better debugging
                newSpan.tag("taxValue", taxValue);
                // ...
                // You can log an event on a span - an event is an annotated timestamp
                newSpan.event("taxCalculated");
            }
            finally {
                // Once done remember to end the span. This will allow collecting
                // the span to send it to a distributed tracing system e.g. Zipkin
                newSpan.end();
            }
            // end::manual_span_creation[]
        }

        @Test
        void should_continue_a_span_with_tracer() throws Exception {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            String taxValue = "10";
            // tag::manual_span_continuation[]
            Span spanFromThreadX = this.tracer.nextSpan().name("calculateTax");
            try (Tracer.SpanInScope ws = this.tracer.withSpan(spanFromThreadX.start())) {
                executorService.submit(() -> {
                    // Pass the span from thread X
                    Span continuedSpan = spanFromThreadX;
                    // ...
                    // You can tag a span
                    continuedSpan.tag("taxValue", taxValue);
                    // ...
                    // You can log an event on a span
                    continuedSpan.event("taxCalculated");
                }).get();
            }
            finally {
                spanFromThreadX.end();
            }
            // end::manual_span_continuation[]

            executorService.shutdown();
        }

        @Test
        void should_start_a_span_with_explicit_parent() throws Exception {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            String commissionValue = "10";
            Span initialSpan = this.tracer.nextSpan().name("calculateTax").start();

            executorService.submit(() -> {
                // tag::manual_span_joining[]
                // let's assume that we're in a thread Y and we've received
                // the `initialSpan` from thread X. `initialSpan` will be the parent
                // of the `newSpan`
                Span newSpan = this.tracer.nextSpan(initialSpan).name("calculateCommission");
                // ...
                // You can tag a span
                newSpan.tag("commissionValue", commissionValue);
                // ...
                // You can log an event on a span
                newSpan.event("commissionCalculated");
                // Once done remember to end the span. This will allow collecting
                // the span to send it to e.g. Zipkin. The tags and events set on the
                // newSpan will not be present on the parent
                newSpan.end();
                // end::manual_span_joining[]
            }).get();

            executorService.shutdown();
        }

        @Test
        void should_work_with_legacy_baggage_api() {
            // tag::baggage_api[]
            Span span = tracer.nextSpan().name("parent").start();

            // Assuming that there's a span in scope...
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {

                // Not passing a TraceContext explicitly will bind the baggage to the
                // current TraceContext
                // If you want to retrieve the baggage value you should make it current
                // first
                try (BaggageInScope baggage = tracer.createBaggage("from_span_in_scope 1", "value 1").makeCurrent()) {
                    // This is how you retrieve the baggage
                    String baggageValue = baggage.get();
                    then(baggageValue).as("[In scope] Baggage 1").isEqualTo("value 1");

                    String baggageValueViaTracer = tracer.getBaggage("from_span_in_scope 1").get();
                    then(baggageValueViaTracer).as("[In scope] Baggage 1").isEqualTo("value 1");
                }

                try (BaggageInScope baggage = tracer.createBaggage("from_span_in_scope 2", "value 2").makeCurrent()) {
                    then(baggage.get()).as("[In scope] Baggage 2").isEqualTo("value 2");
                    then(tracer.getBaggage("from_span_in_scope 2").get()).as("[In scope] Baggage 2")
                        .isEqualTo("value 2");
                }
            }

            // Assuming that you have a handle to the span
            try (BaggageInScope baggage = tracer.createBaggage("from_span")
                .set(span.context(), "value 3")
                .makeCurrent()) {
                String baggageValueFromATraceContext = baggage.get(span.context());
                then(baggageValueFromATraceContext).as("[Span passed explicitly] Baggage 3").isEqualTo("value 3");

                String baggageValueFromATraceContextThroughTracer = tracer.getBaggage("from_span").get(span.context());
                then(baggageValueFromATraceContextThroughTracer).as("[Span passed explicitly] Baggage 3")
                    .isEqualTo("value 3");
            }

            // Assuming that there's no span in scope
            // When there's no span in scope, there will never be any baggage - even if
            // you make it current
            try (BaggageInScope baggage = tracer.createBaggage("from_span_in_scope 1", "value 1").makeCurrent()) {
                then(baggage.get()).as("[Out of span scope] Baggage 1").isNull();
                then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of span scope] Baggage 1").isNull();
            }
            then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of scope] Baggage 1").isNull();
            then(tracer.getBaggage("from_span_in_scope 2").get()).as("[Out of scope] Baggage 2").isNull();
            then(tracer.getBaggage("from_span").get()).as("[Out of scope] Baggage 3").isNull();
            then(tracer.getBaggage("from_span").get(span.context())).as("[Out of scope - with context] Baggage 3")
                .isNull();
            // end::baggage_api[]
        }

        @Test
        void should_work_with_baggage_with_1_11_api() {
            // tag::baggage_api_1_11_0[]
            Span span = tracer.nextSpan().name("parent").start();

            // Assuming that there's a span in scope...
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {

                // Not passing a TraceContext explicitly will bind the baggage to the
                // current TraceContext
                try (BaggageInScope baggage = tracer.createBaggageInScope("from_span_in_scope 1", "value 1")) {
                    // This is how you retrieve the baggage
                    String baggageValue = baggage.get();
                    then(baggageValue).as("[In scope] Baggage 1").isEqualTo("value 1");

                    String baggageValueViaTracer = tracer.getBaggage("from_span_in_scope 1").get();
                    then(baggageValueViaTracer).as("[In scope] Baggage 1").isEqualTo("value 1");
                }

                try (BaggageInScope baggage = tracer.createBaggageInScope("from_span_in_scope 2", "value 2")) {
                    then(baggage.get()).as("[In scope] Baggage 2").isEqualTo("value 2");
                    then(tracer.getBaggage("from_span_in_scope 2").get()).as("[In scope] Baggage 2")
                        .isEqualTo("value 2");
                }
            }

            // Assuming that you have a handle to the span
            try (BaggageInScope baggage = tracer.createBaggageInScope(span.context(), "from_span", "value 3")) {
                String baggageValueFromATraceContext = baggage.get(span.context());
                then(baggageValueFromATraceContext).as("[Span passed explicitly] Baggage 3").isEqualTo("value 3");

                String baggageValueFromATraceContextThroughTracer = tracer.getBaggage("from_span").get(span.context());
                then(baggageValueFromATraceContextThroughTracer).as("[Span passed explicitly] Baggage 3")
                    .isEqualTo("value 3");
            }

            // Assuming that there's no span in scope
            // When there's no span in scope, there will never be any baggage - even if
            // you make it current
            try (BaggageInScope baggage = tracer.createBaggageInScope("from_span_in_scope 1", "value 1")) {
                then(baggage.get()).as("[Out of span scope] Baggage 1").isNull();
                then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of span scope] Baggage 1").isNull();
            }
            then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of scope] Baggage 1").isNull();
            then(tracer.getBaggage("from_span_in_scope 2").get()).as("[Out of scope] Baggage 2").isNull();
            then(tracer.getBaggage("from_span").get()).as("[Out of scope] Baggage 3").isNull();
            then(tracer.getBaggage("from_span").get(span.context())).as("[Out of scope - with context] Baggage 3")
                .isNull();
            // end::baggage_api_1_11_0[]
        }

    }

    @Nested
    class OtelTests {

        // tag::otel_setup[]
        // [OTel component] Example of using a SpanExporter. SpanExporter is a component
        // that gets called when a span is finished. Here we have an example of setting it
        // up with sending spans
        // in a Zipkin format to the provided location via the UrlConnectionSender
        // (through the <io.opentelemetry:opentelemetry-exporter-zipkin> and
        // <io.zipkin.reporter2:zipkin-sender-urlconnection> dependencies)
        // Another option could be to use an ArrayListSpanProcessor for testing purposes
        SpanExporter spanExporter = new ZipkinSpanExporterBuilder()
            .setSender(URLConnectionSender.create("http://localhost:9411/api/v2/spans"))
            .build();

        // [OTel component] SdkTracerProvider is an SDK implementation for TracerProvider
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
            .setSampler(alwaysOn())
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .build();

        // [OTel component] The SDK implementation of OpenTelemetry
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider)
            .setPropagators(ContextPropagators.create(B3Propagator.injectingSingleHeader()))
            .build();

        // [OTel component] Tracer is a component that handles the life-cycle of a span
        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk.getTracerProvider()
            .get("io.micrometer.micrometer-tracing");

        // [Micrometer Tracing component] A Micrometer Tracing wrapper for OTel
        OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();

        // [Micrometer Tracing component] A Micrometer Tracing listener for setting up MDC
        Slf4JEventListener slf4JEventListener = new Slf4JEventListener();

        // [Micrometer Tracing component] A Micrometer Tracing listener for setting
        // Baggage in MDC. Customizable
        // with correlation fields (currently we're setting empty list)
        Slf4JBaggageEventListener slf4JBaggageEventListener = new Slf4JBaggageEventListener(Collections.emptyList());

        // [Micrometer Tracing component] A Micrometer Tracing wrapper for OTel's Tracer.
        // You can consider
        // customizing the baggage manager with correlation and remote fields (currently
        // we're setting empty lists)
        OtelTracer tracer = new OtelTracer(otelTracer, otelCurrentTraceContext, event -> {
            slf4JEventListener.onEvent(event);
            slf4JBaggageEventListener.onEvent(event);
        }, new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(), Collections.emptyList()));

        // end::otel_setup[]

        @BeforeEach
        void setup() {
            this.spanExporter.close();
        }

        @AfterEach
        void close() {
            this.sdkTracerProvider.close();
        }

        @Test
        void should_create_a_span_with_tracer() {
            String taxValue = "10";

            // Create a span. If there was a span present in this thread it will become
            // the `newSpan`'s parent.
            Span newSpan = this.tracer.nextSpan().name("calculateTax");
            // Start a span and put it in scope. Putting in scope means putting the span
            // in thread local
            // and, if configured, adjust the MDC to contain tracing information
            try (Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {
                // ...
                // You can tag a span - put a key value pair on it for better debugging
                newSpan.tag("taxValue", taxValue);
                // ...
                // You can log an event on a span - an event is an annotated timestamp
                newSpan.event("taxCalculated");
            }
            finally {
                // Once done remember to end the span. This will allow collecting
                // the span to send it to a distributed tracing system e.g. Zipkin
                newSpan.end();
            }
        }

        @Test
        void should_continue_a_span_with_tracer() throws Exception {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            String taxValue = "10";
            Span spanFromThreadX = this.tracer.nextSpan().name("calculateTax");
            try (Tracer.SpanInScope ws = this.tracer.withSpan(spanFromThreadX.start())) {
                executorService.submit(() -> {
                    // Pass the span from thread X
                    Span continuedSpan = spanFromThreadX;
                    // ...
                    // You can tag a span
                    continuedSpan.tag("taxValue", taxValue);
                    // ...
                    // You can log an event on a span
                    continuedSpan.event("taxCalculated");
                }).get();
            }
            finally {
                spanFromThreadX.end();
            }

            executorService.shutdown();
        }

        @Test
        void should_start_a_span_with_explicit_parent() throws Exception {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            String commissionValue = "10";
            Span initialSpan = this.tracer.nextSpan().name("calculateTax").start();

            executorService.submit(() -> {
                // let's assume that we're in a thread Y and we've received
                // the `initialSpan` from thread X. `initialSpan` will be the parent
                // of the `newSpan`
                Span newSpan = this.tracer.nextSpan(initialSpan).name("calculateCommission");
                // ...
                // You can tag a span
                newSpan.tag("commissionValue", commissionValue);
                // ...
                // You can log an event on a span
                newSpan.event("commissionCalculated");
                // Once done remember to end the span. This will allow collecting
                // the span to send it to e.g. Zipkin. The tags and events set on the
                // newSpan will not be present on the parent
                newSpan.end();
            }).get();

            executorService.shutdown();
        }

        @Test
        void should_work_with_legacy_baggage_api() {
            Span span = tracer.nextSpan().name("parent").start();

            // Assuming that there's a span in scope...
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {

                // Not passing a TraceContext explicitly will bind the baggage to the
                // current TraceContext
                // If you want to retrieve the baggage value you should make it current
                // first
                try (BaggageInScope baggage = tracer.createBaggage("from_span_in_scope 1", "value 1").makeCurrent()) {
                    // This is how you retrieve the baggage
                    String baggageValue = baggage.get();
                    then(baggageValue).as("[In scope] Baggage 1").isEqualTo("value 1");

                    String baggageValueViaTracer = tracer.getBaggage("from_span_in_scope 1").get();
                    then(baggageValueViaTracer).as("[In scope] Baggage 1").isEqualTo("value 1");
                }

                try (BaggageInScope baggage = tracer.createBaggage("from_span_in_scope 2", "value 2").makeCurrent()) {
                    then(baggage.get()).as("[In scope] Baggage 2").isEqualTo("value 2");
                    then(tracer.getBaggage("from_span_in_scope 2").get()).as("[In scope] Baggage 2")
                        .isEqualTo("value 2");
                }
            }

            // Assuming that you have a handle to the span
            try (BaggageInScope baggage = tracer.createBaggage("from_span")
                .set(span.context(), "value 3")
                .makeCurrent()) {
                String baggageValueFromATraceContext = baggage.get(span.context());
                then(baggageValueFromATraceContext).as("[Span passed explicitly] Baggage 3").isEqualTo("value 3");

                String baggageValueFromATraceContextThroughTracer = tracer.getBaggage("from_span").get(span.context());
                then(baggageValueFromATraceContextThroughTracer).as("[Span passed explicitly] Baggage 3")
                    .isEqualTo("value 3");
            }

            // Assuming that there's no span in scope
            // When there's no span in scope for OTel there will be baggage
            try (BaggageInScope baggage = tracer.createBaggage("from_span_in_scope 1", "value 1").makeCurrent()) {
                then(baggage.get()).as("[Out of span scope] Baggage 1").isNotNull();
                then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of span scope] Baggage 1").isNotNull();
            }
            then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of scope] Baggage 1").isNull();
            then(tracer.getBaggage("from_span_in_scope 2").get()).as("[Out of scope] Baggage 2").isNull();
            then(tracer.getBaggage("from_span").get()).as("[Out of scope] Baggage 3").isNull();
            then(tracer.getBaggage("from_span").get(span.context())).as("[Out of scope - with context] Baggage 3")
                .isNull();
        }

        @Test
        void should_work_with_baggage_with_1_11_api() {
            Span span = tracer.nextSpan().name("parent").start();

            // Assuming that there's a span in scope...
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {

                // Not passing a TraceContext explicitly will bind the baggage to the
                // current TraceContext
                try (BaggageInScope baggage = tracer.createBaggageInScope("from_span_in_scope 1", "value 1")) {
                    // This is how you retrieve the baggage
                    String baggageValue = baggage.get();
                    then(baggageValue).as("[In scope] Baggage 1").isEqualTo("value 1");

                    String baggageValueViaTracer = tracer.getBaggage("from_span_in_scope 1").get();
                    then(baggageValueViaTracer).as("[In scope] Baggage 1").isEqualTo("value 1");
                }

                try (BaggageInScope baggage = tracer.createBaggageInScope("from_span_in_scope 2", "value 2")) {
                    then(baggage.get()).as("[In scope] Baggage 2").isEqualTo("value 2");
                    then(tracer.getBaggage("from_span_in_scope 2").get()).as("[In scope] Baggage 2")
                        .isEqualTo("value 2");
                }
            }

            // Assuming that you have a handle to the span
            try (BaggageInScope baggage = tracer.createBaggageInScope(span.context(), "from_span", "value 3")) {
                String baggageValueFromATraceContext = baggage.get(span.context());
                then(baggageValueFromATraceContext).as("[Span passed explicitly] Baggage 3").isEqualTo("value 3");

                String baggageValueFromATraceContextThroughTracer = tracer.getBaggage("from_span").get(span.context());
                then(baggageValueFromATraceContextThroughTracer).as("[Span passed explicitly] Baggage 3")
                    .isEqualTo("value 3");
            }

            // Assuming that there's no span in scope
            // When there's no span in scope for OTel there will be baggage
            try (BaggageInScope baggage = tracer.createBaggageInScope("from_span_in_scope 1", "value 1")) {
                then(baggage.get()).as("[Out of span scope] Baggage 1").isNotNull();
                then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of span scope] Baggage 1").isNotNull();
            }
            then(tracer.getBaggage("from_span_in_scope 1").get()).as("[Out of scope] Baggage 1").isNull();
            then(tracer.getBaggage("from_span_in_scope 2").get()).as("[Out of scope] Baggage 2").isNull();
            then(tracer.getBaggage("from_span").get()).as("[Out of scope] Baggage 3").isNull();
            then(tracer.getBaggage("from_span").get(span.context())).as("[Out of scope - with context] Baggage 3")
                .isNull();
        }

    }

}
