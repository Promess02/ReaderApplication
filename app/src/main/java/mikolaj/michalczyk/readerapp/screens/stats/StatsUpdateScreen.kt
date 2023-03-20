package mikolaj.michalczyk.readerapp.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import mikolaj.michalczyk.readerapp.components.ReaderAppBar
import mikolaj.michalczyk.readerapp.components.ReaderAppBarAlt
import mikolaj.michalczyk.readerapp.model.Item
import mikolaj.michalczyk.readerapp.model.MBook
import mikolaj.michalczyk.readerapp.navigation.ReaderScreens
import mikolaj.michalczyk.readerapp.screens.home.HomeScreenViewModel
import mikolaj.michalczyk.readerapp.screens.search.BookRow
import mikolaj.michalczyk.readerapp.utils.formatDate
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatsUpdateScreen(navController: NavController, viewModel: HomeScreenViewModel = hiltViewModel())
{
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    Scaffold(topBar = {
        ReaderAppBarAlt(title = "Book Stats", icon = Icons.Default.ArrowBack,
            navController = navController){
            navController.popBackStack()
        }
    }) {
        Surface {
            books = if(!viewModel.data.value.data.isNullOrEmpty()){
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            }else{
                emptyList()
            }
            Column {
                Row() {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(2.dp)) {
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "icon")
                    }
                    Text(text = "Hi, ${currentUser?.email.toString().split("@")[0].uppercase(Locale.ROOT)}")
                }
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                    shape = CircleShape,
                    elevation = 5.dp
                ) {
                    val readBooksList: List<MBook> = if(!viewModel.data.value.data.isNullOrEmpty()){
                        books.filter{ mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading !=null)
                        }
                    }else{
                        emptyList()
                    }

                    val readingBooks = books.filter{mbook ->
                        (mbook.startedReading != null && mbook.finishedReading ==null)
                    }

                    Column(modifier = Modifier.padding(start = 25.dp,top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.h5)
                        Divider()
                        Text(text = "You are reading: ${readingBooks.size} books")
                        Text(text = "You have read: ${readBooksList.size} books")

                    }

                }
                if(viewModel.data.value.loading == true){
                LinearProgressIndicator()
            }else{
                Divider()
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)){
                    val readBooks: List<MBook> = if(!viewModel.data.value.data.isNullOrEmpty()){
                        viewModel.data.value.data!!.filter{mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading!=null)
                        }
                    }else{
                        emptyList()
                    }
                    items(items = readBooks){ mBook ->
                        BookRowStats(book = mBook)

                    }
                }
            }
            }


            }

        
    }
}

@Composable
fun BookRowStats(
    book: MBook) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp) {
        Row(modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top) {

            val imageUrl = if(book.photoUrl.toString().isEmpty()) "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            else book.photoUrl.toString()
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "book image",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp),
            )

            Column {
                Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                Text(text =  "Author: ${book.authors.toString()}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption)

                Text(text =  "Started: ${formatDate(book.startedReading!!)}}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption)

                Text(text =  "Finished: ${formatDate(book.finishedReading!!)}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption)

            }

        }

    }

}