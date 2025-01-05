package com.example.interntest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.interntest.api.ApiConfig
import com.example.interntest.data.People
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun ThirdScreen(modifier: Modifier = Modifier, navController: NavController, userName: String) {
    var page by remember { mutableStateOf(1) }
    var people by remember { mutableStateOf(listOf<People>()) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val loadMore = {
        page++
    }

    LaunchedEffect(page) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = ApiConfig.apiService.getPeople(page, 10)
                if (response.isSuccessful) {
                    val newPeople = response.body()?.data ?: emptyList()
                    people = if (isRefreshing) newPeople else people + newPeople
                } else {
                    errorMessage = "Failed to fetch data: ${response.code()}"
                }
            } catch (e: IOException) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Third Screen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(15.dp))
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = Color.LightGray,
                thickness = 1.dp
            )
            SwipeRefresh(
                state = swipeRefresh,
                onRefresh = {
                    isRefreshing = true
                    page = 1
                    people = emptyList()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading && people.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    people.isEmpty() && !isLoading && errorMessage == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "No data available", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp)
                        ) {
                            items(people) { person ->
                                PeopleItem(person = person, navController = navController, userName = userName)
                            }
                            if (isLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                        LaunchedEffect(listState) {
                            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                                .map { visibleItems -> visibleItems.lastOrNull()?.index }
                                .distinctUntilChanged()
                                .collect { lastVisibleIndex ->
                                    if (lastVisibleIndex != null && lastVisibleIndex >= people.size - 1 && !isLoading && !isRefreshing) {
                                        loadMore()
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun PeopleItem(person: People, navController: NavController, userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                navController.navigate("second/${userName}/${person.first_name} ${person.last_name}")
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(person.avatar),
                contentDescription = "profile_pict",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(55.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "${person.first_name} ${person.last_name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = person.email ?: "No email", fontSize = 14.sp, color = Color.Gray)
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}



//@Preview
//@Composable
//private fun ThirdScreenPreview() {
//    val navController = rememberNavController()
//    ThirdScreen(navController = navController)
//}