package idv.hsu.authenticator.presentation.screen.totplist

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Card
//import androidx.compose.material.FloatingActionButton
//import androidx.compose.material.Icon
//import androidx.compose.material.LinearProgressIndicator
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotpScreen(
    onFabClick: () -> Unit
) {
    val viewModel: TotpViewModel = viewModel<TotpViewModel>()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value
    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (topAppBar, fab, content) = createRefs()

        TopAppBar(
            title = { Text(text = "Authenticator") },
            modifier = Modifier.constrainAs(topAppBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        when (uiState) {
            is TotpUiState.Idle -> Unit
            is TotpUiState.Loading -> Unit // TODO
            is TotpUiState.SaveTOTPAccountFailed -> TODO()
            is TotpUiState.ShowTOTPAccounts -> {
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
                    modifier = Modifier.constrainAs(content) {
                        top.linkTo(topAppBar.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }
                )
            }

            is TotpUiState.SaveTOTPAccountSuccess,
            is TotpUiState.DeleteTOTPAccountFailed,
            is TotpUiState.DeleteTOTPAccountSuccess -> {
                viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            contentColor = colorResource(R.color.white_59),
            modifier = Modifier
                .size(62.dp)
                .constrainAs(fab) {
                    end.linkTo(parent.end, margin = 40.dp)
                    bottom.linkTo(parent.bottom, margin = 45.dp)
                }
                .background(color = colorResource(R.color.white_59))
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun TotpList(items: List<TotpDataItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        items(items, key = { it.id }) { item ->
            TotpListItem(item = item)
        }

        item {
            Spacer(modifier = Modifier.height(45.dp + 62.dp))
        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun TotpListItem(item: TotpDataItem) {
    var isShowingPasscode by remember { mutableStateOf(false) }
    val transition =
        updateTransition(targetState = isShowingPasscode, label = "Passcode Transition")
    val sizeOfIssuer by transition.animateDp(
        label = "Issuer Text Size",
    ) { state ->
        if (state) 12.dp else 20.dp
    }
    val sizeOfAccountName by transition.animateDp(
        label = "Account Name Text Size",
    ) { state ->
        if (state) 10.dp else 12.dp
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
            .height(84.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        MotionLayout(
            motionScene = MotionScene(content = motionScene),
            progress = if (isShowingPasscode) 1f else 0f,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (isShowingPasscode) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_back_16),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .layoutId("imageArrowBack")
                        .clickable { isShowingPasscode = false }
                )
            }

            if (!isShowingPasscode) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
                onPasscodeClick = { isShowingPasscode = !isShowingPasscode },
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
            fontWeight = FontWeight(590)
        )

        Text(
            text = item.accountName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sizeOfAccountName.value.sp,
            fontWeight = FontWeight(590),
            color = colorResource(id = R.color.totp_account)
        )
    }
}

@Composable
fun PasscodeBlock(
    item: TotpDataItem,
    onPasscodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = item.remainingTime / 30f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ConstraintLayout {
            val (textPasscode, progressBar) = createRefs()

            Text(
                text = item.secret,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .constrainAs(textPasscode) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(progressBar.top)
                    }
                    .clickable { onPasscodeClick() }
            )

            LinearProgressIndicator(
                progress = { progress },
                color = colorResource(id = R.color.colorPrimary400),
                modifier = Modifier
                    .constrainAs(progressBar) {
                        start.linkTo(textPasscode.start)
                        end.linkTo(textPasscode.end)
                        top.linkTo(textPasscode.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .clip(RoundedCornerShape(16.dp)),
                strokeCap = StrokeCap.Round
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_copy_24),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
                .clickable { /*onCopyClick()*/ }
        )
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
        )
    )
}