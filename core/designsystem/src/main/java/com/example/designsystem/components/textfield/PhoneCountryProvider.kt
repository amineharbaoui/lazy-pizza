package com.example.designsystem.components.textfield

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.designsystem.R

object PhoneCountryProvider {

    private val PHONE_MASKS_BY_ISO: Map<String, String> = mapOf(
        // NANP countries
        "US" to "(XXX) XXX-XXXX",
        "CA" to "(XXX) XXX-XXXX",
        "PR" to "(XXX) XXX-XXXX",
        "DO" to "(XXX) XXX-XXXX",
        "TT" to "(XXX) XXX-XXXX",
        "JM" to "(XXX) XXX-XXXX",
        "BS" to "(XXX) XXX-XXXX",
        "AG" to "(XXX) XXX-XXXX",
        "AI" to "(XXX) XXX-XXXX",
        "BM" to "(XXX) XXX-XXXX",
        "BB" to "(XXX) XXX-XXXX",
        "GD" to "(XXX) XXX-XXXX",
        "KN" to "(XXX) XXX-XXXX",
        "LC" to "(XXX) XXX-XXXX",
        "VC" to "(XXX) XXX-XXXX",
        "VG" to "(XXX) XXX-XXXX",
        "VI" to "(XXX) XXX-XXXX",

        // Common international formats (indicative, simplified)
        "FR" to "X XX XX XX XX",
        "GB" to "XXXX XXX XXXX",
        "DE" to "XXXX XXXXXXX",
        "ES" to "XXX XXX XXX",
        "IT" to "XXX XXXX XXX",
        "BR" to "(XX) XXXXX-XXXX",
        "MX" to "XXX XXX XXXX",
        "AR" to "XX XXXX-XXXX",
        "CL" to "X XXXX XXXX",
        "CO" to "XXX XXX XXXX",
        "PE" to "XXX XXX XXX",
        "VE" to "XXXX-XXXXXXX",
        "RU" to "XXX XXX-XX-XX",
        "UA" to "XXX XXX XXXX",
        "TR" to "(XXX) XXX XX XX",
        "SA" to "XXX XXX XXXX",
        "AE" to "XXX XXX XXXX",
        "EG" to "XXX XXX XXXX",
        "ZA" to "XXX XXX XXXX",
        "NG" to "XXX XXX XXXX",
        "KE" to "XXX XXX XXX",
        "TZ" to "XXX XXX XXX",
        "IN" to "XXXXX-XXXXX",
        "PK" to "XXX-XXXXXXX",
        "BD" to "XXXXX-XXXXXX",
        "LK" to "XXX XXXX XXX",
        "ID" to "XXXX-XXXX-XXXX",
        "PH" to "XXXX XXX XXXX",
        "MY" to "XXX-XXXX XXXX",
        "SG" to "XXXX XXXX",
        "TH" to "XXX-XXX-XXXX",
        "VN" to "XXX XXXX XXX",
        "JP" to "XXX-XXXX-XXXX",
        "KR" to "XXX-XXXX-XXXX",
        "CN" to "XXX-XXXX-XXXX",
        "AU" to "XXXX XXX XXX",
        "NZ" to "XXX XXX XXXX",
        "MA" to "XXX-XXXXXX",
        "TN" to "XX XXX XXX",
        "DZ" to "XXX XX XX XX",
        "CM" to "XXXX XXXXXX",
        "ET" to "XXX XXX XXXX",
        "GH" to "XXX XXX XXX",
        "CI" to "XX XX XX XX",
    )

    fun load(context: Context): List<DsTextField.PhoneCountryUi> {
        val res = context.resources

        val iso2 = res.getStringArray(R.array.country_codes_a_z)
        val names = res.getStringArray(R.array.country_names_by_code)
        val extensions = res.getStringArray(R.array.country_extensions_by_country_code)

        val count = minOf(iso2.size, names.size, extensions.size)

        val pkg = context.packageName

        return buildList(count) {
            for (i in 0 until count) {
                val iso = iso2[i].trim().uppercase()
                val name = names[i].trim()
                val extRaw = extensions[i].trim()
                val dial = "+$extRaw"

                val flagName = "flag_${iso.lowercase()}"
                val flagId = res.getIdentifier(flagName, "drawable", pkg)

                if (flagId == 0) continue

                val phoneMask = PHONE_MASKS_BY_ISO[iso] ?: "XXXXXXXXXXXX"

                add(
                    DsTextField.PhoneCountryUi(
                        isoCode = iso,
                        name = name,
                        dialCode = dial,
                        flagResId = flagId,
                        phoneMask = phoneMask,
                    ),
                )
            }
        }
    }

    @Composable
    fun rememberPhoneCountries(): List<DsTextField.PhoneCountryUi> {
        val context = LocalContext.current
        return remember(context) { load(context) }
    }
}
