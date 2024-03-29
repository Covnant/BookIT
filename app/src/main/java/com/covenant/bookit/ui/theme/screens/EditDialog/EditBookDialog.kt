package com.covenant.bookit.ui.theme.screens.EditDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.covenant.bookit.ui.theme.Components.DatePickerAlertDialog
import com.covenant.bookit.ui.theme.screens.EditDialog.EditBookDialogState
import com.covenant.bookit.ui.theme.screens.EditDialog.EditBookDialogStateChangeListener
import com.thebrownfoxx.components.FilledButton
import com.thebrownfoxx.components.IconButton
import com.thebrownfoxx.components.TextButton

@Composable
fun EditBookDialog(
    state: EditBookDialogState,
    stateChangeListener: EditBookDialogStateChangeListener,
    modifier: Modifier = Modifier,
) {
    if(state is EditBookDialogState.Visible){
        AlertDialog(
            modifier = modifier,
            onDismissRequest = stateChangeListener.onHideEditBook,
            title = { Text(text = "Edit Book") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextField(
                        label = { Text(text = "Title") },
                        value = state.title,
                        singleLine = true,
                        onValueChange = stateChangeListener.onTitleChange,
                        isError = state.hasTitleWarning,
                        trailingIcon = {
                            if (state.hasTitleWarning)
                                Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                        },
                    )

                    TextField(
                        label = { Text(text = "Author") },
                        modifier = Modifier.wrapContentWidth(),
                        singleLine = true,
                        value = state.author,
                        onValueChange = stateChangeListener.onAuthorChange,
                        isError = state.hasAuthorWarning,
                        trailingIcon = {
                            if (state.hasAuthorWarning)
                                Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                        },
                    )

                    TextField(
                        label = { Text(text = "Number of Pages") },
                        modifier = Modifier.wrapContentWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = state.pages?.toString()?: "",
                        onValueChange = stateChangeListener.onPagesChange,
                        isError = state.hasPagesWarning,
                        trailingIcon = {
                            if (state.hasPagesWarning)
                                Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                        },
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextField(
                            label = { Text(text = "Date Published") },
                            modifier = Modifier.wrapContentWidth()
                                .weight(2f),
                            readOnly = true,
                            value = state.publishedDate.toString(),
                            onValueChange = stateChangeListener.onPublishDateChange,
                        )
                        IconButton(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date Picker",
                            onClick = stateChangeListener.onShowDatePicker,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    text = "Cancel",
                    onClick = stateChangeListener.onHideEditBook,
                )
            },
            confirmButton = {
                FilledButton(
                    text = "Update",
                    onClick = stateChangeListener.onUpdate,
                )
            }
        )

        DatePickerAlertDialog(
            visible = state.datePickerState,
            onDismissRequest =  stateChangeListener.onHideDatePicker ,
            onConfirm = {stateChangeListener.onPublishDateChange(it.toString())}
        )
    }
}