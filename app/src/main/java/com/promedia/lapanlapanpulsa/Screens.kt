package com.promedia.lapanlapanpulsa

sealed class Screens (val screen: String){
    data object Home: Screens("home")
    data object  AboutMe:Screens("aboutme")
}