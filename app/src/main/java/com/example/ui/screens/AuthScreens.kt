package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.domain.AuthSessionState
import com.example.domain.IndiaCity
import com.example.domain.LocationCatalog
import com.example.ui.components.ErrorBanner
import com.example.ui.components.FeedbackBanner
import com.example.ui.components.TopBackBar
import com.example.ui.theme.DiriddleTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isReady: Boolean,
    session: AuthSessionState,
    onNavigate: (destination: String) -> Unit,
) {
    val palette = DiriddleTheme.colors

    LaunchedEffect(isReady) {
        if (!isReady) return@LaunchedEffect
        delay(900)
        val target = when {
            !session.hasSeenOnboarding -> "onboarding"
            !session.isSignedIn -> "login"
            !session.ageVerified || !session.termsAccepted -> "age"
            else -> "main"
        }
        onNavigate(target)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 480.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.accentMuted)
                    .border(1.5.dp, palette.accent, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "d",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.accent,
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "diriddle",
                style = MaterialTheme.typography.displayLarge,
                color = palette.textPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Your city, unfiltered & anonymous.",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))
            OutlinedButton(
                onClick = { onNavigate("onboarding") },
                border = BorderStroke(1.dp, palette.border),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.testTag("splash_continue_button"),
            ) {
                Text("Continue", color = palette.textSecondary)
            }
        }
    }
}

