package com.example

import com.example.data.model.AusbildungCategory
import com.example.data.repository.AusbildungRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `basic math test`() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `repository loads German marketing and education opportunities`() {
        val repo = AusbildungRepository()
        val opps = repo.opportunities.value
        assertTrue("Opportunities list must not be empty", opps.isNotEmpty())

        val marketingCount = opps.count { it.category == AusbildungCategory.MARKETING }
        val educationCount = opps.count { it.category == AusbildungCategory.EDUCATION }
        assertTrue("Must include Marketing opportunities", marketingCount > 0)
        assertTrue("Must include Education opportunities", educationCount > 0)

        // Verify CSV generation
        val csv = repo.generateApplicationsCsv()
        assertNotNull(csv)
        assertTrue("CSV header must contain Position and Company", csv.contains("Position,Company"))
    }
}
