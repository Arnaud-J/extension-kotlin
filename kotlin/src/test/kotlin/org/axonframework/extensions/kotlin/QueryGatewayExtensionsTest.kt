/*
 * Copyright (c) 2010-2020. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.extensions.kotlin

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.axonframework.messaging.responsetypes.AbstractResponseType
import org.axonframework.messaging.responsetypes.InstanceResponseType
import org.axonframework.queryhandling.QueryGateway
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Tests Query Gateway extensions.
 *
 * @author Stefan Andjelkovic
 */
internal class QueryGatewayExtensionsTest {

    private val queryName = ExampleQuery::class.qualifiedName.toString()
    private val exampleQuery = ExampleQuery(2)
    private val instanceReturnValue: CompletableFuture<String> = CompletableFuture.completedFuture("2")
    private val optionalReturnValue: CompletableFuture<Optional<String>> = CompletableFuture.completedFuture(Optional.of("Value"))
    private val listReturnValue: CompletableFuture<List<String>> = CompletableFuture.completedFuture(listOf("Value", "Second value"))
    private val subjectGateway = mockk<QueryGateway>()

    @BeforeTest
    fun before() {
        every { subjectGateway.query(exampleQuery, matchInstanceResponseType<String>()) } returns instanceReturnValue
        every { subjectGateway.query(exampleQuery, matchOptionalResponseType<String>()) } returns optionalReturnValue
        every { subjectGateway.query(exampleQuery, matchMultipleInstancesResponseType<String>()) } returns listReturnValue
        every { subjectGateway.query(queryName, exampleQuery, matchInstanceResponseType<String>()) } returns instanceReturnValue
        every { subjectGateway.query(queryName, exampleQuery, matchOptionalResponseType<String>()) } returns optionalReturnValue
        every { subjectGateway.query(queryName, exampleQuery, matchMultipleInstancesResponseType<String>()) } returns listReturnValue
    }

    @AfterTest
    fun after() {
        clearMocks(subjectGateway)
    }

    @Test
    fun `Query without queryName for Single should invoke query method with correct generic parameters`() {
        val queryResult = subjectGateway.queryForSingle<String, ExampleQuery>(query = exampleQuery)
        assertSame(queryResult, instanceReturnValue)
        verify(exactly = 1) {
            subjectGateway.query(exampleQuery, matchExpectedResponseType(String::class.java))
        }
    }

    @Test
    fun `Query without queryName for Single should invoke query method and not require explicit generic types`() {
        val queryResult:CompletableFuture<String> = subjectGateway.queryForSingle(query = exampleQuery)
        assertSame(queryResult, instanceReturnValue)
        verify(exactly = 1) {
            subjectGateway.query(exampleQuery, matchExpectedResponseType(String::class.java))
        }
    }

    @Test
    fun `Query without queryName for Optional should invoke query method with correct generic parameters`() {
        val queryResult = subjectGateway.queryForOptional<String, ExampleQuery>(query = exampleQuery)

        assertSame(queryResult, optionalReturnValue)
        verify(exactly = 1) { subjectGateway.query(exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query without queryName for Optional should invoke query method and not require explicit generic types`() {
        val queryResult: CompletableFuture<Optional<String>> = subjectGateway.queryForOptional(query = exampleQuery)

        assertSame(queryResult, optionalReturnValue)
        verify(exactly = 1) { subjectGateway.query(exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query without queryName for Multiple should invoke query method with correct generic parameters`() {
        val queryResult = subjectGateway.queryForMultiple<String, ExampleQuery>(query = exampleQuery)

        assertSame(queryResult, listReturnValue)
        verify(exactly = 1) { subjectGateway.query(exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query without queryName for Multiple should invoke query method and not require explicit generic types`() {
        val queryResult: CompletableFuture<List<String>> = subjectGateway.queryForMultiple(query = exampleQuery)

        assertSame(queryResult, listReturnValue)
        verify(exactly = 1) { subjectGateway.query(exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query without queryName for Single should handle nullable responses`() {
        val nullInstanceReturnValue: CompletableFuture<String?> = CompletableFuture.completedFuture(null)
        val nullableQueryGateway = mockk<QueryGateway> {
            every { query(exampleQuery, match { i: AbstractResponseType<String?> -> i is InstanceResponseType }) } returns nullInstanceReturnValue
        }

        val queryResult = nullableQueryGateway.queryForSingle<String?, ExampleQuery>(query = exampleQuery)

        assertSame(queryResult, nullInstanceReturnValue)
        assertTrue(nullInstanceReturnValue.get() == null)
        verify(exactly = 1) { nullableQueryGateway.query(exampleQuery, matchExpectedResponseType(String::class.java)) }
    }


    @Test
    fun `Query for Single should invoke query method with correct generic parameters`() {
        val queryResult = subjectGateway.queryForSingle<String, ExampleQuery>(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, instanceReturnValue)
        verify(exactly = 1) { subjectGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query for Single should invoke query method and not require explicit generic types`() {
        val queryResult: CompletableFuture<String> = subjectGateway.queryForSingle(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, instanceReturnValue)
        verify(exactly = 1) { subjectGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query for Optional should invoke query method with correct generic parameters`() {
        val queryResult = subjectGateway.queryForOptional<String, ExampleQuery>(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, optionalReturnValue)
        verify(exactly = 1) { subjectGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query for Optional should invoke query method and not require explicit generic types`() {
        val queryResult: CompletableFuture<Optional<String>> = subjectGateway.queryForOptional(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, optionalReturnValue)
        verify(exactly = 1) { subjectGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query for Multiple should invoke query method with correct generic parameters`() {
        val queryResult = subjectGateway.queryForMultiple<String, ExampleQuery>(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, listReturnValue)
        verify(exactly = 1) { subjectGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query for Multiple should invoke query method and not require explicit generic types`() {
        val queryResult: CompletableFuture<List<String>> = subjectGateway.queryForMultiple(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, listReturnValue)
        verify(exactly = 1) { subjectGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

    @Test
    fun `Query for Single should handle nullable responses`() {
        val nullInstanceReturnValue: CompletableFuture<String?> = CompletableFuture.completedFuture(null)
        val nullableQueryGateway = mockk<QueryGateway> {
            every { query(queryName, exampleQuery, match { i: AbstractResponseType<String?> -> i is InstanceResponseType }) } returns nullInstanceReturnValue
        }

        val queryResult = nullableQueryGateway.queryForSingle<String?, ExampleQuery>(queryName = queryName, query = exampleQuery)

        assertSame(queryResult, nullInstanceReturnValue)
        assertTrue(nullInstanceReturnValue.get() == null)
        verify(exactly = 1) { nullableQueryGateway.query(queryName, exampleQuery, matchExpectedResponseType(String::class.java)) }
    }

}
