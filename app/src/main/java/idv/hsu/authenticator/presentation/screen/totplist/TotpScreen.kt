package idv.hsu.authenticator.presentation.screen.totplist

import android.widget.Toast
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import idv.hsu.authenticator.R
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.presentation.utils.generateTOTP
import idv.hsu.authenticator.presentation.viewmodel.TotpUiState
import idv.hsu.authenticator.presentation.viewmodel.TotpViewModel
import idv.hsu.authenticator.presentation.widget.PasscodeCountdownProgress
import idv.hsu.authenticator.ui.theme.accountNameColor
import idv.hsu.authenticator.ui.theme.accountTypeColor
import idv.hsu.authenticator.ui.theme.colorBlack
import idv.hsu.authenticator.ui.theme.colorNV100
import idv.hsu.authenticator.ui.theme.colorNV800
import idv.hsu.authenticator.ui.theme.colorNV900
import idv.hsu.authenticator.ui.theme.colorP400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotpScreen(
    modifier: Modifier = Modifier,
    onUpdateTopAppBar: (String, @Composable (() -> Unit)?) -> Unit,
) {
    val viewModel: TotpViewModel = viewModel<TotpViewModel>()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value
    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
//    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        when (uiState) {
            is TotpUiState.Idle -> Unit
            is TotpUiState.Loading -> Unit
            is TotpUiState.SaveTOTPAccountFailed -> Unit
            is TotpUiState.ShowTOTPAccounts -> {
                if (uiState.accounts.isNotEmpty()) {
                    TotpList(
                        items = uiState.accounts.map { account ->
                            TotpDataItem(
                                account.id,
                                account.accountName,
                                generateTOTP(account.secret, System.currentTimeMillis() / 1000),
                                account.issuer,
                                remainingTime
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Data", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            is TotpUiState.SaveTOTPAccountSuccess,
            is TotpUiState.DeleteTOTPAccountFailed,
            is TotpUiState.DeleteTOTPAccountSuccess -> Unit
        }
    }
}

@Composable
fun TotpList(items: List<TotpDataItem>, modifier: Modifier = Modifier) {
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            TotpListItem(
                item = item,
                isShowingPasscode = (expandedItemId == item.id.toString()),
                onPasscodeClick = {
                    expandedItemId =
                        if (expandedItemId == item.id.toString()) null else item.id.toString()
                }
            )
        }

//        item {
//            Spacer(modifier = Modifier.height(45.dp + 62.dp))
//        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun TotpListItem(
    item: TotpDataItem,
    isShowingPasscode: Boolean,
    onPasscodeClick: () -> Unit
) {
//    var isShowingPasscode by remember { mutableStateOf(false) }
    val transition =
        updateTransition(targetState = isShowingPasscode, label = "Passcode Transition")
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
            .height(100.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        MotionLayout(
            motionScene = MotionScene(content = motionScene),
            progress = if (isShowingPasscode) 1f else 0f,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (isShowingPasscode) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_back_16),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .layoutId("imageArrowBack")
                        .clickable { onPasscodeClick() },
                    colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurfaceVariant else colorBlack)
                )
            }

            if (!isShowingPasscode) {
                Image(
                    painter = painterResource(id = item.getIssuerIcon()),
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
                isShowingPasscode = isShowingPasscode,
                onPasscodeClick = { onPasscodeClick() },
                modifier = Modifier
                    .layoutId("passCodeColumn")
            )
        }
    }
}

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

@Composable
fun PasscodeBlock(
    item: TotpDataItem,
    isShowingPasscode: Boolean,
    onPasscodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = item.remainingTime / 30f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ConstraintLayout {
            val (textGetPasscode, textPasscode, progressBar) = createRefs()

            if (!isShowingPasscode) {
                OutlinedButton(
                    onClick = { onPasscodeClick() },
                    border = BorderStroke(
                        1.dp,
                        if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else colorNV900
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            colorNV900
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .constrainAs(textGetPasscode) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 10.dp,
                        end = 12.dp,
                        bottom = 10.dp
                    ),
                ) {
                    Text(text = stringResource(R.string.get_passcode), fontSize = 14.sp)
                }
            }

            val formattedText = item.secret.let {
                if (it.length >= 4) {
                    it.substring(0, 3) + "\u0020" + it.substring(3) // 在第3和第4個字中間插入半形空白
                } else {
                    it // 長度不足4時，保持原樣
                }
            }

            if (isShowingPasscode) {
                Text(
                    text = formattedText,
                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else colorNV800,
                    fontSize = 34.sp,
                    fontWeight = FontWeight(700),
                    modifier = Modifier
                        .constrainAs(textPasscode) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(progressBar.top)
                        }
                )

                PasscodeCountdownProgress(
                    progress = progress,
                    modifier = Modifier
                        .constrainAs(progressBar) {
                            start.linkTo(textPasscode.start)
                            end.linkTo(textPasscode.end)
                            top.linkTo(textPasscode.bottom)
                            width = Dimension.fillToConstraints
                        }
                        .padding(top = 4.dp),
                    trackColor = colorNV100,
                    indicatorColor = colorP400,
                )
            }
        }

        if (isShowingPasscode) {
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current

            IconButton(onClick = {
                clipboardManager.setText(AnnotatedString(item.secret))
                Toast.makeText(context, R.string.passcode_copied, Toast.LENGTH_SHORT).show()
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_copy_24),
                    contentDescription = "Copy Icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PasscodeBlockPreview() {
    PasscodeBlock(
        item = TotpDataItem(
            1,
            "Account Name",
            "Secret",
            "Issuer",
            30L
        ),
        isShowingPasscode = true,
        onPasscodeClick = {}
    )
}

@Preview
@Composable
fun TotpListItemPreview() {
    TotpListItem(
        item = TotpDataItem(
            1,
            "Account Name",
            "Secret",
            "Issuer",
            30L
        ),
        isShowingPasscode = true,
        onPasscodeClick = {}
    )
}