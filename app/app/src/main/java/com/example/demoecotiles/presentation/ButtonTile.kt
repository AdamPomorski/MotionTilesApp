package com.example.demoecotiles.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.demoecotiles.R
import com.example.demoecotiles.data.TileType
import com.example.demoecotiles.ui.theme.Olive

@Composable
fun ButtonTile(
    iconId: Int = 0,
    isClickable: Boolean = true,
    onClick: () -> Unit,
    backgroundColor: Color,
    heightDp: Dp,
    cardShape: RoundedCornerShape = RoundedCornerShape(16.dp),
    widthDp: Dp,
    modifier: Modifier = Modifier
        .height(heightDp)
        .width(widthDp)
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = isClickable, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = cardShape
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if(iconId!=0) {
                val icon: Painter = painterResource(id = iconId)
                Icon(
                    modifier = Modifier
                        .size(50.dp),
                    painter = icon,
                    contentDescription = "Icon",
                )
            }
        }


    }


}

@Preview
@Composable
private fun ButtonTilePreview() {
    ButtonTile(iconId = R.drawable.lighting, backgroundColor = Olive, widthDp = 150.dp,
        heightDp = 150.dp, onClick = {})

}


