package idv.hsu.authenticator.presentation.screen.totplist.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import idv.hsu.authenticator.R
import idv.hsu.authenticator.TotpListRoute
import idv.hsu.authenticator.TutorialRoute
import idv.hsu.authenticator.ui.theme.colorNV500
import idv.hsu.authenticator.ui.theme.colorNV700
import idv.hsu.authenticator.ui.theme.colorNV900
import idv.hsu.authenticator.ui.theme.colorP300

@Composable
fun TotpEmptyPage(
    modifier: Modifier,
    navController: NavController,
    onStartNowAction: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background else Color.White)
    ) {
        val (titleText, subtitleText, image, startButton, howItWorkButton) = createRefs()

        Text(
            text = stringResource(R.string.totp_no_data_title),
            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else colorNV900,
            fontWeight = FontWeight(700),
            fontSize = 28.sp,
            lineHeight = 34.sp,
            modifier = Modifier
                .constrainAs(titleText) {
                    top.linkTo(anchor = parent.top, margin = 40.dp)
                    start.linkTo(anchor = parent.start, margin = 30.dp)
                    end.linkTo(anchor = parent.end, margin = 30.dp)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(R.string.totp_no_data_subtitle),
            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else colorNV500,
            fontWeight = FontWeight(590),
            fontSize = 18.sp,
            lineHeight = 22.sp,
            modifier = Modifier
                .constrainAs(subtitleText) {
                    top.linkTo(anchor = titleText.bottom, margin = 8.dp)
                    start.linkTo(anchor = titleText.start)
                    end.linkTo(anchor = titleText.end)
                    width = Dimension.fillToConstraints
                }
        )

        OutlinedButton(
            border = BorderStroke(
                1.4.dp,
                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else colorNV700
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .constrainAs(howItWorkButton) {
                    start.linkTo(anchor = parent.start, margin = 30.dp)
                    end.linkTo(anchor = parent.end, margin = 30.dp)
                    bottom.linkTo(anchor = parent.bottom, margin = 30.dp)
                    width = Dimension.fillToConstraints
                },
            onClick = {
                navController.navigate(TutorialRoute.route){
                    popUpTo(TotpListRoute.route) { inclusive = true }
                }
            },
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.totp_no_data_how_it_works),
                fontSize = 16.sp,
                fontWeight = FontWeight(590),
                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else colorNV900
            )
        }

        Button(
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorP300,
                contentColor = colorNV900,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            ),
            modifier = Modifier
                .constrainAs(startButton) {
                    start.linkTo(anchor = parent.start, margin = 30.dp)
                    end.linkTo(anchor = parent.end, margin = 30.dp)
                    bottom.linkTo(anchor = howItWorkButton.top, margin = 16.dp)
                    width = Dimension.fillToConstraints
                },
            onClick = { onStartNowAction.invoke() },
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.totp_no_data_start_now),
                fontSize = 16.sp,
                fontWeight = FontWeight(590)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.img_start_now),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(anchor = startButton.top, margin = 28.dp)
                    top.linkTo(subtitleText.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            contentScale = ContentScale.Crop,
        )
    }
}