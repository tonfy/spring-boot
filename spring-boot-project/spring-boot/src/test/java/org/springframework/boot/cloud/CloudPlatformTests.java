/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.cloud;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CloudPlatform}.
 *
 * @author Phillip Webb
 */
class CloudPlatformTests {

	@Test
	void getActiveWhenEnvironmentIsNullShouldReturnNull() {
		CloudPlatform platform = CloudPlatform.getActive(null);
		assertThat(platform).isNull();
	}

	@Test
	void getActiveWhenNotInCloudShouldReturnNull() {
		Environment environment = new MockEnvironment();
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isNull();

	}

	@Test
	void getActiveWhenHasVcapApplicationShouldReturnCloudFoundry() {
		Environment environment = new MockEnvironment().withProperty("VCAP_APPLICATION", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.CLOUD_FOUNDRY);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	void getActiveWhenHasVcapServicesShouldReturnCloudFoundry() {
		Environment environment = new MockEnvironment().withProperty("VCAP_SERVICES", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.CLOUD_FOUNDRY);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	void getActiveWhenHasDynoShouldReturnHeroku() {
		Environment environment = new MockEnvironment().withProperty("DYNO", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.HEROKU);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	void getActiveWhenHasHcLandscapeShouldReturnSap() {
		Environment environment = new MockEnvironment().withProperty("HC_LANDSCAPE", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.SAP);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	void getActiveWhenHasServiceHostAndServicePortShouldReturnKubernetes() {
		MockEnvironment environment = new MockEnvironment();
		Map<String, Object> source = new HashMap<>();
		source.put("EXAMPLE_SERVICE_HOST", "---");
		source.put("EXAMPLE_SERVICE_PORT", "8080");
		PropertySource<?> propertySource = new SystemEnvironmentPropertySource(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, source);
		environment.getPropertySources().addFirst(propertySource);
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.KUBERNETES);
	}

	@Test
	void getActiveWhenHasServiceHostAndNoServicePortShouldNotReturnKubernetes() {
		MockEnvironment environment = new MockEnvironment();
		PropertySource<?> propertySource = new SystemEnvironmentPropertySource(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
				Collections.singletonMap("EXAMPLE_SERVICE_HOST", "---"));
		environment.getPropertySources().addFirst(propertySource);
		CloudPlatform platform = CloudPlatform.getActive(environment);
		File path = new File("/var/run/secrets/kubernetes.io");
		if ( !path.exists() && !path.isDirectory())
			assertThat(platform).isNull();

	}

}
