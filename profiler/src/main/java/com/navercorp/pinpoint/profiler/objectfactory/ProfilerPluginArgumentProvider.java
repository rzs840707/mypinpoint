/**
 * Copyright 2014 NAVER Corp. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.navercorp.pinpoint.profiler.objectfactory;

import java.lang.annotation.Annotation;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentorDelegate;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.Name;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;
import com.navercorp.pinpoint.exception.PinpointException;
import com.navercorp.pinpoint.profiler.util.TypeUtils;

/**
 * @author Jongho Moon
 */
public class ProfilerPluginArgumentProvider implements ArgumentProvider {
    private final InstrumentContext pluginContext;
    private static final String TRACE_CLASS = "com.navercorp.pinpoint.bootstrap.context.Trace";
    private static final String TRACE_CONTEXT_CLASS = "com.navercorp.pinpoint.bootstrap.context.TraceContext";
    private static final String INSTRUMENTOR_CLASS = "com.navercorp.pinpoint.bootstrap.instrument.Instrumentor";
    private static final String INTERCEPTOR_SCOPE_CLASS = "com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope";

    public ProfilerPluginArgumentProvider(InstrumentContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    @Override
    public Option get(int index, Class<?> type, Annotation[] annotations) {
        String typeName = type.getName();
        if (typeName.equals(TRACE_CLASS)) {
            return Option.withValue(pluginContext.getTraceContext().currentTraceObject());
        } else if (typeName.equals(TRACE_CONTEXT_CLASS)) {
            return Option.withValue(pluginContext.getTraceContext());
        } else if (typeName.equals(INSTRUMENTOR_CLASS)) {
            final InstrumentorDelegate delegate = new InstrumentorDelegate(pluginContext);
            return Option.withValue(delegate);
        } else if (typeName.equals(INTERCEPTOR_SCOPE_CLASS)) {
            Name annotation = TypeUtils.findAnnotation(annotations, Name.class);

            if (annotation == null) {
                return Option.empty();
            }

            InterceptorScope scope = pluginContext.getInterceptorScope(annotation.value());

            if (scope == null) {
                throw new PinpointException("No such Scope: " + annotation.value());
            }

            return Option.withValue(scope);
        }

        return Option.empty();
    }
}
