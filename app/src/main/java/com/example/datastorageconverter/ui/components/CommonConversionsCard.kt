package com.example.datastorageconverter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datastorageconverter.data.model.StorageSystem
import com.example.datastorageconverter.data.model.StorageUnit

data class CommonConversionItem(
    val title: String,
    val value: String,
    val unit: StorageUnit,
    val system: StorageSystem,
    val resultPreview: String
)

@Composable
fun CommonConversionsCard(
    onSelectConversion: (value: String, unit: StorageUnit, system: StorageSystem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        CommonConversionItem("1 GB", "1", StorageUnit.GIGABYTE, StorageSystem.DECIMAL, "1,000 MB"),
        CommonConversionItem("1 TB", "1", StorageUnit.TERABYTE, StorageSystem.DECIMAL, "1,000 GB"),
        CommonConversionItem("1 GiB", "1", StorageUnit.GIBIBYTE, StorageSystem.BINARY, "1,024 MiB"),
        CommonConversionItem("1 TiB", "1", StorageUnit.TEBIBYTE, StorageSystem.BINARY, "1,024 GiB")
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "COMMON CONVERSIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 2 rows of 2 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BenchmarkPill(
                        item = items[0],
                        onClick = { onSelectConversion(items[0].value, items[0].unit, items[0].system) },
                        modifier = Modifier.weight(1f)
                    )
                    BenchmarkPill(
                        item = items[1],
                        onClick = { onSelectConversion(items[1].value, items[1].unit, items[1].system) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BenchmarkPill(
                        item = items[2],
                        onClick = { onSelectConversion(items[2].value, items[2].unit, items[2].system) },
                        modifier = Modifier.weight(1f)
                    )
                    BenchmarkPill(
                        item = items[3],
                        onClick = { onSelectConversion(items[3].value, items[3].unit, items[3].system) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BenchmarkPill(
    item: CommonConversionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "Common conversion: ${item.title} to ${item.resultPreview}"
            }
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.resultPreview,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (item.system == StorageSystem.DECIMAL) "Decimal (SI)" else "Binary (IEC)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
