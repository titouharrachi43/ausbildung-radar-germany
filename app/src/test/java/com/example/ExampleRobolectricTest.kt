package com.example

import com.example.data.model.AusbildungCategory
import com.example.data.model.TrainingType
import com.example.data.model.VerificationStatus
import com.example.data.repository.AusbildungRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `verify Ausbildung repository data models`() {
        val repo = AusbildungRepository()
        val list = repo.opportunities.value
        assertTrue(list.isNotEmpty())

        val dualCount = list.count { it.trainingType == TrainingType.DUALE_AUSBILDUNG }
        val piaCount = list.count { it.trainingType == TrainingType.PIA }
        assertTrue("Contains Duale Ausbildung", dualCount > 0)
        assertTrue("Contains PiA Erzieher Ausbildung", piaCount > 0)

        val confirmed = list.filter { it.verificationStatus == VerificationStatus.CONFIRMED_ACTIVE }
        assertTrue("Contains confirmed active opportunities", confirmed.isNotEmpty())
    }
}
