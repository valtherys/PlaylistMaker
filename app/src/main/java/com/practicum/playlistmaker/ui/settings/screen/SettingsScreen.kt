package com.practicum.playlistmaker.ui.settings.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.settings.view_model.UserSettingsViewModel
import com.practicum.playlistmaker.ui.theme.Blue
import com.practicum.playlistmaker.ui.theme.BlueLight

@Composable
fun SettingsScreen(
    viewModel: UserSettingsViewModel,
   
) {
    val isThemeDark by viewModel.isThemeDark.collectAsState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .fillMaxSize(),
    ) {
        Text(
            text = stringResource(R.string.settings),
            modifier = Modifier.height(56.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(
            modifier = Modifier
                .height(24.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Setting(
                text = R.string.dark_theme,
                endItem = {
                    Switcher(
                        isChecked = isThemeDark,
                        onSwitch = viewModel::onSwitchTheme
                    )
                }
            )
            Setting(
                text = R.string.share_app,
                endItem = { IconItem(R.drawable.ic_share_24) },
                onClickAction = { viewModel.onShareClicked() }
            )
            Setting(
                text = R.string.support,
                endItem = { IconItem(R.drawable.ic_support_24) },
                onClickAction = { viewModel.onSupportClicked() }
            )
            Setting(
                text = R.string.agreement,
                endItem = { IconItem(R.drawable.ic_forward_arrow_24) },
                onClickAction = { viewModel.onAgreementClicked() }
            )
        }
    }
}

@Composable
fun Setting(
    @StringRes text: Int,
    endItem: @Composable () -> Unit,
    onClickAction: (() -> Unit)? = null
) {
    @Composable
    fun Modifier.noRipple(onClick: () -> Unit) = clickable(
        onClick = onClick,
        indication = null,
        interactionSource = remember { MutableInteractionSource() })

    val modifier = if (onClickAction != null) {
        Modifier.noRipple { onClickAction() }
    } else {
        Modifier
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(61.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = stringResource(text),
            modifier = Modifier.weight(1F),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        endItem()
    }
}


@Composable
fun Switcher(
    isChecked: Boolean,
    onSwitch: (Boolean) -> Unit
) {
    Switch(
        checked = isChecked, onCheckedChange = { onSwitch(it) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Blue,
            checkedTrackColor = BlueLight,
        )
    )
}

@Composable
fun IconItem(@DrawableRes icon: Int) {
    Icon(
        modifier = Modifier.size(24.dp),
        contentDescription = null,
        painter = painterResource(icon),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
