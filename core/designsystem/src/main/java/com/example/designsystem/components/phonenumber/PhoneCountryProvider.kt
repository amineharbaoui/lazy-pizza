package com.example.designsystem.components.phonenumber

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.designsystem.R

object PhoneCountryProvider {

    private const val DEFAULT_MASK = "XXXXXXXXXXXX"
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

    fun load(context: Context): List<PhoneCountryUi> {
        val res = context.resources

        val countryIsoCodes = res.getStringArray(R.array.country_codes_a_z)
        val countryNames = res.getStringArray(R.array.country_names_by_code)
        val countryDialingCodes = res.getStringArray(R.array.country_extensions_by_country_code)

        val count = minOf(countryIsoCodes.size, countryNames.size, countryDialingCodes.size)

        return buildList(count) {
            for (i in 0 until count) {
                val iso = countryIsoCodes[i].trim().uppercase()
                val name = countryNames[i].trim()
                val dialingCode = countryDialingCodes[i].trim()
                val dial = "+$dialingCode"

                val flagId = resolveFlagResId(context, iso)
                if (flagId == 0) continue

                val phoneMask = PHONE_MASKS_BY_ISO[iso] ?: DEFAULT_MASK

                add(
                    PhoneCountryUi(
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

    fun defaultCountry(
        context: Context,
        countries: List<PhoneCountryUi>,
    ): PhoneCountryUi {
        val config = context.resources.configuration
        val iso = config.locales[0]?.country?.uppercase()
        return countries.firstOrNull { it.isoCode == iso }
            ?: countries.firstOrNull { it.isoCode == "US" }
            ?: countries.first()
    }

    @Composable
    fun rememberPhoneCountriesWithDefault(): Pair<List<PhoneCountryUi>, PhoneCountryUi> {
        val context = LocalContext.current
        val countries = remember(context) { load(context) }
        val default = remember(context, countries) { defaultCountry(context, countries) }
        return countries to default
    }

    private fun resolveFlagResId(
        context: Context,
        iso: String,
    ): Int {
        val flagName = "flag_${iso.lowercase()}"
        return context.resources.getIdentifier(flagName, "drawable", context.packageName)
    }
}
