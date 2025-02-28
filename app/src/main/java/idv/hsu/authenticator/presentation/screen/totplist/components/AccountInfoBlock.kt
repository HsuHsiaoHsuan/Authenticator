package idv.hsu.authenticator.presentation.screen.totplist.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.ui.theme.accountNameColor
import idv.hsu.authenticator.ui.theme.accountTypeColor

@Composable
fun AccountInfoBlock(
    item: TotpDataItem,
    sizeOfIssuer: Dp,
    sizeOfAccountName: Dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item.issuer ?: "",
            fontSize = sizeOfIssuer.value.sp,
            lineHeight = sizeOfIssuer.value.sp,
            fontWeight = FontWeight(590),
            color = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                accountTypeColor
            }
        )

        Text(
            text = item.accountName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sizeOfAccountName.value.sp,
            lineHeight = sizeOfAccountName.value.sp,
            fontWeight = FontWeight(590),
            color = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                accountNameColor
            },
            modifier = modifier.padding(end = 20.dp)
        )
    }
}