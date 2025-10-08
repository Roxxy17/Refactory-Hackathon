package com.example.kalanacommerce.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.ui.screen.auth.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {

        // Rute ke Welcome / First Screen
        composable(route = Screen.Welcome.route) {
            FirstScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // Rute ke Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
              onSignInSuccess = {
                    // Setelah login berhasil, arahkan ke Welcome
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // Rute ke Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                // Setelah registrasi berhasil, arahkan ke FillAccount
                onContinue = { navController.navigate(Screen.FillAccount.route) }
            )
        }

        // Rute ke Forgot Password Screen
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Rute ke Fill Account Screen
        composable(route = Screen.FillAccount.route) {
            FillAccountScreen(
                onNavigateBack = { navController.popBackStack() },
                onContinue = {
                    // TODO: Arahkan ke halaman utama aplikasi setelah mengisi akun
                    // Contoh: navController.navigate(Screen.Home.route) {
                    //     popUpTo(Screen.Welcome.route)
                    // }
                }
            )
        }
    }
}