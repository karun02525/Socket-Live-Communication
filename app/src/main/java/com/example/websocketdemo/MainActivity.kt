package com.example.websocketdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.websocketdemo.ui.theme.WebSocketDemoTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainActivity : ComponentActivity() {

    private lateinit var webSocketListener: WebSocketListener
    private lateinit var viewModel: MainViewModel
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        webSocketListener = WebSocketListener(viewModel)



        setContent {
            WebSocketDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel,
                        connect = {
                            webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)
                        },
                        disConnect = {
                            webSocket?.close(1000,"Cancelled manually ")
                        },
                        send = {
                            webSocket?.send(it)
                            viewModel.setMessage(Pair(true,it))
                        }
                    )

                }
            }
        }
    }

    private fun createRequest(): Request {
        return Request.Builder()
            .url("wss://free.blr2.piesocket.com/v3/1?api_key=xkarunsouRPLUZ1kglzGqOmK9m3OQGuph5ATVqyutBpj&notify_self=1")
            .build()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    connect: () -> Unit,
    disConnect: () -> Unit,
    send: (String) -> Unit,
) {

    val context= LocalContext.current
    val status by viewModel.socketStatus.collectAsState()
    val mesasge by viewModel.message.collectAsState()

    var edit by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Row(

            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Button(onClick = {connect() }) {
                Text(text = "Connect")
            }
            Button(onClick = { disConnect()}) {
                Text(text = "Disconnect")
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = if (status) "Connected" else "Disconnected",
                fontSize = 20.sp,
                color = Color.Red
            )

            Text(
                text = "${if (mesasge.first) "You: " else "Other: "} ${mesasge.second}\n",
                fontSize = 20.sp,
                color = Color.Red
            )



            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {


                TextField(value = edit,
                    modifier = Modifier.weight(0.6f),
                    onValueChange = { edit = it },
                    placeholder = {
                        Text(text = "Enter message...")
                    }

                )

                Button(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(0.3f),
                    onClick = {
                        if(edit.isEmpty()){
                            Toast.makeText(context, "please enter message", Toast.LENGTH_SHORT).show()
                        }else {
                            send(edit)
                        }
                    }) {
                    Text(text = "Send")
                }
            }
        }


    }

}
