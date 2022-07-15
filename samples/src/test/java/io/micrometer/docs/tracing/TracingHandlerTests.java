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

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.docs.KeyName;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.observation.TimerObservationHandler;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationTextPublisher;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.observation.docs.DocumentedObservation;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/**
 * Sources for tracing-handler.adoc
 */
class TracingHandlerTests {

    @Test
    void timer() {
        // tag::timer[]
        MeterRegistry registry = new SimpleMeterRegistry();
        Timer.Sample sample = Timer.start(registry);
        try {
            // do some work here
        }
        finally {
            sample.stop(Timer.builder("my.timer").register(registry));
        }
        // end::timer[]
    }

    @Test
    void observation_instead_of_timer() {
        // tag::observation[]
        ObservationRegistry registry = ObservationRegistry.create();
        Observation.createNotStarted("my.operation", registry).observe(this::doSomeWorkHere);
        // end::observation[]
    }

    @Test
    void registering_handler() {
        // tag::register_handler[]
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new SimpleHandler());
        // end::register_handler[]
    }

    @Test
    void instrumenting_code() {
        // tag::instrumenting_code[]
        ObservationRegistry registry = ObservationRegistry.create();
        // using a context is optional, you can call start without it:
        // Observation.createNotStarted(name, registry)
        Observation.Context context = new Observation.Context().put(String.class, "test");
        Observation.createNotStarted("my.operation", context, registry).observe(this::doSomeWorkHere);
        // end::instrumenting_code[]
    }

    @Test
    void manual_scoping() {
        // tag::manual_scoping[]
        // using a context is optional, you can call start without it:
        // Observation.start(name, registry)
        ObservationRegistry registry = ObservationRegistry.create();
        Observation.Context context = new Observation.Context().put(String.class, "test");
        Observation observation = Observation.start("my.operation", context, registry);
        try (Observation.Scope scope = observation.openScope()) {
            doSomeWorkHere();
        }
        catch (Exception ex) {
            observation.error(ex); // and don't forget to handle exceptions
            throw ex;
        }
        finally {
            observation.stop();
        }
        // end::manual_scoping[]
    }

    @Test
    void observation_convention_example() {
        // tag::observation_convention_example[]
        // Registry setup
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        // add metrics
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        observationRegistry.observationConfig().observationHandler(new TimerObservationHandler(registry));
        observationRegistry.observationConfig()
                // these will be applied to all observations
                .keyValuesProvider(new GlobalKeyValueProvider())
                .observationConvention(new GlobalTaxObservationConvention());

        TaxCalculator taxCalculator = new TaxCalculator(observationRegistry);
        // you can use a setter to override the default tags provider
        taxCalculator.setObservationConvention(new CustomTaxObservationConvention());
        // run the logic you want to observe
        taxCalculator.calculateTax("INCOME_TAX", "1234567890");
        // end::observation_convention_example[]
    }

    @Test
    void annotatedCallShouldBeObserved() {
        // tag::observed_aop[]
        // create a test registry
        TestObservationRegistry registry = TestObservationRegistry.create();
        // add a system out printing handler
        registry.observationConfig().observationHandler(new ObservationTextPublisher());

        // create a proxy around the observed service
        AspectJProxyFactory pf = new AspectJProxyFactory(new ObservedService());
        pf.addAspect(new ObservedAspect(registry));

        // make a call
        ObservedService service = pf.getProxy();
        service.call();

        // assert that observation has been properly created
        // @formatter:off
        TestObservationRegistryAssert.assertThat(registry)
                .hasSingleObservationThat()
                .hasBeenStopped()
                .hasNameEqualTo("test.call")
                .hasContextualNameEqualTo("test#call")
                .hasLowCardinalityKeyValue("abc", "123")
                .hasLowCardinalityKeyValue("test", "42")
                .hasLowCardinalityKeyValue("class", ObservedService.class.getName())
                .hasLowCardinalityKeyValue("method", "call").doesNotHaveError();
        // @formatter:on
        // end::observed_aop[]
    }

    private void doSomeWorkHere() {

    }

    // tag::simple_handler[]
    static class SimpleHandler implements ObservationHandler<Observation.Context> {

        @Override
        public void onStart(Observation.Context context) {
            System.out.println("START " + "data: " + context.get(String.class));
        }

        @Override
        public void onError(Observation.Context context) {
            System.out
                    .println("ERROR " + "data: " + context.get(String.class) + ", error: " + context.getError().get());
        }

        @Override
        public void onStop(Observation.Context context) {
            System.out.println("STOP  " + "data: " + context.get(String.class));
        }

        @Override
        public boolean supportsContext(Observation.Context handlerContext) {
            return true; // you can decide if your handler should be invoked for this
            // context object or not
        }

    }
    // end::simple_handler[]

    // tag::tax_example[]

    /**
     * A dedicated {@link Observation.Context} used for taxing.
     */
    class TaxContext extends Observation.Context {

        private final String taxType;

        private final String userId;

        TaxContext(String taxType, String userId) {
            this.taxType = taxType;
            this.userId = userId;
        }

        public String getTaxType() {
            return taxType;
        }

        public String getUserId() {
            return userId;
        }

    }

    /**
     * An example of a {@link Observation.GlobalKeyValuesProvider} that adds cloud related
     * tags to all contexts. When registered via the
     * `ObservationRegistry#observationConfig#keyValueProvider` will be applied globally.
     */
    class GlobalKeyValueProvider implements Observation.GlobalKeyValuesProvider<Observation.Context> {

        @Override
        public KeyValues getLowCardinalityKeyValues(Observation.Context context) {
            return KeyValues.of(KeyValue.of("cloud.zone", CloudUtils.getZone()));
        }

        @Override
        public KeyValues getHighCardinalityKeyValues(Observation.Context context) {
            return KeyValues.of(KeyValue.of("cloud.instance.id", CloudUtils.getCloudInstanceId()));
        }

        @Override
        public boolean supportsContext(Observation.Context context) {
            return true;
        }

    }

    /**
     * An example of a {@link Observation.ObservationConvention} that renames the tax
     * related observations. When registered via the
     * `ObservationRegistry#observationConfig#observationConvention` will be applied
     * globally.
     */
    class GlobalTaxObservationConvention implements Observation.GlobalObservationConvention<TaxContext> {

        // this will be applicable for all tax contexts - it will rename all the tax
        // contexts
        @Override
        public boolean supportsContext(Observation.Context context) {
            return context instanceof TaxContext;
        }

        @Override
        public String getName() {
            return "global.tax.calculate";
        }

    }

    // Interface for a ObservationConvention related to calculating Tax
    interface TaxObservationConvention extends Observation.ObservationConvention<TaxContext> {

        @Override
        default boolean supportsContext(Observation.Context context) {
            return context instanceof TaxContext;
        }

    }

    /**
     * Default provider of tags related to calculating tax. If no other will be provided
     * either via a setter or global registration then this one will be picked.
     */
    class DefaultTaxObservationConvention implements TaxObservationConvention {

        @Override
        public KeyValues getLowCardinalityKeyValues(TaxContext context) {
            return KeyValues.of(TaxObservation.TaxLowCardinalityKeyNames.TAX_TYPE.of(context.getTaxType()));
        }

        @Override
        public KeyValues getHighCardinalityKeyValues(TaxContext context) {
            return KeyValues.of(TaxObservation.TaxHighCardinalityKeyNames.USER_ID.of(context.getUserId()));
        }

        @Override
        public String getName() {
            return "default.tax.name";
        }

    }

    /**
     * If micrometer-docs-generator is used, we will automatically generate documentation
     * for your observations. Check this URL
     * https://github.com/micrometer-metrics/micrometer-docs-generator#documentation for
     * setup example and read the {@link DocumentedObservation} javadocs.
     */
    enum TaxObservation implements DocumentedObservation {

        CALCULATE {
            @Override
            public Class<? extends Observation.ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
                return DefaultTaxObservationConvention.class;
            }

            @Override
            public String getContextualName() {
                return "calculate tax";
            }

            @Override
            public String getPrefix() {
                return "tax";
            }

            @Override
            public KeyName[] getLowCardinalityKeyNames() {
                return TaxLowCardinalityKeyNames.values();
            }

            @Override
            public KeyName[] getHighCardinalityKeyNames() {
                return TaxHighCardinalityKeyNames.values();
            }
        };

        enum TaxLowCardinalityKeyNames implements KeyName {

            TAX_TYPE {
                @Override
                public String getKeyName() {
                    return "tax.type";
                }
            }

        }

        enum TaxHighCardinalityKeyNames implements KeyName {

            USER_ID {
                @Override
                public String getKeyName() {
                    return "tax.user.id";
                }
            }

        }

    }

    /**
     * Our business logic that we want to observe.
     */
    class TaxCalculator implements Observation.ObservationConventionAware<TaxObservationConvention> {

        private final ObservationRegistry observationRegistry;

        private TaxObservationConvention observationConvention;

        TaxCalculator(ObservationRegistry observationRegistry) {
            this.observationRegistry = observationRegistry;
        }

        public void calculateTax(String taxType, String userId) {
            // Create a new context
            TaxContext taxContext = new TaxContext(taxType, userId);
            // Create a new observation
            TaxObservation.CALCULATE
                    .observation(this.observationConvention, new DefaultTaxObservationConvention(), taxContext,
                            observationRegistry)
                    // Run the actual logic you want to observe
                    .observe(this::calculateInterest);
        }

        private void calculateInterest() {
            // do some work
        }

        // Use this if you want to override the defaults
        @Override
        public void setObservationConvention(TaxObservationConvention observationConvention) {
            this.observationConvention = observationConvention;
        }

    }

    /**
     * Example of user changing the default conventions.
     */
    class CustomTaxObservationConvention extends DefaultTaxObservationConvention {

        @Override
        public KeyValues getLowCardinalityKeyValues(TaxContext context) {
            return super.getLowCardinalityKeyValues(context)
                    .and(KeyValue.of("additional.low.cardinality.tag", "value"));
        }

        @Override
        public KeyValues getHighCardinalityKeyValues(TaxContext context) {
            return KeyValues.of("this.would.override.the.default.high.cardinality.tags", "value");
        }

        @Override
        public String getName() {
            return "tax.calculate";
        }

    }

    /**
     * A utility class to set cloud related arguments.
     */
    static class CloudUtils {

        static String getZone() {
            return "...";
        }

        static String getCloudInstanceId() {
            return "...";
        }

    }
    // end::tax_example[]

    // tag::observed_service[]
    static class ObservedService {

        @Observed(name = "test.call", contextualName = "test#call",
                lowCardinalityKeyValues = { "abc", "123", "test", "42" })
        void call() {
            System.out.println("call");
        }

    }
    // end::observed_service[]

}
