package com.example.kalanacommerce.presentation.screen.auth.terms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Terms & Conditions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // Tanggal Update
            Text(
                text = "Last updated: December 2025",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Intro
            Text(
                text = "Welcome to Kalana Commerce!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            TermsTextParagraph(
                "These terms and conditions outline the rules and regulations for the use of Kalana Commerce's Application. By accessing this app we assume you accept these terms and conditions. Do not continue to use Kalana Commerce if you do not agree to take all of the terms and conditions stated on this page."
            )

            TermsDivider()

            // Section 1
            TermsSectionTitle("1. License")
            TermsTextParagraph(
                "Unless otherwise stated, Kalana Commerce and/or its licensors own the intellectual property rights for all material on Kalana Commerce. All intellectual property rights are reserved. You may access this from Kalana Commerce for your own personal use subjected to restrictions set in these terms and conditions."
            )

            // Section 2
            TermsSectionTitle("2. User Accounts")
            TermsTextParagraph(
                "When you create an account with us, you must provide us information that is accurate, complete, and current at all times. Failure to do so constitutes a breach of the Terms, which may result in immediate termination of your account on our Service."
            )

            // Section 3
            TermsSectionTitle("3. Content Liability")
            TermsTextParagraph(
                "We shall not be hold responsible for any content that appears on your App. You agree to protect and defend us against all claims that is rising on your App. No link(s) should appear on any Website that may be interpreted as libelous, obscene or criminal, or which infringes, otherwise violates, or advocates the infringement or other violation of, any third party rights."
            )

            // Section 4
            TermsSectionTitle("4. Your Privacy")
            TermsTextParagraph(
                "Please read our Privacy Policy. We handle your data with care and use industry-standard encryption to protect your personal information."
            )

            TermsDivider()

            // Footer
            Text(
                text = "If you have any questions about these Terms, please contact us at support@kalanacommerce.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

// --- Helper Components untuk Konsistensi ---

@Composable
fun TermsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun TermsTextParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun TermsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 24.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun TermsPreviewLight() {
    KalanaCommerceTheme(darkTheme = false) {
        TermsAndConditionsScreen(onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TermsPreviewDark() {
    KalanaCommerceTheme(darkTheme = true) {
        TermsAndConditionsScreen(onBack = {})
    }
}