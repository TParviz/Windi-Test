@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package ru.winditest.presentation.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.winditest.R
import ru.winditest.core.countries.CountryData
import ru.winditest.core.countries.countryNames
import ru.winditest.core.countries.getLibCountries
import ru.winditest.presentation.navigation.LocalNavController
import ru.winditest.presentation.viewmodel.AuthViewModel

@Composable
fun CountrySelectionBottomSheet(
    viewModel: AuthViewModel
) {
    val navController = LocalNavController.current

    CountrySelectionBottomSheetContent(
        selectedCountry = viewModel.authenticationState.selectedCountry,
        onCountrySelected = {
            viewModel.onEvent(AuthenticationEvent.OnCountrySelected(it))
            navController.popBackStack()
        }
    )
}

@Composable
private fun CountrySelectionBottomSheetContent(
    selectedCountry: CountryData,
    onCountrySelected: (CountryData) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val countries = remember(searchQuery) {
        if (searchQuery.isEmpty()) getLibCountries
        else getLibCountries.filter {
            searchQuery.trim() in it.countryPhoneCode ||
                    searchQuery.trim() in (countryNames[it.countryCode]?.let {
                        context.getString(it)
                    } ?: "")
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Text(
                text = stringResource(R.string.select_country),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        stickyHeader {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(stringResource(R.string.search))
                    },
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    singleLine = true
                )
            }
        }

        items(
            items = countries,
            key = { it.countryCode }
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onCountrySelected(it)
                    }
                    .animateItemPlacement(),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = {
                    Text(
                        text = it.countryEmoji,
                        fontSize = 32.sp
                    )
                },
                headlineText = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        countryNames[it.countryCode]?.let {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(it),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (it == selectedCountry) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                },
                supportingText = {
                    Text(
                        text = "(${it.countryPhoneCode})"
                    )
                }
            )
        }
    }
}