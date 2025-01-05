package com.example.interntest

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.window.Dialog

@Composable
fun FirstScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var palindrome by remember { mutableStateOf("") }
    var showPopup = remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val mContext = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(
            id = R.drawable.background_1),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_photo),
                contentDescription = "photo"
            )
            Spacer(modifier = modifier.height(50.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Name", color = Color.LightGray, fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = Color.Gray
                )
            )
            Spacer(modifier = modifier.height(15.dp))
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    palindrome = it.reversed()
                                },
                placeholder = { Text("Palindrome", color = Color.LightGray, fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = Color.Gray
                )
            )
            Spacer(modifier = modifier.height(50.dp))
            Button(
                onClick = {
                    if (text.isNotEmpty()) {
                        dialogMessage = if(text == palindrome) {
                            "${text} is palindrome"
                        } else {
                            "${text} is not palindrome"
                        }
                        showPopup.value = true
                    } else {
                        Toast.makeText(mContext, "Palindrome textfield can't be empty", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color(0xff2b637b)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Check", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = modifier.height(10.dp))
            Button(
                onClick = {
                    if(name.isNotEmpty()) {
                        navController.navigate("second/${name}/Selected User Name")
                    } else {
                        Toast.makeText(mContext, "Name can't be empty", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color(0xff2b637b)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Next", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }

    if (showPopup.value) {
        Dialog(onDismissRequest = { showPopup.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dialogMessage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xff5374a1)
                    )
                    Spacer(modifier = modifier.height(12.dp))
                    Button(
                        onClick = {
                            showPopup.value = false
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xff2b637b)),
                        shape = RoundedCornerShape(10.dp)
                    )
                    {
                        Text(text = "OK", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LandingPagePreview() {
    val navController = rememberNavController()
    FirstScreen(navController = navController)
}