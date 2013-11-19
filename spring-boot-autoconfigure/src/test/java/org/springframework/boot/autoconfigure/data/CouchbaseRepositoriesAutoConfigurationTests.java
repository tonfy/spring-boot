/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.data;

import com.couchbase.client.CouchbaseClient;
import org.junit.Test;
import org.springframework.boot.autoconfigure.ComponentScanDetectorConfiguration;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.City;
import org.springframework.boot.autoconfigure.data.couchbase.CityRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link CouchbaseRepositoriesAutoConfiguration}.
 *
 * @author Michael Nitschinger
 */
public class CouchbaseRepositoriesAutoConfigurationTests {

	private AnnotationConfigApplicationContext context;

	@Test
	public void testDefaultRepositoryConfiguration() throws Exception {
		this.context = new AnnotationConfigApplicationContext();
		this.context.register(TestConfiguration.class,
			ComponentScanDetectorConfiguration.class,
			CouchbaseRepositoriesAutoConfiguration.class,
			PropertyPlaceholderAutoConfiguration.class);
		this.context.refresh();
		assertNotNull(this.context.getBean(CityRepository.class));
		assertNotNull(this.context.getBean(CouchbaseClient.class));
	}

	@Test
	public void testNoRepositoryConfiguration() throws Exception {
		this.context = new AnnotationConfigApplicationContext();
		this.context.register(EmptyConfiguration.class,
			ComponentScanDetectorConfiguration.class,
			CouchbaseRepositoriesAutoConfiguration.class,
			PropertyPlaceholderAutoConfiguration.class);
		this.context.refresh();
		assertNotNull(this.context.getBean(CouchbaseClient.class));
	}

	@Configuration
	@ComponentScan (basePackageClasses = City.class)
	protected static class TestConfiguration {
		@Bean
		public CouchbaseClient clientMock() {
			return mock(CouchbaseClient.class);
		}
	}

	@Configuration
	protected static class EmptyConfiguration {
		@Bean
		public CouchbaseClient clientMock() {
			return mock(CouchbaseClient.class);
		}
	}

}
