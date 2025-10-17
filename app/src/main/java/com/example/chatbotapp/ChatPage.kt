package com.example.chatbotapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import com.example.chatbotapp.ui.theme.Purple80
import com.example.chatbotapp.ui.theme.lightGreen

@Composable
fun ChatPage(modifier: Modifier, viewModel: ChatViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ChatAppBar()
        Divider(color = Color(0xFFE5E5E7), thickness = 1.dp)
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = viewModel.messageList
        )
        MessageInput(
            onMessageSend = { viewModel.sendMessage(it) }
        )
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(56.dp),
                painter = painterResource(R.drawable.baseline_question_answer_24),
                contentDescription = "Chat Icon",
                tint = Color(0xFFB9B9BB)
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "No messages yet\nSend a DM!",
                fontSize = 18.sp,
                color = Color(0xFF868B8E),
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(messageList.reversed()) {
                MessageRow(messageModel = it)
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isUser = messageModel.role != "model"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 1.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(19.dp))
                .background(
                    if (isUser) Color(0xFF3797EF) else Color(0xFFF1F1F2)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 260.dp)
        ) {
            Text(
                text = messageModel.Message,
                color = if (isUser) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var msg by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp, horizontal = 0.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF6F6F6))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE5E5EA),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TextField(
                        value = msg,
                        onValueChange = { msg = it },
                        placeholder = {
                            Text(
                                "Message…",
                                color = Color(0xFFACACB2),
                                fontSize = 16.sp
                            )
                        },
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF3797EF), CircleShape),
                    onClick = {
                        if (msg.isNotBlank()) {
                            onMessageSend(msg)
                            msg = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ChatAppBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color(0xFF54565B),
                modifier = Modifier.size(29.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Easy Bot",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color(0xFF979797),
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ChatAppBar()
}