private data class OnboardingSlide(
    val title: String,
    val body: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    val slides = remember {
        listOf(
            OnboardingSlide(
                title = "Zero Real Names. Ever.",
                body = "Get a fresh anonymous handle (like Anonymous #0421) per community. Nobody sees your phone or email.",
                icon = Icons.Outlined.Shield,
            ),
            OnboardingSlide(
                title = "Hyperlocal Across India",
                body = "Connect with your city, neighborhood, or campus—from Bengaluru traffic alerts to Delhi campus confessions.",
                icon = Icons.Outlined.LocationOn,
            ),
            OnboardingSlide(
                title = "Safe & Moderated 18+",
                body = "Built-in PII protection, instant blocking, and active moderation keep local conversations respectful.",
                icon = Icons.Outlined.FavoriteBorder,
            ),
        )
    }
    var step by remember { mutableIntStateOf(0) }
    val slide = slides[step]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "diriddle",
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
            )
            TextButton(
                onClick = onFinish,
                modifier = Modifier.testTag("onboarding_skip_button"),
            ) {
                Text("Skip", color = palette.textSecondary)
            }
        }

        Column(
            modifier = Modifier.widthIn(max = 560.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(20.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_city_night_banner_1794992839231),
                    contentDescription = "Indian city night skyline illustration",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, palette.card.copy(alpha = 0.85f)),
                            ),
                        ),
                )
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(palette.accentMuted)
                            .border(1.dp, palette.accentBorder, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = slide.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.widthIn(max = 560.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slides.indices.forEach { idx ->
                    val active = idx == step
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (active) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (active) palette.accent else palette.border),
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (step < slides.lastIndex) step += 1 else onFinish()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = palette.background,
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("onboarding_next_button"),
            ) {
                Text(
                    text = if (step == slides.lastIndex) "Get Started" else "Next",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onVerifyOtp: (otp: String, onError: (String) -> Unit) -> Unit,
    onQuickDemo: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    var mode by remember { mutableStateOf("phone") }
    var identifier by remember { mutableStateOf("+919876543210") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var resendSeconds by remember { mutableIntStateOf(0) }
    var infoMsg by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(resendSeconds) {
        if (resendSeconds > 0) {
            delay(1000)
            resendSeconds -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "diriddle",
                style = MaterialTheme.typography.titleMedium,
                color = palette.accent,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Private Sign-In",
                style = MaterialTheme.typography.displayLarge,
                color = palette.textPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Phone or email is only used to prevent spam. Your public identity is always anonymous.",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textSecondary,
            )
            Spacer(Modifier.height(18.dp))

            errorMsg?.let {
                ErrorBanner(message = it)
                Spacer(Modifier.height(12.dp))
            }
            infoMsg?.let {
                FeedbackBanner(message = it, isInfo = true)
                Spacer(Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ModeChip(
                    label = "Phone (India +91)",
                    active = mode == "phone",
                    onClick = {
                        mode = "phone"
                        identifier = "+919876543210"
                        otpSent = false
                        otp = ""
                        errorMsg = null
                        infoMsg = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("login_mode_phone"),
                )
                ModeChip(
                    label = "Email",
                    active = mode == "email",
                    onClick = {
                        mode = "email"
                        identifier = "tester@diriddle.in"
                        otpSent = false
                        otp = ""
                        errorMsg = null
                        infoMsg = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("login_mode_email"),
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = if (mode == "phone") "Mobile Number" else "Email Address",
                style = MaterialTheme.typography.labelLarge,
                color = palette.textSecondary,
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                placeholder = {
                    Text(
                        if (mode == "phone") "+919876543210" else "you@example.com",
                        color = palette.textMuted,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (mode == "phone") KeyboardType.Phone else KeyboardType.Email,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedBorderColor = palette.accent,
                    unfocusedBorderColor = palette.border,
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_identifier_input"),
            )

            Spacer(Modifier.height(16.dp))

            if (!otpSent) {
                Button(
                    onClick = {
                        errorMsg = null
                        if (identifier.trim().isEmpty()) {
                            errorMsg = "Please enter a valid phone number or email address."
                        } else {
                            otpSent = true
                            resendSeconds = 30
                            infoMsg = "Demo mode active: enter OTP 123456."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background,
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("send_otp_button"),
                ) {
                    Text("Send OTP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "6-Digit OTP",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    placeholder = { Text("123456", color = palette.textMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.surface,
                        unfocusedContainerColor = palette.surface,
                        focusedBorderColor = palette.accent,
                        unfocusedBorderColor = palette.border,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_otp_input"),
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        errorMsg = null
                        if (otp.trim().length < 4) {
                            errorMsg = "Please enter the 6-digit verification code."
                        } else {
                            onVerifyOtp(otp) { err -> errorMsg = err }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background,
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("verify_otp_button"),
                ) {
                    Text(
                        "Verify & Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        resendSeconds = 30
                        infoMsg = "Demo mode active: enter OTP 123456."
                    },
                    enabled = resendSeconds == 0,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = if (resendSeconds > 0) "Resend OTP in ${resendSeconds}s" else "Resend OTP",
                        color = palette.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Local Testing Helper",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Testing locally without SMS? Use OTP 123456 or launch the instant demo session below.",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Normal,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onQuickDemo,
                        border = BorderStroke(1.dp, palette.accentBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("demo_login_button"),
                    ) {
                        Text(
                            "Continue with Demo Test Account",
                            color = palette.accent,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (active) palette.accentMuted else palette.surface,
        border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (active) palette.accent else palette.textSecondary,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}

@Composable
fun AgeEligibilityScreen(
    onConfirm: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    var is18Plus by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val canContinue = is18Plus && acceptedTerms

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = palette.accentMuted,
                    border = BorderStroke(1.dp, palette.accentBorder),
                ) {
                    Text(
                        text = "18+ Community Gate",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.accent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Age & Safety Agreement",
                    style = MaterialTheme.typography.displayLarge,
                    color = palette.textPrimary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "diriddle is strictly for adults (18+) in India. Keeping communities safe starts with clear accountability.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.textSecondary,
                )
                Spacer(Modifier.height(20.dp))

                errorMsg?.let {
                    ErrorBanner(message = it)
                    Spacer(Modifier.height(12.dp))
                }

                CheckCard(
                    checked = is18Plus,
                    title = "I confirm that I am 18 years of age or older",
                    desc = "Required under Indian digital safety standards for anonymous social spaces.",
                    onToggle = { is18Plus = !is18Plus },
                    testTag = "age_18_checkbox",
                )
                Spacer(Modifier.height(12.dp))
                CheckCard(
                    checked = acceptedTerms,
                    title = "I accept the Terms, Privacy Policy & Community Rules",
                    desc = "No doxxing, harassment, hate speech, personal phone numbers, or non-consensual content.",
                    onToggle = { acceptedTerms = !acceptedTerms },
                    testTag = "terms_checkbox",
                )
            }

            Button(
                onClick = {
                    if (!canContinue) {
                        errorMsg = "Please confirm both eligibility requirements to proceed."
                    } else {
                        onConfirm()
                    }
                },
                enabled = canContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = palette.background,
                    disabledContainerColor = palette.accent.copy(alpha = 0.4f),
                    disabledContentColor = palette.background.copy(alpha = 0.6f),
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("age_confirm_button"),
            ) {
                Text("Confirm & Continue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CheckCard(
    checked: Boolean,
    title: String,
    desc: String,
    onToggle: () -> Unit,
    testTag: String,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (checked) palette.surface else palette.card,
        border = BorderStroke(1.dp, if (checked) palette.accent else palette.border),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onToggle)
            .testTag(testTag),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = if (checked) Icons.Filled.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                contentDescription = title,
                tint = if (checked) palette.accent else palette.textSecondary,
                modifier = Modifier.size(24.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun LocationSetupScreen(
    currentCityName: String,
    fromSettings: Boolean,
    onBack: (() -> Unit)? = null,
    onConfirmCity: (IndiaCity, Boolean?) -> Unit,
) {
    val palette = DiriddleTheme.colors
    val context = LocalContext.current
    var selected by remember(currentCityName) { mutableStateOf(LocationCatalog.findByName(currentCityName)) }
    var detectMessage by remember { mutableStateOf<String?>(null) }
    var permissionGranted by remember { mutableStateOf<Boolean?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val detected = LocationCatalog.detectLastKnownCity(context)
            permissionGranted = true
            if (detected != null) {
                selected = detected
                detectMessage = "Approximate city detected: ${detected.name}, ${detected.state}"
            } else {
                detectMessage = "Location enabled, but last-known fix unavailable. Pick your city manually below."
            }
        } else {
            permissionGranted = false
            detectMessage = "Approximate location unavailable or denied. Pick your city manually below."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (fromSettings && onBack != null) {
            TopBackBar(title = "Primary City", onBack = onBack)
        }

        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .weight(1f)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Choose Your City",
                style = MaterialTheme.typography.displayLarge,
                color = palette.textPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "We only use city-level location to show nearby communities—never your exact street or GPS coordinates.",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textSecondary,
            )
            Spacer(Modifier.height(14.dp))

            OutlinedButton(
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        val detected = LocationCatalog.detectLastKnownCity(context)
                        permissionGranted = true
                        if (detected != null) {
                            selected = detected
                            detectMessage = "Approximate city detected: ${detected.name}, ${detected.state}"
                        } else {
                            detectMessage = "No recent coarse location fix found. Pick your city manually below."
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                },
                border = BorderStroke(1.dp, palette.accentBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .background(palette.accentMuted, RoundedCornerShape(14.dp))
                    .testTag("detect_location_button"),
            ) {
                Icon(
                    imageVector = Icons.Outlined.NearMe,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Use Approximate Location (Optional)",
                    color = palette.accent,
                    fontWeight = FontWeight.Bold,
                )
            }

            detectMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                )
            }

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(LocationCatalog.indiaCities, key = { it.slug }) { city ->
                    val active = selected.slug == city.slug
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (active) palette.surface else palette.card,
                        border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selected = city }
                            .testTag("city_row_${city.slug}"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (active) palette.accent else palette.textPrimary,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                )
                                Text(
                                    text = city.state,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textSecondary,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            if (active) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = palette.accent,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(
            color = palette.background,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(20.dp),
            ) {
                Button(
                    onClick = { onConfirmCity(selected, permissionGranted) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background,
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("confirm_city_button"),
                ) {
                    Text(
                        text = "Continue in ${selected.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
