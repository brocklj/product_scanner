package cz.broc.productscanner.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.core.content.ContextCompat
import androidx.lifecycle.*

import cz.broc.productscanner.ListActivity
import cz.broc.productscanner.R
import cz.broc.productscanner.datasource.ListDataSource
import cz.broc.productscanner.models.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    @ApplicationContext var context: Context,
    val listDataSource: ListDataSource
): ViewModel() {
    val sp:SharedPreferences = context.getSharedPreferences("TelomarScanner", Context.MODE_PRIVATE)
    val userId: MutableStateFlow<String> = MutableStateFlow("")
    val userIdDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val sourceFilePath: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isQttyEditEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCheckDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var fileOpenReader: BufferedReader? = null

    lateinit var toneGenerator: ToneGenerator



    var listItems:  MutableStateFlow<Flow<List<ListItem>>> =
        MutableStateFlow(
            emptyFlow()
        )
    val listState: MutableStateFlow<LazyListState> = MutableStateFlow(LazyListState())

    val qttyDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val qttyAddDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val upcAddDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    var selectedItem: MutableStateFlow<ListItem?> = MutableStateFlow(null)


    val sourceFileErrorHandler: CoroutineExceptionHandler = CoroutineExceptionHandler() { _, exception ->
        Log.e("ListViewModel: sourceFileErrorHandler", exception.toString())
        loading.value = false
        viewModelScope.launch {
            if (!exception.message.isNullOrBlank()) {
                Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    val pathObserver = Observer<Uri?> {
            //TODO: Load Stream file
        if(it != null) {
            viewModelScope.launch(Dispatchers.IO + sourceFileErrorHandler) {
                loading.value = true
                loadFileIntoDb()
                listItems.value.firstOrNull()
                delay(400)
                sourceFilePath.value = null
                loading.value = false
            }
        }
    }

    init {
        userId.value = sp.getString("userId", "") ?: ""

        // TODO: Fix instantiation issues, act singleton-like
        // temporal fix  by:   val viewModel = (LocalContext.current as ListActivity).viewModel get only the local context instance of ListViewModel
        
            val scannerListener = this
            viewModelScope.launch(Dispatchers.IO) {

            }    

        // Forever observe file path changes
        sourceFilePath.asLiveData().observeForever(pathObserver)
        viewModelScope.launch(Dispatchers.IO) {
                listItems.value = listDataSource.getAllListItems()
                        listItems.value.firstOrNull()
                        delay(500)
                        loading.value = false
        }

        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
    }

    override fun onCleared() {
        sourceFilePath.asLiveData().removeObserver(pathObserver)

        super.onCleared()
    }

    //TODO: FIX path File resolving
    fun openFile(uri: Uri) {
        val path = uri.path
        if(!path?.endsWith(".csv", ignoreCase = true)!!) {
            //throw Throwable("Invalid file")
        }
        val file = File(uri.path?.split(":")?.get(1) ?: "")
        fileOpenReader = BufferedReader(FileReader(file))
    }

    fun setUserId(id: Int) {
        sp.edit().putString("userId", id.toString()).apply()
        userId.value = id.toString()
        userIdDialogOpen.value = false
    }

    fun loadFileIntoDb() {
        val handler: CoroutineExceptionHandler = CoroutineExceptionHandler() { _, exception ->
            when(exception) {
                is SQLiteConstraintException -> {
                    viewModelScope.launch {
                        Toast.makeText(context, "Chyba: Soubor obsahuje duplicity", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    viewModelScope.launch {
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                        // TODO: redirect to start screen
                    }
                }
            }
            Log.e("ListViewModel: loadFileIntoDb", exception.toString())
            fileOpenReader?.close()
            sourceFilePath.value = null
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            listDataSource.insertListItemIntoDbForCsvReader()
        }
    }

    fun onExportClick(listActity: Activity): Unit {
        viewModelScope.launch(Dispatchers.IO + sourceFileErrorHandler) {
            val l = listDataSource.getAllLScannedItems().firstOrNull()
            listDataSource.exportDbIntoCSV(context, userId.value, l!!)
                viewModelScope.launch {
                    Toast.makeText(context, "Export success", Toast.LENGTH_LONG).show()
                    showExportFinishedDialog(listActity as ListActivity)
            }
        }
    }

    fun onItemClick(item: ListItem) {
        qttyDialogOpen.value = true
        selectedItem.value = item
    }

    fun onNumberSelected(i: Int, j: String) {
        viewModelScope.launch(Dispatchers.IO) {
            listDataSource.setCount(selectedItem?.value?.ean!!, i)
            qttyDialogOpen.value = false
        }
    }

    fun onNumberAdded(i: Int, j: String) {
        val handler: CoroutineExceptionHandler = CoroutineExceptionHandler() { _, exception ->
            viewModelScope.launch {
                when(exception) {
                    is Resources.NotFoundException -> {
                        //TODO: Add a nasty "not found sound"
                        playNastySound()
                        Toast.makeText(context, context.getString(R.string.item_not_found), Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO+handler) {
            listDataSource.addCount(selectedItem?.value?.ean!!, i)
            qttyAddDialogOpen.value = false
            viewModelScope.launch {
                scrollToTop()
            }
        }
    }

    fun onNumberUpcAdded(i: Int, j: String) {
        val handler: CoroutineExceptionHandler = CoroutineExceptionHandler() { _, exception ->
            viewModelScope.launch {
                when(exception) {
                    is Resources.NotFoundException -> {
                        //TODO: Add a nasty "not found sound"
                        playNastySound()
                        Toast.makeText(context, context.getString(R.string.item_not_found), Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO+handler) {
            selectedItem.value = listDataSource.findOne(j)
            listDataSource.addCount(j, i)
            upcAddDialogOpen.value = false
            viewModelScope.launch {
                scrollToTop()
            }
        }
    }

    fun onDismissRequest() {

        qttyDialogOpen.value = false
    }


    private suspend fun scrollToTop() {
        //TODO: vylepšit - odscrolování po dokončení překlesení lazy listu
        delay(500)
        listState.value.scrollToItem(0, -90)
    }

    private fun showExportFinishedDialog(activity: ListActivity) {
        val builder = AlertDialog.Builder(activity)

        // Set the dialog title and message
        builder.setTitle("Upozornění")
        builder.setMessage("Data úspěšně exportována")

        // Add Cancel button
        builder.setPositiveButton("Zavřít") { dialog, which ->
            dialog.dismiss()
        }

        // Add Action 1 button
        builder.setNeutralButton("Vynulovat") { dialog, which ->
            viewModelScope.launch(Dispatchers.IO) {
                listDataSource.setAllZeroCount()
            }
        }

        // Add Action 2 button
        builder.setNegativeButton("Smazat") { dialog, which ->
            viewModelScope.launch(Dispatchers.IO) {
                listDataSource.deleteDb()
            }
        }

        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    fun playNastySound() {
        val longBeepDuration = 500 // Long beep duration in milliseconds
        //TODO
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, longBeepDuration * 5)
        val vibratingService = ContextCompat.getSystemService(context, Vibrator::class.java) as Vibrator
        vibratingService.vibrate(200)
    }

    fun onCheckTopBarMenuItemClick() {
        isCheckDialogOpen.value = true
    }
}