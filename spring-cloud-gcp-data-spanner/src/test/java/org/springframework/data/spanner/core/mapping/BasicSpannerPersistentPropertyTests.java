/*
 *  Copyright 2018 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.data.spanner.core.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Chengyuan Zhao
 */
@RunWith(SpringRunner.class)
public class BasicSpannerPersistentPropertyTests {

	@Test
	public void testGetColumn() {
		BasicSpannerPersistentEntity<TestEntity> entity =
				(BasicSpannerPersistentEntity<TestEntity>) (new SpannerMappingContext()
				.getPersistentEntity(TestEntity.class));

		assertThat(entity.columns(), containsInAnyOrder("id", "custom_col", "other"));
	}

	@Test(expected = MappingException.class)
	public void testNullColumnName() {
		SpannerMappingContext context = new SpannerMappingContext();
		FieldNamingStrategy namingStrat = mock(FieldNamingStrategy.class);
		when(namingStrat.getFieldName(any())).thenReturn(null);
		context.setFieldNamingStrategy(namingStrat);
		BasicSpannerPersistentEntity<TestEntity> entity =
				(BasicSpannerPersistentEntity<TestEntity>) (context
				.getPersistentEntity(TestEntity.class));

		entity.columns().forEach(col -> {
			BasicSpannerPersistentProperty prop = (BasicSpannerPersistentProperty) entity
					.getPersistentPropertyByColumnName(col);

			// Getting the column name will throw an exception because of the mock naming
			// strategy.
			prop.getColumnName();
		});
	}

	@Test
	public void testAssociations() {
		BasicSpannerPersistentEntity<TestEntity> entity =
				(BasicSpannerPersistentEntity<TestEntity>) (new SpannerMappingContext()
				.getPersistentEntity(TestEntity.class));

		entity.columns().forEach(col -> {
			BasicSpannerPersistentProperty prop = (BasicSpannerPersistentProperty) entity
					.getPersistentPropertyByColumnName(col);
			assertSame(prop, prop.createAssociation().getInverse());
			assertNull(prop.createAssociation().getObverse());
		});
	}

	@Table(name = "custom_test_table")
	private static class TestEntity {
		@Id
		String id;

		@Column(name = "custom_col")
		String something;

		@Column(name = "")
		String other;
	}
}
