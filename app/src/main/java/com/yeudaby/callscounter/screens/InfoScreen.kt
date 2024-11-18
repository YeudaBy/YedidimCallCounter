package com.yeudaby.callscounter.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yeudaby.callscounter.R

@Composable
fun InfoScreen(back: () -> Boolean) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { back() },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.extraLarge
                )
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(12.dp)
        ) {

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 2.5.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(id = R.string.app_description),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Justify
                )
            )

            HorizontalDivider()

            Text(
                text = stringResource(id = R.string.app_author),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(
                    id = R.string.app_version,
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.align(Alignment.End),
            ) {
                OutlinedButton(onClick = { context.openUrl(R.string.latest_releases_url) }) {
                    Text(
                        text = stringResource(id = R.string.latest_releases),
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(onClick = { context.openUrl(R.string.app_author_site_address) }) {
                    Text(
                        text = stringResource(id = R.string.app_author_site),
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(onClick = { context.openUrl(R.string.app_github_address) }) {
                    Text(
                        text = stringResource(id = R.string.app_github),
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(onClick = { context.openUrl(R.string.privacy_policy_url) }) {
                    Text(
                        text = stringResource(id = R.string.privacy_policy),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


private fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = android.net.Uri.parse(url)
    startActivity(intent)
}

private fun Context.openUrl(resId: Int) {
    openUrl(getString(resId))
}

private fun Context.openEmail(email: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "plain/text"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    startActivity(Intent.createChooser(intent, ""))
}

private fun Context.openEmail(resId: Int) {
    openEmail(getString(resId))
}