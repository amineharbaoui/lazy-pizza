package com.example.auth.ui.login

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.auth.domain.usecase.SignInWithSmsCodeUseCase
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.usecase.TransferGuestCartToUserUseCase
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneAuthViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val clock = Clock.fixed(NOW, ZoneOffset.UTC)
    private val authTestData = AuthTestData()
    private val cartTestData = CartTestData(clock)

    private val viewModel = PhoneAuthViewModel(
        signInWithSmsCode = SignInWithSmsCodeUseCase(authTestData.phoneAuthRepository),
        transferGuestCartToUserUseCase = TransferGuestCartToUserUseCase(cartTestData.cartRepository),
    )

    @Nested
    inner class UpdatePhone {

        @Test
        fun `updatePhone when entering the phone number then shows it trimmed`() = runTest {
            viewModel.uiState.test {
                // When
                viewModel.updatePhone(RAW_PHONE)

                // Then
                assertThat(expectEnterPhone().phoneNumber).isEqualTo(PHONE_LOCAL)
            }
        }

        @Test
        fun `updatePhone when entering the code then changes nothing`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                awaitItem()

                // When
                viewModel.updatePhone(OTHER_PHONE_LOCAL)

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class UpdateFullPhone {

        @Test
        fun `updateFullPhone when entering the phone number then updates the full phone number`() = runTest {
            viewModel.uiState.test {
                // When
                viewModel.updateFullPhone(FULL_PHONE)

                // Then
                assertThat(expectEnterPhone().fullPhoneNumber).isEqualTo(FULL_PHONE)
            }
        }

        @Test
        fun `updateFullPhone when entering the code then changes nothing`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                awaitItem()

                // When
                viewModel.updateFullPhone(OTHER_FULL_PHONE)

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class UpdateCountryIso {

        @Test
        fun `updateCountryIso when entering the phone number then updates the selected country`() = runTest {
            viewModel.uiState.test {
                // When
                viewModel.updateCountryIso(COUNTRY_ISO)

                // Then
                assertThat(expectEnterPhone().selectedCountryIso).isEqualTo(COUNTRY_ISO)
            }
        }

        @Test
        fun `updateCountryIso when entering the code then changes nothing`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                awaitItem()

                // When
                viewModel.updateCountryIso(OTHER_COUNTRY_ISO)

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class UpdatePhoneValidity {

        @Test
        fun `updatePhoneValidity when the phone number is valid then enables continue`() = runTest {
            viewModel.uiState.test {
                // When
                viewModel.updatePhoneValidity(true)

                // Then
                val state = expectEnterPhone()
                assertThat(state.isPhoneValid).isTrue()
                assertThat(state.continueEnabled).isTrue()
            }
        }

        @Test
        fun `updatePhoneValidity when entering the code then changes nothing`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                awaitItem()

                // When
                viewModel.updatePhoneValidity(false)

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class VerifyPhone {

        @Test
        fun `verifyPhone when the phone number is valid then emits start phone verification`() = runTest {
            // Given
            givenValidPhone()

            viewModel.events.test {
                // When
                viewModel.verifyPhone()
                runCurrent()

                // Then
                assertThat(awaitItem()).isEqualTo(PhoneAuthEvent.StartPhoneVerification(FULL_PHONE))
            }
        }

        @Test
        fun `verifyPhone when the phone number is valid then shows loading`() = runTest {
            // Given
            givenValidPhone()

            viewModel.uiState.test {
                // When
                viewModel.verifyPhone()

                // Then
                val state = expectEnterPhone()
                assertThat(state.isLoading).isTrue()
                assertThat(state.continueEnabled).isFalse()
                assertThat(state.skipEnabled).isFalse()
            }
        }

        @Test
        fun `verifyPhone when the phone number is not valid then emits nothing`() = runTest {
            // Given
            viewModel.updateFullPhone(FULL_PHONE)

            viewModel.events.test {
                // When
                viewModel.verifyPhone()
                runCurrent()

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class SkipAuth {

        @Test
        fun `skipAuth when called then emits skip auth`() = runTest {
            viewModel.events.test {
                // When
                viewModel.skipAuth()
                runCurrent()

                // Then
                assertThat(awaitItem()).isEqualTo(PhoneAuthEvent.SkipAuth)
            }
        }
    }

    @Nested
    inner class HandleCodeSent {

        @Test
        fun `handleCodeSent when the code is sent then shows the code step with the resend countdown`() = runTest {
            // Given
            givenValidPhone()
            viewModel.verifyPhone()

            viewModel.uiState.test {
                // When
                viewModel.handleCodeSent(VERIFICATION_ID)

                // Then
                val state = expectMostRecentItem()
                assertThat(state).isInstanceOf(PhoneAuthUiState.EnterCode::class.java)
                state as PhoneAuthUiState.EnterCode
                assertThat(state.phoneLocal).isEqualTo(PHONE_LOCAL)
                assertThat(state.fullPhoneNumber).isEqualTo(FULL_PHONE)
                assertThat(state.countryIso).isEqualTo(COUNTRY_ISO)
                assertThat(state.code).isEmpty()
                assertThat(state.isLoading).isFalse()
                assertThat(state.canResend).isFalse()
                assertThat(state.secondsRemaining).isEqualTo(RESEND_TIMEOUT_SECONDS)
                assertThat(state.subtitleRes).isEqualTo(R.string.auth_screen_subtitle_enter_code)
            }
        }

        @Test
        fun `handleCodeSent when a second passes then counts down`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                // When
                advanceTimeBy(1.seconds)
                runCurrent()

                // Then
                assertThat(expectEnterCode().secondsRemaining).isEqualTo(RESEND_TIMEOUT_SECONDS - 1)
            }
        }

        @Test
        fun `handleCodeSent when the countdown ends then enables resend`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                // When
                advanceUntilIdle()

                // Then
                val state = expectEnterCode()
                assertThat(state.secondsRemaining).isZero()
                assertThat(state.canResend).isTrue()
                assertThat(state.resendEnabled).isTrue()
            }
        }
    }

    @Nested
    inner class HandleVerificationFailure {

        @Test
        fun `handleVerificationFailure when entering the phone number then shows the error`() = runTest {
            // Given
            givenValidPhone()
            viewModel.verifyPhone()

            viewModel.uiState.test {
                // When
                viewModel.handleVerificationFailure(IllegalStateException(ERROR_MESSAGE))

                // Then
                val state = expectEnterPhone()
                assertThat(state.errorMessage).isEqualTo(ERROR_MESSAGE)
                assertThat(state.isLoading).isFalse()
            }
        }

        @Test
        fun `handleVerificationFailure when entering the code then shows the error`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                // When
                viewModel.handleVerificationFailure(IllegalStateException(ERROR_MESSAGE))

                // Then
                assertThat(expectEnterCode().errorMessage).isEqualTo(ERROR_MESSAGE)
            }
        }
    }

    @Nested
    inner class UpdateCode {

        @Test
        fun `updateCode when the input has other characters then keeps six digits`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                // When
                viewModel.updateCode(CODE_WITH_NON_DIGITS)

                // Then
                val state = expectEnterCode()
                assertThat(state.code).isEqualTo(SMS_CODE)
                assertThat(state.confirmEnabled).isTrue()
            }
        }

        @Test
        fun `updateCode when an error is shown then clears it`() = runTest {
            // Given
            givenEnterCodeStep()
            viewModel.handleVerificationFailure(IllegalStateException(ERROR_MESSAGE))

            viewModel.uiState.test {
                // When
                viewModel.updateCode(SMS_CODE)

                // Then
                assertThat(expectEnterCode().errorMessage).isNull()
            }
        }

        @Test
        fun `updateCode when entering the phone number then changes nothing`() = runTest {
            viewModel.uiState.test {
                awaitItem()

                // When
                viewModel.updateCode(SMS_CODE)

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class ConfirmCode {

        @Test
        fun `confirmCode when the code is too short then changes nothing`() = runTest {
            // Given
            givenEnterCodeStep()
            viewModel.updateCode(SHORT_CODE)

            viewModel.uiState.test {
                awaitItem()

                // When
                viewModel.confirmCode()
                runCurrent()

                // Then
                expectNoEvents()
            }
        }

        @Test
        fun `confirmCode when the code is right then emits auth completed`() = runTest {
            // Given
            givenSmsCodeEntered()

            viewModel.events.test {
                // When
                viewModel.confirmCode()
                runCurrent()

                // Then
                assertThat(awaitItem()).isEqualTo(PhoneAuthEvent.AuthCompleted)
            }
        }

        @Test
        fun `confirmCode when the code is right then moves the guest cart to the user`() = runTest {
            // Given
            cartTestData.givenCart(CartOwner.Guest, colaLine())
            givenSmsCodeEntered()

            viewModel.uiState.test {
                // When
                viewModel.confirmCode()
                runCurrent()

                // Then
                assertThat(cartTestData.cartLines(CartOwner.User(USER_ID))).containsExactly(colaLine())
                assertThat(cartTestData.cartLines(CartOwner.Guest)).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `confirmCode when the code is right then shows an empty phone step`() = runTest {
            // Given
            givenSmsCodeEntered()

            viewModel.uiState.test {
                // When
                viewModel.confirmCode()
                runCurrent()

                // Then
                val state = expectMostRecentItem()
                assertThat(state).isInstanceOf(PhoneAuthUiState.EnterPhone::class.java)
                state as PhoneAuthUiState.EnterPhone
                assertThat(state.phoneNumber).isEmpty()
                assertThat(state.fullPhoneNumber).isEmpty()
                assertThat(state.isPhoneValid).isFalse()
                assertThat(state.isLoading).isFalse()
            }
        }

        @Test
        fun `confirmCode when the code is wrong then shows the error`() = runTest {
            // Given
            givenSmsCodeEntered()
            authTestData.givenSignInFails(IllegalStateException(ERROR_MESSAGE))

            viewModel.uiState.test {
                // When
                viewModel.confirmCode()
                runCurrent()

                // Then
                val state = expectEnterCode()
                assertThat(state.errorMessage).isEqualTo(ERROR_MESSAGE)
                assertThat(state.isLoading).isFalse()
            }
        }

        @Test
        fun `confirmCode when the code is wrong then keeps the guest cart`() = runTest {
            // Given
            cartTestData.givenCart(CartOwner.Guest, colaLine())
            givenSmsCodeEntered()
            authTestData.givenSignInFails(IllegalStateException(ERROR_MESSAGE))

            viewModel.uiState.test {
                // When
                viewModel.confirmCode()
                runCurrent()

                // Then
                assertThat(cartTestData.cartLines(CartOwner.Guest)).containsExactly(colaLine())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class ResendCode {

        @Test
        fun `resendCode when resend is enabled then emits resend code`() = runTest {
            // Given
            givenResendEnabled()

            viewModel.events.test {
                // When
                viewModel.resendCode()
                runCurrent()

                // Then
                assertThat(awaitItem()).isEqualTo(PhoneAuthEvent.ResendCode(FULL_PHONE))
            }
        }

        @Test
        fun `resendCode when resend is enabled then restarts the countdown`() = runTest {
            // Given
            givenResendEnabled()

            viewModel.uiState.test {
                // When
                viewModel.resendCode()
                runCurrent()

                // Then
                val state = expectEnterCode()
                assertThat(state.canResend).isFalse()
                assertThat(state.secondsRemaining).isEqualTo(RESEND_TIMEOUT_SECONDS)
            }
        }

        @Test
        fun `resendCode when the countdown is running then emits nothing`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.events.test {
                // When
                viewModel.resendCode()
                runCurrent()

                // Then
                expectNoEvents()
            }
        }
    }

    @Nested
    inner class EditPhone {

        @Test
        fun `editPhone when entering the code then shows the phone step with the phone number`() = runTest {
            // Given
            givenEnterCodeStep()

            viewModel.uiState.test {
                // When
                viewModel.editPhone()

                // Then
                val state = expectMostRecentItem()
                assertThat(state).isInstanceOf(PhoneAuthUiState.EnterPhone::class.java)
                state as PhoneAuthUiState.EnterPhone
                assertThat(state.phoneNumber).isEqualTo(FULL_PHONE)
                assertThat(state.fullPhoneNumber).isEqualTo(FULL_PHONE)
                assertThat(state.selectedCountryIso).isEqualTo(COUNTRY_ISO)
                assertThat(state.isPhoneValid).isTrue()
                assertThat(state.isLoading).isFalse()
            }
        }
    }

    private fun givenValidPhone() {
        viewModel.updatePhone(PHONE_LOCAL)
        viewModel.updateFullPhone(FULL_PHONE)
        viewModel.updatePhoneValidity(true)
        viewModel.updateCountryIso(COUNTRY_ISO)
    }

    private fun TestScope.givenEnterCodeStep() {
        givenValidPhone()
        viewModel.verifyPhone()
        runCurrent()
        viewModel.handleCodeSent(VERIFICATION_ID)
    }

    private fun TestScope.givenSmsCodeEntered() {
        authTestData.givenSignInAs(USER_ID)
        givenEnterCodeStep()
        viewModel.updateCode(SMS_CODE)
    }

    private fun TestScope.givenResendEnabled() {
        givenEnterCodeStep()
        advanceUntilIdle()
    }

    private suspend fun ReceiveTurbine<PhoneAuthUiState>.expectEnterPhone(): PhoneAuthUiState.EnterPhone =
        expectMostRecentItem() as PhoneAuthUiState.EnterPhone

    private suspend fun ReceiveTurbine<PhoneAuthUiState>.expectEnterCode(): PhoneAuthUiState.EnterCode =
        expectMostRecentItem() as PhoneAuthUiState.EnterCode

    private fun colaLine() = CartItem.Other(
        lineId = COLA_LINE_ID,
        productId = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        quantity = COLA_QUANTITY,
        category = ProductCategory.DRINK,
        unitPrice = COLA_PRICE,
    )

    private companion object {
        val NOW: Instant = Instant.parse("2026-01-15T12:00:00Z")

        const val RAW_PHONE = "  5551234  "
        const val PHONE_LOCAL = "5551234"
        const val OTHER_PHONE_LOCAL = "5559999"
        const val FULL_PHONE = "+15551234"
        const val OTHER_FULL_PHONE = "+15559999"
        const val COUNTRY_ISO = "US"
        const val OTHER_COUNTRY_ISO = "FR"

        const val VERIFICATION_ID = "verification-id"
        const val SMS_CODE = "123456"
        const val CODE_WITH_NON_DIGITS = "1a2b3c4d5e6f7"
        const val SHORT_CODE = "12"
        const val RESEND_TIMEOUT_SECONDS = 60

        const val USER_ID = "user-id"
        const val ERROR_MESSAGE = "Incorrect code"

        const val COLA_LINE_ID = "line-cola"
        const val COLA_ID = "cola"
        const val COLA_NAME = "Cola"
        const val COLA_QUANTITY = 2
        const val COLA_PRICE = 1.99
    }
}
