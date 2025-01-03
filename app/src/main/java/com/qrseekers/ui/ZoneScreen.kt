package com.qrseekers.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qrseekers.AppRoute
import com.qrseekers.R
import com.qrseekers.viewmodels.ZoneViewModel

@Composable
fun ZoneScreen(navController: NavController, zoneViewModel: ZoneViewModel) {
    val context = LocalContext.current
    Log.d("ZoneScreen", "Current zone: ${zoneViewModel.currentZone.value}")



    val zoneName = zoneViewModel.currentZone.value?.name ?: "Unknown Location"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // QRseekers Header
        Text(
            text = "QRseekers",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E88E5)
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image between QRseekers and Location Info
        Image(
            painter = painterResource(id = R.drawable.location_icon), // Replace with your image resource
            contentDescription = "Location Icon",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location Info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your next location:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add a small icon to indicate maps
            Icon(
                painter = painterResource(id = R.drawable.map_icon),
                contentDescription = "Open in Maps",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        val mapsIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(zoneName)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(mapIntent)
                    },
                tint = Color(0xFF1E88E5)
            )

            // Clickable location name with description
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                    Text(
                        text = zoneName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E88E5),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .drawBehind {
                                val underlineHeight = 2.dp.toPx()
                                val y = size.height
                                drawLine(
                                    color = Color(0xFF1E88E5),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = underlineHeight
                                )
                            }
                            .clickable {
                                val mapsIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(zoneName)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                context.startActivity(mapIntent)
                            }
                    )


                Spacer(modifier = Modifier.width(8.dp))

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap on the location name to open it in Maps.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        // todo: show hint


        Spacer(modifier = Modifier.height(24.dp))

        // Button
        Button(
            onClick = { navController.navigate(AppRoute.SCAN.route) }, // Navigate to ScanPage
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Text(
                text = "I'm at the location",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

