/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.tracing;

import io.micrometer.tracing.otel.bridge.Slf4JEventListener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;

// TODO: Should this live here?
public class OpenTelemetrySlf4jApplicationListener implements ApplicationListener<ApplicationEvent> {

	private final Slf4JEventListener listener = new Slf4JEventListener();

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		Object actualEvent = event;
		if (event instanceof PayloadApplicationEvent<?> payloadEvent) {
			actualEvent = payloadEvent.getPayload();
		}
		this.listener.onEvent(actualEvent);
	}

}
