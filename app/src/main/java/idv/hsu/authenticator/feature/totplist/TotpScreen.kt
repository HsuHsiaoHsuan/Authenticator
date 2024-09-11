package idv.hsu.authenticator.feature.totplist

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import idv.hsu.authenticator.R
import idv.hsu.authenticator.model.TotpDataItem
import kotlinx.coroutines.delay

@Composable
fun TotpScreen(
    onFabClick: () -> Unit
) {
    val viewModel: TotpViewModel = viewModel()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value

    val (remainingTime, setRemainingTime) = remember { mutableLongStateOf(30L) }
    val (progress, setProgress) = remember { mutableLongStateOf(1L) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
        while (true) {
            val timeRemaining = 30L - ((System.currentTimeMillis() / 1000) % 30L)
            setRemainingTime(timeRemaining)
            delay(1000L)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Authenticator") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
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
                            account.secret,
                            account.issuer,
                            remainingTime
                        )
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is TotpUiState.SaveTOTPAccountSuccess,
            is TotpUiState.DeleteTOTPAccountFailed,
            is TotpUiState.DeleteTOTPAccountSuccess -> {
                viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
            }
        }
    }
}

@Composable
fun TotpList(items: List<TotpDataItem>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(items.size) { index ->
            TotpListItem(item = items[index])
        }
    }
}

@Composable
fun TotpListItem(item: TotpDataItem) {
    val progress = item.remainingTime / 30f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)

        ) {
            val (imageLogo, textIssuer, textAccountName, textPasscode, progressBar, imageCopy) = createRefs()
            val verticalChainOne =
                createVerticalChain(textIssuer, textAccountName, chainStyle = ChainStyle.Packed)
            val verticalChainTwo =
                createVerticalChain(textPasscode, progressBar, chainStyle = ChainStyle.Packed)

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .constrainAs(imageLogo) {
                        start.linkTo(parent.start, margin = 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                contentScale = ContentScale.Crop
            )

            Text(
                text = item.issuer ?: "",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.constrainAs(textIssuer) {
                    start.linkTo(imageLogo.end, margin = 8.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(textAccountName.top)
                }
            )

            Text(
                text = item.accountName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.constrainAs(textAccountName) {
                    start.linkTo(textIssuer.start)
                    top.linkTo(textIssuer.bottom)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(textPasscode.start)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = "123 123",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.constrainAs(textPasscode) {
                    end.linkTo(imageCopy.start, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(progressBar.top)
                }
            )

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .constrainAs(progressBar) {
                        start.linkTo(textPasscode.start)
                        end.linkTo(textPasscode.end)
                        top.linkTo(textPasscode.bottom)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .clip(RoundedCornerShape(16.dp)),
                strokeCap = StrokeCap.Round
            )

            Image(
                painter = painterResource(id = R.drawable.ic_copy_24),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(imageCopy) {
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable { /*onCopyClick()*/ }
            )
        }
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = item.accountName, style = MaterialTheme.typography.h6)
//            item.issuer?.let {
//                Text(text = it, style = MaterialTheme.typography.body2)
//            }
//            Text(
//                text = "Remaining Time: ${item.remainingTime}",
//                style = MaterialTheme.typography.body2
//            )
//        }
    }
}

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = Color.Blue,
    backgroundColor: Color = Color.LightGray,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(3.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
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