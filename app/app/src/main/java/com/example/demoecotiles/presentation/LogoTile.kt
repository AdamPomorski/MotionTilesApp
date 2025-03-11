package com.example.demoecotiles.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demoecotiles.R
import com.example.demoecotiles.ui.theme.DarkGreen
import com.example.demoecotiles.ui.theme.Olive
import com.example.demoecotiles.ui.theme.Yellowish

@Composable
fun LogoTile(
    widthDp: Dp,
    modifier: Modifier = Modifier
        .width(widthDp)
        .height(112.dp)


) {
    Card(
        modifier = modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = DarkGreen,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.Center,

            ) {
            Text(
                text = "Motion",
                fontSize = 18.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.align(Alignment.End),
                color = Color.White
            )
            Text(
                text = "Tiles",fontSize = 18.sp, textAlign = TextAlign.End, modifier = Modifier.align(Alignment.End),
                color = Yellowish
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,

            ) {
            Image(
                painter = painterResource(id = R.drawable.logo_dark_green),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.End)
            )
        }
    }


    }
}


}

@Preview
@Composable
private fun LogoTilePreview() {
    LogoTile(widthDp = 200.dp)

}