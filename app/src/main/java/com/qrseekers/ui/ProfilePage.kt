package com.qrseekers.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qrseekers.AppRoute
import com.qrseekers.viewmodels.AuthViewModel
import com.qrseekers.R
import com.qrseekers.data.User
import com.qrseekers.viewmodels.AuthState
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream


@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    // Estados para almacenar los datos del usuario
    var email by remember { mutableStateOf("Loading...") }
    var participates by remember { mutableStateOf("None") }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Observa el estado de autenticación
    val authState by authViewModel.authState.observeAsState(AuthState.Loading)

    // Redirige a la pantalla de inicio de sesión si el usuario cierra sesión
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate(AppRoute.LOGIN.route) {
                popUpTo(AppRoute.PROFILE.route) { inclusive = true }
            }
        }
    }

    // Recuperar imagen desde Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        email = document.getString("email") ?: "No Email"
                        participates = document.getString("gameName")?.takeIf { it.isNotEmpty() } ?: "No active game"
                        profileImageBase64 = document.getString("profileImageBase64")
                    }
                }
                .addOnFailureListener {
                    email = "Error loading data"
                    participates = "Error loading data"
                }
        }
    }

    // Selector de imágenes
    val selectImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val base64String = encodeImageToBase64(it, context)
            if (base64String != null) {
                profileImageBase64 = base64String
                // Guardar en Firestore
                FirebaseFirestore.getInstance().collection("users").document(userId!!)
                    .update("profileImageBase64", base64String)
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra Superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF1E88E5)
                    )
                }
                Text(
                    text = "QRseekers",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del usuario
            Box(
                modifier = Modifier.size(120.dp)
            ) {
                if (profileImageBase64 != null) {
                    val bitmap = decodeBase64ToBitmap(profileImageBase64!!)
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logo_square),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
                IconButton(
                    onClick = { selectImageLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color(0xFF1E88E5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color(0xFF1E88E5)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = email,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = "Participates",
                            tint = Color(0xFF1E88E5)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Last game played:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Text(
                                text = participates,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Logout
            Button(
                onClick = {
                    authViewModel.signout() // Cerrar sesión
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Log Out",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



fun encodeImageToBase64(uri: Uri, context: android.content.Context): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun decodeBase64ToBitmap(base64: String): Bitmap? {
    return try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}



@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {

    val context = LocalContext.current
    val mockNavController = object : NavController(context) {} // Mock NavController
    val mockAuthViewModel = AuthViewModel() // Replace with a mock or real instance if available

    ProfilePage(
        navController = mockNavController,
        authViewModel = mockAuthViewModel
    )
}