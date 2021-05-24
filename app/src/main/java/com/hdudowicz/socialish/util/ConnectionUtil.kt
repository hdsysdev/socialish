package com.hdudowicz.socialish.util

import java.net.InetAddress

/**
 * Static class for internet connection utility functions
 */
object ConnectionUtil {

    /**
     * Checks internet connection status by trying to get the IP address of google.com
     *
     * @return if the internet is reachable from the device
     */
    fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }
}