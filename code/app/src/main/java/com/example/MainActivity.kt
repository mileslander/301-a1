package com.example.a301_a1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a301_a1.ui.theme._301a1Theme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val lengthRepository = RandomSequence()

        setContent {
            _301a1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        lengthRepository.lengths,
                        lengthRepository.evalHistory,
                        lengthRepository.gameHistory,
                        lengthRepository.guessHistory,
                        lengthRepository::generateSequence,
                        lengthRepository::evalAndStoreGuess,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    lengths: List<Int>,
    evalHistory: ArrayList<String>,
    gameHistory: ArrayList<Int>,
    guessHistory: ArrayList<Int>,
    generateSequence: (Int) -> Int,
    evalAndStoreGuess: (Int, Int) -> String,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    var isRowVisible by remember { mutableStateOf(true) }

    var generatedSequence by remember { mutableIntStateOf(-1) }

    var isHistoryVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()){

        Spacer(modifier = Modifier.height(25.dp))

        if (isRowVisible){
            LazyRow(modifier = modifier.fillMaxWidth()){
                itemsIndexed(lengths) { index, length ->
                    LengthColumn(
                        length = length,
                        modifier = Modifier.fillMaxWidth()
                            .selectable(selected = selectedIndex == index,
                            onClick  = { selectedIndex = index
                            }).background(
                            if (selectedIndex == index){
                                Color.Gray
                            }
                            else {
                                Color.Transparent
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(modifier = modifier.fillMaxWidth()){

                Spacer(modifier = Modifier.width(30.dp))

                Button(
                    onClick = {
                        if (selectedIndex >= 0) {

                            val sequenceLen: Int = lengths[selectedIndex]
                            generatedSequence = generateSequence(sequenceLen)

                            isRowVisible = !isRowVisible

                        }
                    }
                ) {
                    Text("Generate Sequence")
                }

                Spacer(modifier = Modifier.width(30.dp))

                Button(
                    onClick = {
                            isHistoryVisible = !isHistoryVisible
                    }
                ) {
                    Text("Show History")
                }
            }


            if (isHistoryVisible){

                if (evalHistory.isEmpty()) {
                    Text("No games played yet", modifier = Modifier.padding(12.dp))
                }

                else {

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {

                        items(evalHistory.size) { i ->
                            val sequence = gameHistory[i]
                            val guess = guessHistory[i]
                            val eval = evalHistory[i]

                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Game ${i + 1}", fontSize = 20.sp)
                                Text("Sequence: $sequence")
                                Text("Your guess: $guess")
                                Text(
                                    text = eval,
                                    color = if (eval == "Correct!") Color.Green else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!isRowVisible){

            GenerateScreen(generatedSequence, evalAndStoreGuess, onDismiss = { isRowVisible = true }, modifier)
        }
    }
}

@Composable
fun GenerateScreen(
    sequence: Int,
    evalAndStoreGuess: (Int, Int) -> String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    var finishedLoop by remember { mutableStateOf(false) }

    val sequenceInputState = rememberTextFieldState("")

    var receivedFeedback by remember { mutableStateOf(false) }

    var feedbackStr by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(40.dp))

        if (!finishedLoop){
            DisplaySequence(sequence, { finishedLoop = true })
        }

        if (finishedLoop){

            Column(modifier = modifier.fillMaxSize()){

                Row(modifier = modifier.fillMaxWidth()){

                    Spacer(modifier = Modifier.width(50.dp))

                    OutlinedTextField(
                        state = sequenceInputState,
                        label = { Text("Input Sequence") },
                        readOnly = false
                    )
                }

                Row(modifier = modifier.fillMaxWidth()) {

                    Spacer(modifier = Modifier.width(130.dp))

                    Button(modifier = Modifier.padding(vertical = 12.dp),
                        onClick = {
                            if (sequenceInputState.text.isNotBlank()){
                                feedbackStr = evalAndStoreGuess(sequence, sequenceInputState.text.toString().toInt())
                                receivedFeedback = true
                            }
                        }
                    ) {
                        Text("Submit Answer")
                    }
                }

                Row(modifier = modifier.fillMaxWidth()){

                    Spacer(modifier = Modifier.width(120.dp))

                    Button(
                        onClick = {
                            onDismiss()
                        }
                    ){
                        Text("Return to Main Screen")
                    }
                }

                if (receivedFeedback) {
                    Row(modifier = modifier.fillMaxWidth()){

                        Spacer(modifier = Modifier.width(160.dp))

                        Text(feedbackStr)

                    }
                }
            }
        }
    }
}

@Composable
fun DisplaySequence(
    sequence: Int,
    finishedLoop: () -> Unit,
    modifier: Modifier = Modifier
) {

    val sequenceOutput = rememberTextFieldState("")

    var sequenceCopy: Int = sequence

    var runLaunchedEffect by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(75.dp))

        Row(modifier = modifier.fillMaxWidth()){

            Spacer(modifier = Modifier.width(200.dp))

            Text(
                "${sequenceOutput.text}",
                fontSize = 27.sp)
        }

        Spacer(modifier = Modifier.height(70.dp))

        Row(modifier = modifier.fillMaxSize()){

            Spacer(modifier = Modifier.width(35.dp))

            Button(
                onClick = {
                    finishedLoop()
                }
            ){
                Text("Guess!")
            }

            Spacer(modifier = Modifier.width(35.dp))

            Button(
                onClick = {
                    runLaunchedEffect += 1
                }
            ){
                Text("Show Sequence Again")
            }
        }
    }

    LaunchedEffect(runLaunchedEffect) {
        val divide = 10

        while(sequenceCopy > 0){

            val display = sequenceCopy % divide

            sequenceOutput.edit {
                replace(
                    0,
                    length,
                    display.toString()
                )
            }

            delay(1.seconds)

            sequenceCopy /= divide
        }
    }
}


@Composable
fun LengthColumn(length: Int, modifier: Modifier = Modifier){
    Text(
        text = length.toString(),
        fontSize = 27.sp,
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)
    )
}
