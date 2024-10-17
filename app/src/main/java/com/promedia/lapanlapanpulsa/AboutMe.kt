package com.promedia.lapanlapanpulsa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promedia.lapanlapanpulsa.ui.theme.LapanLapanPulsaTheme

@Composable
fun AboutMe() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Gambar Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Lapan Lapan",
            modifier = Modifier.size(130.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Nama Perusahaan
        Text(
            text = "PT. ALOMOGADA MANDIRI NUSANTARA" ,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Deskripsi
        Text(
            text = stringResource(R.string.a2),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.a3),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Alamat
        Text(
            text = "Alamat Kantor Layanan:",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.a4),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )


        Spacer(modifier = Modifier.height(90.dp))

        // Footer
        Text(
            text = stringResource(R.string.nama_aboutme),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.versi),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.tahun),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

//buatkan preview
@Preview(showBackground = true)
@Composable
fun AboutMePreview() {
    LapanLapanPulsaTheme {
        AboutMe()
    }
}
