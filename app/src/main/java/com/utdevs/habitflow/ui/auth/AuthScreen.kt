/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo UI de Autenticacion "Botanical FinTech":
 * Paso 1: "¿Ya tienes una cuenta? / Registrate" y SOLO 3 botones (Correo, Google, Telefono SMS).
 * Paso 2: Formulario especifico con boton para regresar a los 3 metodos.
 */
package com.utdevs.habitflow.ui.auth

import android.app.Activity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.R
import com.utdevs.habitflow.ui.theme.LocalForestColors

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity
    val colors = LocalForestColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.heroStart, colors.heroEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Color.White.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "HabitFlow",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "HabitFlow",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sprint Progresivo de 21 Dias contra la Procrastinacion",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Selector superior claro: Ya tienes cuenta? / Registrate
                    SecondaryTabRow(selectedTabIndex = if (state.isRegisterMode) 1 else 0) {
                        Tab(
                            selected = !state.isRegisterMode,
                            onClick = { viewModel.toggleMode(false) },
                            text = { Text("Ya tienes cuenta?") }
                        )
                        Tab(
                            selected = state.isRegisterMode,
                            onClick = { viewModel.toggleMode(true) },
                            text = { Text("Registrate") }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    when (state.selectedMethod) {
                        AuthMethod.NONE -> {
                            Text(
                                text = if (state.isRegisterMode) {
                                    "Elige como crear tu cuenta:"
                                } else {
                                    "Elige como iniciar sesion:"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Boton 1: Correo
                            Button(
                                onClick = { viewModel.selectMethod(AuthMethod.EMAIL) },
                                enabled = !state.isLoading,
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.terracottaCta,
                                    contentColor = colors.textOnAccent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continuar con Correo")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Boton 2: Google
                            OutlinedButton(
                                onClick = { activity?.let { viewModel.signInWithGoogle(it) } },
                                enabled = !state.isLoading && activity != null,
                                shape = RoundedCornerShape(28.dp),
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continuar con Google", color = colors.textPrimary)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Boton 3: Telefono SMS
                            OutlinedButton(
                                onClick = { viewModel.selectMethod(AuthMethod.PHONE) },
                                enabled = !state.isLoading,
                                shape = RoundedCornerShape(28.dp),
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = colors.tealSecondary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continuar con Telefono SMS", color = colors.textPrimary)
                            }
                        }

                        AuthMethod.EMAIL -> {
                            TextButton(onClick = viewModel::backToMethodChooser) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cambiar metodo")
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = state.email,
                                onValueChange = viewModel::onEmailChange,
                                label = { Text("Correo electronico") },
                                singleLine = true,
                                shape = RoundedCornerShape(28.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primaryAction,
                                    unfocusedBorderColor = colors.cardBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = state.password,
                                onValueChange = viewModel::onPasswordChange,
                                label = { Text("Contrasena (min. 6 caracteres)") },
                                singleLine = true,
                                shape = RoundedCornerShape(28.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primaryAction,
                                    unfocusedBorderColor = colors.cardBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = viewModel::submitEmail,
                                enabled = !state.isLoading,
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.terracottaCta,
                                    contentColor = colors.textOnAccent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(if (state.isRegisterMode) "Registrarme con Correo" else "Entrar con Correo")
                                }
                            }
                        }

                        AuthMethod.PHONE -> {
                            TextButton(onClick = viewModel::backToMethodChooser) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cambiar metodo")
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = state.phoneNumber,
                                onValueChange = viewModel::onPhoneChange,
                                label = { Text("Numero telefonico (E.164)") },
                                placeholder = { Text("+5215512345678") },
                                singleLine = true,
                                shape = RoundedCornerShape(28.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (state.verificationId != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = state.smsCode,
                                    onValueChange = viewModel::onSmsCodeChange,
                                    label = { Text("Codigo SMS (6 digitos)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(28.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (state.verificationId == null) {
                                        activity?.let { viewModel.sendPhoneCode(it) }
                                    } else {
                                        viewModel.verifyPhoneCode()
                                    }
                                },
                                enabled = !state.isLoading,
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.terracottaCta,
                                    contentColor = colors.textOnAccent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    if (state.verificationId == null) "Enviar Codigo SMS" else "Verificar y Entrar"
                                )
                            }
                        }
                    }

                    if (state.infoMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.infoMessage.orEmpty(),
                            color = colors.vibrantSuccess,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.errorMessage.orEmpty(),
                            color = colors.terracottaCta,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
