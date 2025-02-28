package idv.hsu.authenticator.presentation.screen.totplist.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import idv.hsu.authenticator.R
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.presentation.widget.PasscodeCountdownProgress
import idv.hsu.authenticator.ui.theme.colorNV100
import idv.hsu.authenticator.ui.theme.colorNV800
import idv.hsu.authenticator.ui.theme.colorNV900
import idv.hsu.authenticator.ui.theme.colorP400

@Composable
fun PasscodeBlock(
    item: TotpDataItem,
    isShowingPasscode: Boolean,
    onGetPasscodeClick: () -> Unit,
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
                    onClick = { onGetPasscodeClick() },
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 10.dp,
                        end = 12.dp,
                        bottom = 10.dp
                    ),
                ) {
                    Text(text = stringResource(R.string.totp_get_passcode), fontSize = 14.sp)
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
                Toast.makeText(context, R.string.totp_passcode_copied, Toast.LENGTH_SHORT).show()
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