package com.example.kalanacommerce.core.util

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.connectToAutofill(
    autofillTypes: List<AutofillType>,
    onFill: (String) -> Unit,
): Modifier = composed {
    val autofill = LocalAutofill.current
    val autofillTree = LocalAutofillTree.current

    // 1. Update state callback agar selalu fresh
    val currentOnFill by rememberUpdatedState(onFill)

    // 2. Buat Node HANYA SEKALI
    val autofillNode = remember {
        AutofillNode(
            onFill = { currentOnFill(it) },
            autofillTypes = autofillTypes
        )
    }

    // 3. Pasang Node ke Tree, dan HAPUS saat composable hilang (DisposableEffect)
    // Ini mencegah penumpukan node sampah yang bikin autofill macet
    DisposableEffect(autofillNode) {
        autofillTree += autofillNode
        onDispose {
            autofillTree.children.remove(autofillNode.id)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
            // * Udah dikasih logic autofill masih nggak mau muncul box paddingnya
            if (autofillNode.boundingBox != null) {
                // ! udah dikasih akses ke google keyboard masih nggak mau
                // ! udah dipancing pancing pakek bounding box tetep nggak jalan
                // ! INTINYA SAYA LELAHHHH
                autofill?.requestAutofillForNode(autofillNode)
            }

        }
        .onFocusChanged { focusState ->
            autofill?.apply {
                if (focusState.isFocused) {
                    Log.d("AutofillDebug", "Requesting autofill for Node ID: ${autofillNode.id}")
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
        // Force trigger saat diklik
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                Log.d("AutofillDebug", "Force Click Request for Node ID: ${autofillNode.id}")
                autofill?.requestAutofillForNode(autofillNode)
            }
        )
}