package com.yapp.buddycon.startup.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.yapp.buddycon.designsystem.R
import com.yapp.buddycon.designsystem.component.button.LoginButton
import com.yapp.buddycon.designsystem.theme.BuddyConTheme
import com.yapp.buddycon.designsystem.theme.Paddings
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import kotlin.coroutines.resume

private val LoginBannerDescription = "LoginBanner"
private val BuddyConLogoDescription = "BuddyConLogo"

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loginViewModel.loginResult.collect { loginResult ->
            if (loginResult) onNavigateToSignUp()
        }
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(BuddyConTheme.colors.background)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BuddyConTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(279f))
        Image(
            painter = painterResource(R.drawable.ic_full_logo),
            contentDescription = LoginBannerDescription,
            modifier = Modifier.size(97.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = stringResource(R.string.login_banner_description),
            style = BuddyConTheme.typography.body03,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(238f))
        LoginButton(
            modifier = Modifier
                .padding(bottom = Paddings.xlarge)
                .padding(horizontal = Paddings.xlarge)
                .fillMaxWidth()
        ) {
            coroutineScope.launch {
                val oauthToken = loginKakao(context)
                oauthToken?.let { token ->
                    UserApiClient.instance.me { user, error ->
                        user?.let {
                            it.kakaoAccount?.profile?.nickname?.let { nickname ->
                                loginViewModel.saveNickname(nickname)
                                loginViewModel.fetchLogin(
                                    oauthAccessToken = token.accessToken,
                                    nickname = nickname,
                                    age = it.kakaoAccount?.birthyear?.let {
                                        (Calendar.getInstance().get(Calendar.YEAR) - it.toInt()).toString()
                                    },
                                    email = it.kakaoAccount?.email,
                                    gender = it.kakaoAccount?.gender?.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun loginKakao(context: Context): OAuthToken? = suspendCancellableCoroutine { continuation ->
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            error?.let { e ->
                if (e is ClientError && e.reason == ClientErrorCause.Cancelled) return@loginWithKakaoTalk
                loginWithKakaoAccount(context, continuation)
            } ?: kotlin.run {
                continuation.resume(token)
            }
        }
    } else {
        loginWithKakaoAccount(context, continuation)
    }
}

private fun loginWithKakaoAccount(
    context: Context,
    continuation: CancellableContinuation<OAuthToken?>
) {
    UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
        error?.let {
            continuation.resume(null)
        } ?: kotlin.run {
            continuation.resume(token)
        }
    }
}
