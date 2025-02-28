package idv.hsu.authenticator.presentation.screen.totplist.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.presentation.screen.totplist.components.TotpListItem
import idv.hsu.authenticator.ui.theme.colorNV500

@Composable
fun TotpListPage(data: Map<String, List<TotpDataItem>>, modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        data.forEach { (issuer, listOfData) ->
            item(key = issuer) {
                Text(
                    text = issuer,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight(400),
                    fontSize = 16.sp,
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        colorNV500
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(listOfData, key = { "${it.issuer}_${it.accountName}_${it.secret}" }) { item ->
                TotpListItem(item = item)
            }
        }

        item {
            val fabSize = 56.dp  // 標準 FAB 尺寸
            val fabMargin = 16.dp  // Material Design 建議的 margin
            val totalFabHeight = fabSize + (fabMargin * 2)
            Spacer(modifier = Modifier.height(totalFabHeight))
        }
    }
}