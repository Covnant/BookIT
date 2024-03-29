package com.covenant.bookit.ui.theme.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covenant.bookit.Model.Books
import com.covenant.bookit.realm.RealmDatabase
import com.covenant.bookit.ui.theme.screens.AddBookDialog.AddBooksDialogState
import com.covenant.bookit.ui.theme.screens.Archives.DeleteDialogState
import com.covenant.bookit.ui.theme.screens.Archives.RestoreDialogState
import com.covenant.bookit.ui.theme.screens.EditDialog.EditBookDialogState
import com.covenant.bookit.ui.theme.screens.ViewBookDialog.ViewBookDialogState
import com.hamthelegend.enchantmentorder.extensions.combineToStateFlow
import com.hamthelegend.enchantmentorder.extensions.search
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

class BooksViewModel: ViewModel(){
    private val database = RealmDatabase()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.update { newQuery }
    }

    val favoriteBooks = combineToStateFlow(
        database.getFavoriteBooks(),
        searchQuery,
        scope = viewModelScope,
        initialValue = emptyList(),
    ){realmBooks, searchQuery ->
        realmBooks.map { realmBook ->
            Books(
                id = realmBook.id.toHexString(),
                author = realmBook.author!!,
                title = realmBook.title!!,
                pages = realmBook.pages,
                pagesRead = realmBook.pagesRead,
                publishDate = LocalDate.ofEpochDay(realmBook.publishDate!!),
                dateAdded = LocalDate.ofEpochDay(realmBook.dateAdded!!),
                dateModified = LocalDate.ofEpochDay(realmBook.dateModified!!),
                favorite = realmBook.favorite,
                archived = realmBook.archived,
            )
        }.search(searchQuery){it.title}
    }

    val books = combineToStateFlow(
        database.getAllBooks(),
        searchQuery,
        scope = viewModelScope,
        initialValue = emptyList(),
    ) { realmBooks, searchQuery ->
        realmBooks.map { realmBook ->
            Books(
                id = realmBook.id.toHexString(),
                author = realmBook.author!!,
                title = realmBook.title!!,
                pages = realmBook.pages,
                pagesRead = realmBook.pagesRead,
                publishDate = LocalDate.ofEpochDay(realmBook.publishDate!!),
                dateAdded = LocalDate.ofEpochDay(realmBook.dateAdded!!),
                dateModified = LocalDate.ofEpochDay(realmBook.dateModified!!),
                favorite = realmBook.favorite,
                archived = realmBook.archived,
            )
        }.search(searchQuery) {it.title}
    }

    val archivedBooks = combineToStateFlow(
        database.getArchivedBooks(),
        searchQuery,
        scope = viewModelScope,
        initialValue = emptyList(),
    ){realmArchivedBooks, searchQuery ->
        realmArchivedBooks.map { realmBook ->
            Books(
                id = realmBook.id.toHexString(),
                author = realmBook.author!!,
                title = realmBook.title!!,
                pages = realmBook.pages,
                pagesRead = realmBook.pagesRead,
                publishDate = LocalDate.ofEpochDay(realmBook.publishDate!!),
                dateAdded = LocalDate.ofEpochDay(realmBook.dateAdded!!),
                dateModified = LocalDate.ofEpochDay(realmBook.dateModified!!),
                favorite = realmBook.favorite,
                archived = realmBook.archived,
            )
        }.search(searchQuery){it.title}
    }


    private val _addBooksDialogState = MutableStateFlow<AddBooksDialogState>(AddBooksDialogState.Hidden)
    val addBooksDialogState = _addBooksDialogState.asStateFlow()

    private val _viewBookDialogState = MutableStateFlow<ViewBookDialogState>(ViewBookDialogState.Hidden)
    val viewBookDialogState = _viewBookDialogState.asStateFlow()

    private val _editBookDialogState = MutableStateFlow<EditBookDialogState>(EditBookDialogState.Hidden)
    val editBookDialogState = _editBookDialogState.asStateFlow()

    private val _deleteDialogsState = MutableStateFlow<DeleteDialogState>(DeleteDialogState.Hide)
    val deleteDialogState = _deleteDialogsState.asStateFlow()


    private val _restoreDialogsState = MutableStateFlow<RestoreDialogState>(RestoreDialogState.Hide)
    val restoreDialogState = _restoreDialogsState.asStateFlow()

    fun initiateDeleteAll(){
        _deleteDialogsState.update { DeleteDialogState.Visible }
    }

    fun hideDeleteAll(){
        _deleteDialogsState.update { DeleteDialogState.Hide }
    }

    fun initiateRestoreAll(){
        _restoreDialogsState.update { RestoreDialogState.Visible }
    }

    fun hideRestoreAll(){
        _restoreDialogsState.update { RestoreDialogState.Hide }
    }


    fun initiateEdit(book: Books){
        _editBookDialogState.update { EditBookDialogState.Visible(book) }
    }

    fun hideEdit(){
        _editBookDialogState.update { EditBookDialogState.Hidden }
    }

    fun hideAddBooksDialogState(){
        _addBooksDialogState.update { AddBooksDialogState.Hidden }
    }

    fun showAddBooksDialogState(){
        _addBooksDialogState.update { AddBooksDialogState.Visible() }
    }

    fun showDatePicker(){
        _addBooksDialogState.update {
            if(it is AddBooksDialogState.Visible){
                it.copy(
                    datePickerState = true
                )
            } else it
        }
    }

    fun hideDatePicker(){
        _addBooksDialogState.update {
            if(it is AddBooksDialogState.Visible){
                it.copy(
                    datePickerState = false
                )
            } else it
        }
    }

    fun updateTitle(title: String) {
        _addBooksDialogState.update {
            if (it is AddBooksDialogState.Visible) {
                it.copy(
                    title = title,
                    hasTitleWarning = false
                )
            } else it
        }
    }

    fun updateAuthor(author: String) {
        _addBooksDialogState.update {
            if (it is AddBooksDialogState.Visible) {
                it.copy(
                    author = author,
                    hasAuthorWarning = false
                )
            } else it
        }
    }

    fun updatePages(pages: String) {
        _addBooksDialogState.update {
            if (it is AddBooksDialogState.Visible) {
                val numberOfPages = when (pages) {
                    "" -> null
                    else -> pages.toIntOrNull() ?: it.pages
                }
                it.copy(
                    pages = numberOfPages,
                    hasPagesWarning = false,
                )
            } else it
        }
    }

    fun updateDatePublished(newDatePublished: String) {
        _addBooksDialogState.update {
            if (it is AddBooksDialogState.Visible) {
                val publishedDate = when (newDatePublished) {
                    "" -> LocalDate.now()
                    else -> LocalDate.parse(newDatePublished) ?: it.publishedDate
                }
                it.copy(
                    publishedDate = publishedDate!!,
                    hasPagesWarning = false,
                )
            } else it
        }
    }

    fun addBook(){
        var state = _addBooksDialogState.value
        with(state){
            if(this is AddBooksDialogState.Visible){
                if(author.isBlank() || title.isBlank() || publishedDate == null|| pages == null){
                    state = copy(
                        hasAuthorWarning = author.isBlank(),
                        hasTitleWarning = title.isBlank(),
                        hasPublishedDateWarning = publishedDate == null,
                        hasPagesWarning = pages == null,
                    )
                }
                else{
                    viewModelScope.launch {
                        database.addBook(
                            title = title,
                            author = author,
                            publishedDate = publishedDate,
                            pages = pages
                        )
                    }
                    state = AddBooksDialogState.Hidden
                }
                _addBooksDialogState.update { state }
            }
        }
    }

    fun deleteBook(book: Books){
        viewModelScope.launch {
            database.deleteBook(id = ObjectId(book.id))
        }
    }

    fun restoreAll(){
        viewModelScope.launch {
            database.restoreAll()
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            database.deleteAll()
        }
    }

    fun initiateViewBook(book:Books){
        _viewBookDialogState.update { ViewBookDialogState.Visible(book) }
    }

    fun hideViewBook(){
        _viewBookDialogState.update { ViewBookDialogState.Hidden }
    }


    fun showDatePickerOnViewBook(){
        _editBookDialogState.update {
            if(it is EditBookDialogState.Visible){
                it.copy(
                    datePickerState = true
                )
            } else it
        }
    }

    fun hideDatePickerOnViewBook(){
        _editBookDialogState.update {
            if(it is EditBookDialogState.Visible){
                it.copy(
                    datePickerState = false
                )
            } else it
        }
    }

    fun updateTitleOnViewBook(title: String) {
        _editBookDialogState.update {
            if (it is EditBookDialogState.Visible) {
                it.copy(
                    title = title,
                    hasTitleWarning = false
                )
            } else it
        }
    }

    fun updateAuthorOnViewBook(author: String) {
        _editBookDialogState.update {
            if (it is EditBookDialogState.Visible) {
                it.copy(
                    author = author,
                    hasAuthorWarning = false
                )
            } else it
        }
    }

    fun updatePagesOnViewBook(pages: String) {
        _editBookDialogState.update {
            if (it is EditBookDialogState.Visible) {
                val numberOfPages = when (pages) {
                    "" -> null
                    else -> pages.toIntOrNull() ?: it.pages
                }
                it.copy(
                    pages = numberOfPages,
                    hasPagesWarning = false,
                )
            } else it
        }
    }


    fun updatePagesReadOnViewBook(currentPage: String) {
        _viewBookDialogState.update {
            if (it is ViewBookDialogState.Visible) {
                val pagesRead = when (currentPage) {
                    "" -> 0
                    else -> currentPage.toIntOrNull() ?: it.pagesRead
                }
                it.copy(
                    pagesRead = pagesRead,
                    hasPagesWarning = false,
                )
            } else it
        }
    }

    fun updateDatePublishedOnEdit(newDatePublished: String) {
        _editBookDialogState.update {
            if (it is EditBookDialogState.Visible) {
                val publishedDate = when (newDatePublished) {
                    "" -> null
                    else -> LocalDate.parse(newDatePublished) ?: it.publishedDate
                }
                it.copy(
                    publishedDate = publishedDate!!,
                    hasPagesWarning = false,
                )
            } else it
        }
    }

    fun updateBook(){
        var state = _editBookDialogState.value
        with(state){
            if(this is EditBookDialogState.Visible){
                if(author.isBlank() || title.isBlank() || pages == null){
                    state = copy(
                        hasAuthorWarning = author.isBlank(),
                        hasTitleWarning = title.isBlank(),
                        hasPagesWarning = pages == null,
                    )
                } else{
                    viewModelScope.launch {
                        database.updateBook(
                            book = book,
                            title = title,
                            author = author,
                            pages = pages,
                            publishDate = publishedDate,
                        )
                    }
                    state = EditBookDialogState.Hidden
                }
                _editBookDialogState.update{state}
            }
        }
    }

    fun updatePagesRead(){
        val state = _viewBookDialogState.value
        with(state){
            if(this is ViewBookDialogState.Visible){
                viewModelScope.launch {
                    database.updatePagesRead(
                       book = book,
                        pagesRead = pagesRead
                    )
                }
                _viewBookDialogState.update{ ViewBookDialogState.Hidden}
            }
        }
    }

    fun archiveBook(book: Books){
        viewModelScope.launch {
            database.archive(book)
        }
    }
    fun favoriteBook(book: Books){
        viewModelScope.launch {
            database.favorite(book)
        }
    }








}