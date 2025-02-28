package idv.hsu.authenticator.presentation.screen.totplist.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import idv.hsu.authenticator.R
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.ui.theme.colorBlack

@OptIn(ExperimentalMotionApi::class)
@Composable
fun TotpListItem(
    item: TotpDataItem
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    fun toggleExpansion() {
        isExpanded = !isExpanded
    }

    val transition =
        updateTransition(targetState = isExpanded, label = "Passcode Transition")
    val sizeOfIssuer by transition.animateDp(
        label = "Issuer Text Size",
    ) { state ->
        if (state) 14.dp else 20.dp
    }
    val sizeOfAccountName by transition.animateDp(
        label = "Account Name Text Size",
    ) { state ->
        if (state) 12.dp else 14.dp
    }

    val context = LocalContext.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.totp_list_item_motion_scene)
            .readBytes()
            .decodeToString()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        MotionLayout(
            motionScene = MotionScene(content = motionScene),
            progress = if (isExpanded) 1f else 0f,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (isExpanded) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_back_16),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .layoutId("imageArrowBack")
                        .clickable { toggleExpansion() },
                    colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurfaceVariant else colorBlack)
                )
            }

            if (!isExpanded) {
                Image(
                    painter = painterResource(id = item.getIssuerIcon(isSystemInDarkTheme())),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .layoutId("imageLogo"),
                    contentScale = ContentScale.Crop
                )
            }

            AccountInfoBlock(
                item = item,
                sizeOfIssuer = sizeOfIssuer,
                sizeOfAccountName = sizeOfAccountName,
                modifier = Modifier
                    .layoutId("accountColumn")
            )

            PasscodeBlock(
                item = item,
                isShowingPasscode = isExpanded,
                onGetPasscodeClick = { toggleExpansion() },
                modifier = Modifier
                    .layoutId("passCodeColumn")
            )
        }
    }
}