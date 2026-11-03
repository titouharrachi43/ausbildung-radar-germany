package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.CompanyCategory
import com.example.ui.theme.MarketingAmber
import com.example.ui.theme.EducationEmerald
import com.example.ui.theme.RadarCyanGlow

@Composable
fun CompaniesScreen(
    companies: List<Company>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CompanyCategory?>(null) }

    val filteredCompanies = remember(companies, searchQuery, selectedCategory) {
        companies.filter { comp ->
            val matchesQuery = searchQuery.isBlank() ||
                    comp.name.contains(searchQuery, ignoreCase = true) ||
                    comp.city.contains(searchQuery, ignoreCase = true) ||
                    comp.bundesland.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == null || comp.category == selectedCategory
            matchesQuery && matchesCat
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("companies_screen")
    ) {
        // Search & Category filters
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search discovered company or city...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("companies_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All (${companies.size})") }
                )
                CompanyCategory.values().forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = if (selectedCategory == cat) null else cat
                        },
                        label = { Text(cat.germanLabel, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        // Total count & Status Label
        Text(
            text = "Potential Employers: ${filteredCompanies.size} (Demo Seed Registry • Places API: NOT CONFIGURED)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCompanies, key = { it.id }) { company ->
                val isMarketing = company.category in listOf(
                    CompanyCategory.MARKETING_AGENCY,
                    CompanyCategory.WERBEAGENTUR,
                    CompanyCategory.DIGITAL_AGENCY,
                    CompanyCategory.SOCIAL_MEDIA_AGENCY,
                    CompanyCategory.E_COMMERCE,
                    CompanyCategory.MEDIENUNTERNEHMEN,
                    CompanyCategory.PR_AGENCY
                )
                val badgeColor = if (isMarketing) MarketingAmber else EducationEmerald

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("company_item_${company.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = badgeColor.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = company.category.germanLabel,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = badgeColor)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RadarCyanGlow.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${company.verifiedOpportunitiesCount} verified Ausbildung",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = RadarCyanGlow, fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = company.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                            Text(
                                text = "${company.city}, ${company.bundesland}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Place ID attribution
                        Text(
                            text = "Potential Employer • Seed Place ID: ${company.placeId}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action links: Website & Career Page
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(company.websiteUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Website", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(company.careerUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Career Page", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
