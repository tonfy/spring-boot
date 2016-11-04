/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.boot.actuate.health;

import java.util.List;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.util.features.Version;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.couchbase.client.java.query.Select.select;


/**
 * {@link HealthIndicator} for Couchbase.
 *
 * @author Eddú Meléndez
   kriskrishna
 * @since 1.4.0
 */
public class CouchBaseHealthIndicator extends AbstractHealthIndicator {

	private CouchbaseOperations couchbaseOperations;

	public CouchBaseHealthIndicator(CouchbaseOperations couchbaseOperations) {
		Assert.notNull(couchbaseOperations, "CouchbaseOperations must not be null");
		this.couchbaseOperations = couchbaseOperations;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		try {
			List<Version> versions = this.couchbaseOperations.getCouchbaseClusterInfo().getAllVersions();
			Statement statement = select("*").from("system:keyspaces");
			N1qlQuery q = N1qlQuery.simple(statement);
			N1qlQueryResult results = this.couchbaseOperations.queryN1QL(q);
			if (results.finalSuccess()) {
				builder.up().withDetail("versions", StringUtils.collectionToCommaDelimitedString(versions));
				return;
			}
		} catch (Exception ex) {
			builder.down(ex);
		}
	}

}
